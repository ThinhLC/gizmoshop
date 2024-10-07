package com.gizmo.gizmoshop.repository;

import com.gizmo.gizmoshop.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    List<ProductImage> findByFileDownloadUriContaining(String uri);
}
