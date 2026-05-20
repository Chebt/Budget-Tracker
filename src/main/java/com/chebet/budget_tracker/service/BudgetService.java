package com.chebet.budget_tracker.service;

import com.chebet.budget_tracker.entity.Transaction;
import com.chebet.budget_tracker.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BudgetService {

    private final TransactionRepository repository;

    public BudgetService(TransactionRepository repository) {
        this.repository = repository;
    }

    public Transaction save(Transaction transaction) {
        return repository.save(transaction);
    }

    public List<Transaction> getCurrentMonthTransactions() {
        YearMonth current = YearMonth.now();
        LocalDate start = current.atDay(1);
        LocalDate end = current.atEndOfMonth();
        return repository.findByDateBetween(start, end);
    }

    public Map<String, Object> getBudgetSummary() {
        List<Transaction> transactions = getCurrentMonthTransactions();

        double totalIncome = transactions.stream()
                .filter(t -> "INCOME".equalsIgnoreCase(t.getType()))
                .mapToDouble(Transaction::getAmount)
                .sum();

        double totalExpenses = transactions.stream()
                .filter(t -> "EXPENSE".equalsIgnoreCase(t.getType()))
                .mapToDouble(Transaction::getAmount)
                .sum();

        double balance = totalIncome - totalExpenses;

        double needsActual = getCategoryTotal(transactions, "Needs");
        double wantsActual = getCategoryTotal(transactions, "Wants");
        double savingsActual = getCategoryTotal(transactions, "Savings");

        double needsTarget = totalIncome * 0.5;
        double wantsTarget = totalIncome * 0.3;
        double savingsTarget = totalIncome * 0.2;

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalIncome", Math.round(totalIncome * 100.0) / 100.0);
        summary.put("totalExpenses", Math.round(totalExpenses * 100.0) / 100.0);
        summary.put("balance", Math.round(balance * 100.0) / 100.0);

        summary.put("needsTarget", Math.round(needsTarget * 100.0) / 100.0);
        summary.put("wantsTarget", Math.round(wantsTarget * 100.0) / 100.0);
        summary.put("savingsTarget", Math.round(savingsTarget * 100.0) / 100.0);

        summary.put("needsActual", Math.round(needsActual * 100.0) / 100.0);
        summary.put("wantsActual", Math.round(wantsActual * 100.0) / 100.0);
        summary.put("savingsActual", Math.round(savingsActual * 100.0) / 100.0);

        // === Warnings ===
        StringBuilder warnings = new StringBuilder();

        if (balance < 0) {
            warnings.append("CRITICAL: Overall spending exceeds income!\n");
        }
        if (needsActual > needsTarget) {
            warnings.append("WARNING: Needs exceeded 50% limit!\n");
        }
        if (wantsActual > wantsTarget) {
            warnings.append("WARNING: Wants exceeded 30% limit!\n");
        }
        if (savingsActual < savingsTarget * 0.8) {
            warnings.append("WARNING: Savings below target!\n");
        }

        if (warnings.length() > 0) {
            summary.put("warning", warnings.toString().trim());
        } else {
            summary.put("warning", null);
        }

        return summary;
    }

    private double getCategoryTotal(List<Transaction> transactions, String category) {
        return transactions.stream()
                .filter(t -> "EXPENSE".equalsIgnoreCase(t.getType()) && category.equals(t.getCategory()))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public Transaction findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    // Get transactions for a specific month
    public List<Transaction> getTransactionsForMonth(YearMonth yearMonth) {
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();
        return repository.findByDateBetween(start, end);
    }

    public Map<String, Object> getBudgetSummaryForMonth(YearMonth yearMonth) {
        List<Transaction> transactions = getTransactionsForMonth(yearMonth);

        double totalIncome = transactions.stream()
                .filter(t -> "INCOME".equalsIgnoreCase(t.getType()))
                .mapToDouble(Transaction::getAmount)
                .sum();

        double totalExpenses = transactions.stream()
                .filter(t -> "EXPENSE".equalsIgnoreCase(t.getType()))
                .mapToDouble(Transaction::getAmount)
                .sum();

        double balance = totalIncome - totalExpenses;

        double needsActual = getCategoryTotal(transactions, "Needs");
        double wantsActual = getCategoryTotal(transactions, "Wants");
        double savingsActual = getCategoryTotal(transactions, "Savings");

        double needsTarget = totalIncome * 0.5;
        double wantsTarget = totalIncome * 0.3;
        double savingsTarget = totalIncome * 0.2;

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalIncome", Math.round(totalIncome * 100.0) / 100.0);
        summary.put("balance", Math.round(balance * 100.0) / 100.0);
        summary.put("needsTarget", Math.round(needsTarget * 100.0) / 100.0);
        summary.put("wantsTarget", Math.round(wantsTarget * 100.0) / 100.0);
        summary.put("savingsTarget", Math.round(savingsTarget * 100.0) / 100.0);
        summary.put("needsActual", Math.round(needsActual * 100.0) / 100.0);
        summary.put("wantsActual", Math.round(wantsActual * 100.0) / 100.0);
        summary.put("savingsActual", Math.round(savingsActual * 100.0) / 100.0);

        // === Warnings ===
        StringBuilder warnings = new StringBuilder();

        if (balance < 0) {
            warnings.append("CRITICAL: Overall spending exceeds income!\n");
        }
        if (needsActual > needsTarget) {
            warnings.append("WARNING: Needs exceeded 50% limit!\n");
        }
        if (wantsActual > wantsTarget) {
            warnings.append("WARNING: Wants exceeded 30% limit!\n");
        }
        if (savingsActual < savingsTarget * 0.8) {
            warnings.append("WARNING: Savings below target!\n");
        }

        if (warnings.length() > 0) {
            summary.put("warning", warnings.toString().trim());
        } else {
            summary.put("warning", null);
        }

        return summary;
    }
}