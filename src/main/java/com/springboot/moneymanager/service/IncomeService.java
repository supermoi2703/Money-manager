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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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

    // Hàm lấy tất cả thu nhập của người dùng hiện tại trong tháng hiện tại
    public List<IncomeDTO> getCurrentMonthIncomesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<IncomeEntity> incomes = incomeRepository.findByProfileIdAndDateBetween(profile.getId(), startDate, endDate);
        return incomes.stream()
                .map(this::toDTO)
                .toList();
    }

    // Hàm xóa thu nhập
    public void deleteIncome(Long incomeId) {
        ProfileEntity profile = profileService.getCurrentProfile();
        IncomeEntity income = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new RuntimeException("income not found"));
        if (!income.getProfile().getId().equals(profile.getId())){
            throw new RuntimeException("You are not authorized to delete this income");
        }
        incomeRepository.delete(income);
    }

    // Hàm lấy 5 thu nhập mới nhất của người dùng hiện tại
    public List<IncomeDTO> getLastest5IncomesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<IncomeEntity> incomes = incomeRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return incomes.stream()
                .map(this::toDTO)
                .toList();
    }

    // Hàm tính tổng thu nhập của người dùng hiện tại
    public BigDecimal getTotalIncomeForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        return incomeRepository.findTotalExpenseByProfileId(profile.getId());
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
