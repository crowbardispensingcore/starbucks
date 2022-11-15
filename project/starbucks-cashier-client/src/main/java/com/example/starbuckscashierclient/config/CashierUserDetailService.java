package com.example.starbuckscashierclient.config;

import com.example.starbuckscashierclient.model.CashierUser;
import com.example.starbuckscashierclient.repository.CashierUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CashierUserDetailService implements UserDetailsService {

    @Autowired
    private CashierUserRepository cashierUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final CashierUser cashierUser = cashierUserRepository.findCashierUserByUsername(username);
        if (cashierUser == null) {
            throw new UsernameNotFoundException(username);
        }
        UserDetails userDetails = User
                .withUsername(cashierUser.getUsername())
                .password(cashierUser.getPassword())
                .authorities("USER")
                .build();

        return userDetails;
    }
}
