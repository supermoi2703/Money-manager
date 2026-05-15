package com.springboot.moneymanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDTO {
    private Long id;
    private String name;
    private Long profileId;
    private String icon; // "income" hoặc "expense"
    private String type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
