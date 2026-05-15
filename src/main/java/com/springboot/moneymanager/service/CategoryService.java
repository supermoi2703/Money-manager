package com.springboot.moneymanager.service;

import com.springboot.moneymanager.dto.CategoryDTO;
import com.springboot.moneymanager.entity.CategoryEntity;
import com.springboot.moneymanager.entity.ProfileEntity;
import com.springboot.moneymanager.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final ProfileService profileService;
    private final CategoryRepository categoryRepository;

    // Chuyển đổi từ CategoryDTO sang CategoryEntity
    private CategoryEntity toEntity(CategoryDTO categoryDTO, ProfileEntity profile) {
        return CategoryEntity.builder()
                .name(categoryDTO.getName())
                .icon(categoryDTO.getIcon())
                .type(categoryDTO.getType())
                .profile(profile)
                .build();
    }

    // Chuyển đổi từ CategoryEntity sang CategoryDTO
    private CategoryDTO toDTO(CategoryEntity categoryEntity) {
        return CategoryDTO.builder()
                .id(categoryEntity.getId())
                .name(categoryEntity.getName())
                .icon(categoryEntity.getIcon())
                .type(categoryEntity.getType())
                .profileId(categoryEntity.getProfile() != null ? categoryEntity.getProfile().getId() : null)
                .createdAt(categoryEntity.getCreatedAt())
                .updatedAt(categoryEntity.getUpdatedAt())
                .build();
    }

    // Hàm tạo category mới
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        // Lấy profile của người dùng hiện tại
        ProfileEntity profile = profileService.getCurrentProfile();
        if (categoryRepository.existsByNameAndProfileId(categoryDTO.getName(), profile.getId())) {
            throw new RuntimeException("Category name already exists for this profile");
        }

        // Chuyển đổi DTO sang Entity
        CategoryEntity categoryEntity = toEntity(categoryDTO, profile);
        // Lưu category vào database
        CategoryEntity savedCategory = categoryRepository.save(categoryEntity);
        // Chuyển đổi Entity đã lưu sang DTO và trả về
        return toDTO(savedCategory);
    }

    // Hàm lấy tất cả category của người dùng hiện tại
    public List<CategoryDTO> getAllCategoriesByProfileId() {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<CategoryEntity> categoryEntities = categoryRepository.findByProfileId(profile.getId());
        return categoryEntities.stream().map(this::toDTO).toList();
    }

    // Hàm lấy tất cả category theo type của người dùng hiện tại
    public List<CategoryDTO> getAllCategoriesByType(String type) {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<CategoryEntity> categoryEntities = categoryRepository.findByTypeAndProfileId(type, profile.getId());
        return categoryEntities.stream().map(this::toDTO).toList();
    }

    public CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO) {
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity categoryEntity = categoryRepository.findByIdAndProfileId(categoryId, profile.getId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        categoryEntity.setName(categoryDTO.getName());
        categoryEntity.setIcon(categoryDTO.getIcon());
        return toDTO(categoryRepository.save(categoryEntity));
    }
}
