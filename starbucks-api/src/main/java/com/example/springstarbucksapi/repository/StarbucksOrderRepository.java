package com.example.springstarbucksapi.repository;

/* https://docs.spring.io/spring-data/jpa/docs/2.4.6/reference/html/#repositories */

import com.example.springstarbucksapi.model.StarbucksOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StarbucksOrderRepository extends JpaRepository<StarbucksOrder, Long> {
    Optional<StarbucksOrder> findStarbucksOrderByRegisterAndStatus(String register, String status);

    Optional<StarbucksOrder> findTop1ByRegisterOrderByIdDesc(String register);
}


