import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import 'dart:convert';

import 'package:starbucks_customer_client/balance_page.dart';

class PaymentPage extends StatefulWidget {
  const PaymentPage({super.key});

  @override
  PaymentPageState createState() => PaymentPageState();
}

class PaymentPageState extends State<PaymentPage> {
  final Future<SharedPreferences> _prefs = SharedPreferences.getInstance();
  String? _cardNum;
  String regId = "5012349";

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Payment'),
        actions: [
          IconButton(
            icon: const Icon(Icons.account_balance),
            onPressed: _navigateToBalance,
          ),
        ],
      ),
      body: Container(
        padding: const EdgeInsets.all(20),
        child: Column(
          children: [
            const Image(
              image: AssetImage('assets/images/barcode.png'),
            ),
            const SizedBox(height: 20),
            ElevatedButton(
              onPressed: _makePayment,
              child: const Text('Pay'),
            ),
          ],
        ),
      ),
    );
  }

  void _navigateToBalance() {
    // Navigate to the Balance page
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => const BalancePage(),
      ),
    );
  }

  void _makePayment() async {
    final SharedPreferences prefs = await _prefs;
    _cardNum = prefs.getString('cardNum');
    var url =
        'http://${dotenv.env['HOST']}/order/register/$regId/pay/$_cardNum?apikey=${dotenv.env['API_KEY']}';

    var response = await http.post(Uri.parse(url));
    debugPrint(response.body);
    if (response.statusCode == 200) {
      var body = json.decode(response.body);
      var balance = body['balance'];
      if (!mounted) return;
      ScaffoldMessenger.of(context).removeCurrentSnackBar();
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('Succeed! Remaining balance:$balance'),
        ),
      );
    } else {
      if (!mounted) return;
      ScaffoldMessenger.of(context).removeCurrentSnackBar();
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Failed to make payment'),
        ),
      );
    }
  }
}
