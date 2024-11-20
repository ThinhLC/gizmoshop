package com.gizmo.gizmoshop.repository;

import com.gizmo.gizmoshop.dto.reponseDto.BrandResponseDto;
import com.gizmo.gizmoshop.entity.Categories;
import com.gizmo.gizmoshop.entity.ProductBrand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductBrandRepository extends JpaRepository<ProductBrand, Long> {
    // Tìm kiếm thương hiệu theo tên
    Optional<ProductBrand> findByName(String name);
//
//    // Tìm tất cả thương hiệu đang hoạt động
//    List<ProductBrand> findByActive(boolean active);
//
////    boolean existsByName(String name);
    Page<ProductBrand> findByDeletedFalse(Pageable pageable);

    ProductBrand findByIdAndDeletedFalse(Long id);

    List<Categories> findByIdIn(List<Long> ids);


    @Query("SELECT new com.gizmo.gizmoshop.dto.reponseDto.BrandResponseDto(i.id, i.name, i.description, i.deleted) " +
            "FROM ProductBrand i WHERE " +
            "(:name IS NULL OR i.name LIKE %:name%) " +
            "AND (:deleted IS NULL OR i.deleted = :deleted)")
    Page<BrandResponseDto> findBrandResponseDtos(@Param("name") String name,
                                                 @Param("deleted") Boolean deleted,
                                                 Pageable pageable);

    boolean existsByName(String name);
}
