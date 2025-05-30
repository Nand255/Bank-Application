package com.ldrp.bankapplication.repository;

import com.ldrp.bankapplication.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction,String> {
    // first option requires no methods

    // the second option which is straightforward and good
    /*1. Used Spring Data JPA's method naming convention to create a query that:
    - Filters by account number
    - Finds transactions where `createdAt` is between start and end dates (inclusive)
    - Orders results by created date in descending order (most recent first)*/

    List<Transaction> findByAccountNumberAndCreatedAtBetweenOrderByCreatedAtDesc(String accountNumber, LocalDate startDate, LocalDate endDate);
}
