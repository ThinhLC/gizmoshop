package com.gizmo.gizmoshop.repository;

import com.gizmo.gizmoshop.entity.Categories;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriesRepository extends JpaRepository<Categories, Long> {
    List<Categories> findByActiveFalse();
    Page<Categories> findByActiveFalse(Pageable pageable);

}
