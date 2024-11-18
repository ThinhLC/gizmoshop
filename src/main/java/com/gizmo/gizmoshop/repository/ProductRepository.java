package com.gizmo.gizmoshop.repository;

import com.gizmo.gizmoshop.entity.Product;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findById(Long productId);

    @Query("SELECT p FROM Product p WHERE (:productName IS NULL OR p.name LIKE %:productName%) " +
            "AND (:isSupplier IS NULL OR p.isSupplier = :isSupplier) " +
            "AND (:idStatus IS NULL OR p.status.id = :idStatus) " +
            "AND (:active IS NULL OR p.deleted = :active)")
    Page<Product> findAllByCriteria(@Param("productName") String productName,
                                    @Param("active") Boolean active,
                                    Pageable pageable,
                                    @Param("isSupplier") Boolean isSupplier,
                                    @Param("idStatus") Long idStatus);

    @Query("SELECT p FROM Product p WHERE FUNCTION('MONTH', p.createAt) = :month AND FUNCTION('YEAR', p.createAt) = :year")
    Page<Product> findByMonthAndYear(@Param("month") int month, @Param("year") int year, Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "JOIN FETCH p.author " +
            "JOIN FETCH p.category " +
            "JOIN FETCH p.brand " +
            "JOIN FETCH p.status")
    Page<Product> findAllProducts(Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "JOIN p.category c " +
            "JOIN p.productInventory pi " +
            "JOIN pi.inventory i " +
            "JOIN p.brand b " +
            "JOIN p.status s " +
            "JOIN p.author a " +
            "WHERE c.active = true " +
            "AND (:brand IS NULL OR b.id = :brand) "+
            "AND (:category IS NULL OR c.id = :category)" +
            "AND i.active = true " +
            "AND b.deleted = false " +
            "AND (p.deleted = false OR p.deleted IS NULL) " +
            "AND s.id = 1 " +
            "AND a.deleted = false " +
            "AND (:price1 IS NULL OR :price2 IS NULL OR p.price BETWEEN :price1 AND :price2) " +
            "AND (:keyword IS NULL OR LOWER(p.name) LIKE CONCAT('%', :keyword, '%') " +
            "OR LOWER(p.shortDescription) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.longDescription) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY " +
            "CASE WHEN :sortFieldCase = 'discountproduct' THEN p.discountProduct END DESC," +
            "CASE WHEN :sortFieldCase = 'view' THEN p.view END DESC"
    )
    Page<Product> findAllProductsForClient(
            @Param("price1") Long price1,
            @Param("price2") Long price2,
            @Param("brand") Long brand,
            @Param("category") Long category,
            @Param("keyword") String keyword,
            @Param("sortFieldCase") String sortFieldCase,
            Pageable pageable
    );

    @Query("SELECT p FROM Product p " +
            "JOIN p.category c " +
            "JOIN p.productInventory pi " +
            "JOIN pi.inventory i " +
            "JOIN p.brand b " +
            "JOIN p.status s " +
            "JOIN p.author a " +
            "WHERE c.active = true " +
            "AND i.active = true " +
            "AND b.deleted = false " +
            "AND (p.deleted = false OR p.deleted IS NULL) " +
            "AND s.id = 1 " +
            "AND a.deleted = false " +
            "AND p.id = :idProduct")
    Optional<Product> findProductDetailForClient(@Param("idProduct") Long idProduct);


    @Query("SELECT SUM(od.quantity) FROM OrderDetail od JOIN od.idOrder o WHERE od.idProduct.id = :productId AND o.orderStatus.id = 16")
    Long countSoldProduct(Long productId);

    @Query("SELECT p FROM Product p " +
            "JOIN p.brand b " +
            "JOIN p.productInventory pi "+
            "JOIN p.category c " +
            "JOIN p.author a "+
            "JOIN p.status s "+
            "WHERE c.active = true "+
            "AND b.deleted = false "+
            "AND s.id = 1 "+
            "OR b.id = :brandId "+
            "AND a.deleted = false "
    )
    Page<Product> findByBrand(@Param("brandId") Long brandId, Pageable pageable);

}
