import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'dart:developer' as developer;

import 'package:shared_preferences/shared_preferences.dart';

class BalancePage extends StatefulWidget {
  const BalancePage({super.key});

  @override
  BalancePageState createState() => BalancePageState();
}

class BalancePageState extends State<BalancePage> {
  final Future<SharedPreferences> _prefs = SharedPreferences.getInstance();
  String? _cardNum;
  String? _cardCode;
  Map<String, dynamic>? _responseData;

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Balance'),
      ),
      body: Container(
        padding: const EdgeInsets.all(20),
        child: _responseData == null
            ? Column(
                children: [
                  const Center(
                    child: CircularProgressIndicator(),
                  ),
                  ElevatedButton(
                    onPressed: _getNewCard,
                    child: const Text('Get A New Card'),
                  ),
                ],
              )
            : Center(
                child: Column(
                  children: [
                    Text('Card number: ${_responseData?['cardNumber']}'),
                    Text('Card code: ${_responseData?['cardCode']}'),
                    Text('Balance: ${_responseData?['balance']}'),
                    Text('Activated: ${_responseData?['activated']}'),
                    Text('Status: ${_responseData?['status']}'),
                    const SizedBox(height: 20),
                    ElevatedButton(
                      onPressed: _loadData,
                      child: const Text('Refresh Card Info'),
                    ),
                    ElevatedButton(
                      onPressed: _getNewCard,
                      child: const Text('Get A New Card'),
                    ),
                    ElevatedButton(
                      onPressed: _activateCard,
                      child: const Text('Activate Card'),
                    ),
                  ],
                ),
              ),
      ),
    );
  }

  void _loadData() async {
    final SharedPreferences prefs = await _prefs;
    _cardNum = prefs.getString('cardNum');
    _cardCode = prefs.getString('cardCode');
    debugPrint("Current card number: $_cardNum");
    debugPrint("Current card code: $_cardCode");
    String msg = "Card info refreshed";
    var url =
        'http://${dotenv.env['HOST']}/card/$_cardNum?apikey=${dotenv.env['API_KEY']}';
    var response = await http.get(Uri.parse(url));
    if (response.statusCode == 200) {
      var body = json.decode(response.body);
      setState(() => _responseData = body);
    } else {
      msg = "Failed to refresh card! Please try again or get a new one.";
    }
    if (!mounted) return;
    ScaffoldMessenger.of(context).removeCurrentSnackBar();
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(msg),
      ),
    );
  }

  void _getNewCard() async {
    final SharedPreferences prefs = await _prefs;
    String msg = "You get a new card successfully.";
    var url =
        'http://${dotenv.env['HOST']}/cards?apikey=${dotenv.env['API_KEY']}';
    var response = await http.post(Uri.parse(url));
    debugPrint(response.body);
    if (response.statusCode == 201) {
      var body = json.decode(response.body);
      setState(() => _responseData = body);

      if (!_responseData!.containsKey('cardNumber') ||
          !_responseData!.containsKey('cardCode')) {
        msg = "Internal server error";
      }

      _cardNum = _responseData?['cardNumber'];
      _cardCode = _responseData?['cardCode'];
      prefs.setString('cardNum', _cardNum ?? '');
      prefs.setString('cardCode', _cardCode ?? '');
    } else {
      msg = 'Unknown error. Please try again';
    }

    if (!mounted) return;
    ScaffoldMessenger.of(context).removeCurrentSnackBar();
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(msg),
      ),
    );
  }

  void _activateCard() async {
    String msg = "Card is activated";
    var url =
        'http://${dotenv.env['HOST']}/card/activate/$_cardNum/$_cardCode/?apikey=${dotenv.env['API_KEY']}';
    var response = await http.post(Uri.parse(url));
    if (response.statusCode == 200) {
      var body = json.decode(response.body);
      setState(() => _responseData = body);
      // _cardNum = _responseData['cardNumber'];
    } else {
      msg = "Failed to activate card!";
    }

    if (!mounted) return;
    ScaffoldMessenger.of(context).removeCurrentSnackBar();
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(msg),
      ),
    );
  }
}
