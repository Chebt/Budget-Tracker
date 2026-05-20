package com.chebet.budget_tracker.controller;

import com.chebet.budget_tracker.entity.Transaction;
import com.chebet.budget_tracker.service.BudgetService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.YearMonth;

@Controller
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping("/")
    public String dashboard(@RequestParam(required = false) String month, Model model) {
        YearMonth currentMonth = (month != null && !month.isEmpty()) 
                ? YearMonth.parse(month) 
                : YearMonth.now();

        model.addAttribute("summary", budgetService.getBudgetSummaryForMonth(currentMonth));
        model.addAttribute("transactions", budgetService.getTransactionsForMonth(currentMonth));
        model.addAttribute("currentMonth", currentMonth);
        model.addAttribute("prevMonth", currentMonth.minusMonths(1));
        model.addAttribute("nextMonth", currentMonth.plusMonths(1));
        
        return "dashboard";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("transaction", new Transaction());
        return "add-transaction";
    }

    @PostMapping("/add")
    public String addTransaction(@ModelAttribute Transaction transaction) {
        if (transaction.getDate() == null) {
            transaction.setDate(LocalDate.now());
        }
        budgetService.save(transaction);
        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Transaction transaction = budgetService.findById(id);
        model.addAttribute("transaction", transaction);
        return "add-transaction";
    }

    @GetMapping("/delete/{id}")
    public String deleteTransaction(@PathVariable Long id) {
        budgetService.deleteById(id);
        return "redirect:/";
    }

    // Export to CSV
    @GetMapping("/export")
    public String exportToCsv(@RequestParam(required = false) String month) {
        
        return "redirect:/";
    }
}