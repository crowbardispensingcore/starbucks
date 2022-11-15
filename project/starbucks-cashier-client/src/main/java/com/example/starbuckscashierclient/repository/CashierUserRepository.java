package com.example.starbuckscashierclient.repository;

import com.example.starbuckscashierclient.model.CashierUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CashierUserRepository extends JpaRepository<CashierUser, Long> {
    CashierUser findCashierUserByUsername(String username);
}
