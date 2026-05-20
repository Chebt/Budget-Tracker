package com.chebet.budget_tracker.repository;


import com.chebet.budget_tracker.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByDateBetween(LocalDate start, LocalDate end);

    void deleteById(Long id);

    List<Transaction> findAllByOrderByDateDesc();
}