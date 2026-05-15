package com.springboot.moneymanager.controller;

import com.springboot.moneymanager.dto.CategoryDTO;
import com.springboot.moneymanager.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;

    // Endpoint để tạo category mới
    @PostMapping({"/create"})
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryDTO categoryDTO) {
        CategoryDTO createdCategory = categoryService.createCategory(categoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
    }

    // Endpoint để lấy tất cả category của người dùng hiện tại
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getALlCategories(){
        List<CategoryDTO> categories = categoryService.getAllCategoriesByProfileId();
        return ResponseEntity.ok(categories);
    }

    // Endpoint để lấy tất cả category theo type (income hoặc expense) của người dùng hiện tại
    @GetMapping("/{type}")
    public ResponseEntity<List<CategoryDTO>> getCategoriesByType(@PathVariable String type) {
        List<CategoryDTO> categories = categoryService.getAllCategoriesByType(type);
        return ResponseEntity.ok(categories);
    }

    // Endpoint để cập nhật category theo id
    @PutMapping("/update/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id,
                                                      @RequestBody CategoryDTO categoryDTO) {
        try {
            CategoryDTO updatedCategory = categoryService.updateCategory(id, categoryDTO);
            return ResponseEntity.ok(updatedCategory);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
