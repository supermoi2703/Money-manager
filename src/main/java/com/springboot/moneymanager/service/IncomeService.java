package com.springboot.moneymanager.service;

import com.springboot.moneymanager.dto.ExpenseDTO;
import com.springboot.moneymanager.dto.IncomeDTO;
import com.springboot.moneymanager.entity.CategoryEntity;
import com.springboot.moneymanager.entity.ExpenseEntity;
import com.springboot.moneymanager.entity.IncomeEntity;
import com.springboot.moneymanager.entity.ProfileEntity;
import com.springboot.moneymanager.repository.CategoryRepository;
import com.springboot.moneymanager.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IncomeService {
    private final CategoryRepository categoryRepository;
    private final IncomeRepository incomeRepository;
    private final ProfileService profileService;

    // Hàm thêm thu nhập mới
    public IncomeDTO addIncome(IncomeDTO incomeDTO) {
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity category = null;
        if (incomeDTO.getCategoryId() != null) {
            category = categoryRepository.findById(incomeDTO.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
        }

        IncomeEntity newIncome = toEntity(incomeDTO, profile, category);
        newIncome = incomeRepository.save(newIncome);
        return toDTO(newIncome);
    }


    private IncomeEntity toEntity(IncomeDTO incomeDTO, ProfileEntity profile, CategoryEntity category) {
        return IncomeEntity.builder()
                .name(incomeDTO.getName())
                .icon(incomeDTO.getIcon())
                .date(incomeDTO.getDate())
                .amount(incomeDTO.getAmount())
                .profile(profile)
                .category(category)
                .build();
    }

    private IncomeDTO toDTO(IncomeEntity incomeEntity) {
        return IncomeDTO.builder()
                .id(incomeEntity.getId())
                .name(incomeEntity.getName())
                .icon(incomeEntity.getIcon())
                .date(incomeEntity.getDate())
                .amount(incomeEntity.getAmount())
                .categoryId(incomeEntity.getCategory() != null ? incomeEntity.getCategory().getId() : null)
                .categoryName(incomeEntity.getCategory() != null ? incomeEntity.getCategory().getName() : "N/A")
                .createdAt(incomeEntity.getCreatedAt())
                .updatedAt(incomeEntity.getUpdatedAt())
                .build();
    }
}
