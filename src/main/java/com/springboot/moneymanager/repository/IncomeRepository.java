package com.springboot.moneymanager.repository;

import com.springboot.moneymanager.entity.IncomeEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


public interface IncomeRepository extends JpaRepository<IncomeEntity, Long> {

    // Phương thức truy vấn này để lấy tất cả các khoản thu của một profile, sắp xếp theo ngày giảm dần
    List<IncomeEntity> findByProfileIdOrderByDateDesc(Long profileId);

    // Phương thức truy vấn này để lấy 5 khoản thu mới nhất của một profile, sắp xếp theo ngày giảm dần
    List<IncomeEntity> findTop5ByProfileIdOrderByDateDesc(Long profileId);

    // Phương thức truy vấn này để tính tổng số tiền thu nhập của một profile, sử dụng JPQL để thực hiện phép tính SUM trên cột amount
    @Query("SELECT SUM(e.amount) FROM IncomeEntity e WHERE e.profile.id = :profileId")
    BigDecimal findTotalExpenseByProfileId(@Param("profileId") Long profileId);

    // Phương thức truy vấn này để tìm kiếm các khoản thu của một profile dựa trên tên và khoảng thời gian, sử dụng các tham số để lọc kết quả và sắp xếp theo ngày
    List<IncomeEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
            Long profileId,
            String name,
            LocalDate startDate,
            LocalDate endDate,
            Sort sort
    );

    // Phương thức truy vấn này để tìm kiếm các khoản thu của một profile dựa trên khoảng thời gian, sử dụng các tham số để lọc kết quả và sắp xếp theo ngày
    List<IncomeEntity> findByProfileIdAndDateBetween(Long profileId, LocalDate startDate, LocalDate endDate);

}
