package com.chebet.budget_tracker.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    private Double amount;

    private String category;   // Needs, Wants, Savings, Other

    private LocalDate date;

    private String type;       // INCOME or EXPENSE
}