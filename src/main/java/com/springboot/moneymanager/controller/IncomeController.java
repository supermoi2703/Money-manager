package com.springboot.moneymanager.controller;

import com.springboot.moneymanager.dto.ExpenseDTO;
import com.springboot.moneymanager.dto.IncomeDTO;
import com.springboot.moneymanager.service.IncomeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/incomes")
public class IncomeController {
    private final IncomeService incomeService;

    @PostMapping("/add")
    public ResponseEntity<IncomeDTO> addExpense(@RequestBody IncomeDTO incomeDTO) {
        IncomeDTO createdIncome = incomeService.addIncome(incomeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdIncome);
    }
}
