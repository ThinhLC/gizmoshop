package com.gizmo.gizmoshop.repository;

import com.gizmo.gizmoshop.entity.Categories;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriesRepository extends JpaRepository<Categories, Long> {
    List<Categories> findByActiveTrue();
    Page<Categories> findByActiveTrue(Pageable pageable);
    @Query("SELECT c FROM Categories c WHERE " +
            "(:keyword IS NULL OR c.name LIKE %:keyword%) " +
            "AND (:deleted IS NULL OR c.active = :deleted)")
    Page<Categories> findCategorissByCriteria(@Param("keyword") String keyword,
                                             @Param("deleted") Boolean deleted,
                                             Pageable pageable);
    boolean existsByName(String name);  // Kiểm tra trùng lặp tên
    Categories findByIdAndActiveFalse(Long id);

}
