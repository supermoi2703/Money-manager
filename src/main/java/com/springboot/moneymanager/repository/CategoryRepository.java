package com.springboot.moneymanager.repository;

import com.springboot.moneymanager.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    // Tìm tất cả các category theo profileId
    List<CategoryEntity> findByProfileId(Long profileId);

    // Tìm category theo id và profileId để đảm bảo category thuộc về profile đó
    Optional<CategoryEntity> findByIdAndProfileId(Long id, Long profileId);

    // Tìm tất cả các category theo type và profileId
    List<CategoryEntity> findByTypeAndProfileId(String type, Long profileId);

    // Kiểm tra xem đã tồn tại category với name và profileId chưa (dùng để tránh trùng tên)
    Boolean existsByNameAndProfileId(String name, Long profileId);
}
