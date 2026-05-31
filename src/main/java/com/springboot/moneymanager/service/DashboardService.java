package com.springboot.moneymanager.service;

import com.springboot.moneymanager.dto.ExpenseDTO;
import com.springboot.moneymanager.dto.IncomeDTO;
import com.springboot.moneymanager.dto.RecentTransactionDTO;
import com.springboot.moneymanager.entity.ProfileEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Stream.concat;

@Service
@RequiredArgsConstructor
public class DashboardService {
    public final ExpenseService expenseService;
    public final IncomeService incomeService;
    public final ProfileService profileService;

    public Map<String, Object> getDashboardData() {
        ProfileEntity profile = profileService.getCurrentProfile();

        Map<String,Object> returnValue = new LinkedHashMap<>();
        List<ExpenseDTO> lastestExpenses = expenseService.getLastest5ExpensesForCurrentUser();
        List<IncomeDTO> lastestIncomes = incomeService.getLastest5IncomesForCurrentUser();
        List<RecentTransactionDTO> recentTransactions = concat(
                lastestIncomes.stream().map(income ->
                        RecentTransactionDTO.builder()
                                .id(income.getId())
                                .name(income.getName())
                                .amount(income.getAmount())
                                .date(income.getDate())
                                .createdAt(income.getCreatedAt())
                                .updatedAt(income.getUpdatedAt())
                                .type("income")
                                .build()),
                lastestExpenses.stream().map(expense ->
                        RecentTransactionDTO.builder()
                                .id(expense.getId())
                                .name(expense.getName())
                                .amount(expense.getAmount())
                                .date(expense.getDate())
                                .createdAt(expense.getCreatedAt())
                                .updatedAt(expense.getUpdatedAt())
                                .type("expense")
                                .build()))
                .sorted((RecentTransactionDTO a, RecentTransactionDTO b) -> {
                      int cmp = b.getDate().compareTo(a.getDate());
                      if (cmp == 0 && a.getCreatedAt() != null && b.getCreatedAt() != null) {
                          return b.getCreatedAt().compareTo(a.getCreatedAt());
                      }
                      return cmp;
        }).collect(Collectors.toList());

        returnValue.put("totalBalance", incomeService.getTotalIncomeForCurrentUser()
                .subtract(expenseService.getTotalExpenseForCurrentUser()));
        returnValue.put("totalIncome", incomeService.getTotalIncomeForCurrentUser());
        returnValue.put("totalExpense", expenseService.getTotalExpenseForCurrentUser());
        returnValue.put("recent5Expenses", lastestExpenses);
        returnValue.put("recent5Incomes", lastestIncomes);
        returnValue.put("recentTransactions", recentTransactions);
        return returnValue;
    }
}
