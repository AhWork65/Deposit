package com.heydari.deposit.repository;


import com.heydari.deposit.model.customer.Customer;
import com.heydari.deposit.model.deposit.Deposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepositRepository extends JpaRepository<Deposit, Long> {

    Deposit findDepositByNumber(String number);
    List<Deposit> findAllByCustomer(Customer customer);
    Boolean existsByCustomer(Customer customer);
    Deposit findByNumber(String number);
}
