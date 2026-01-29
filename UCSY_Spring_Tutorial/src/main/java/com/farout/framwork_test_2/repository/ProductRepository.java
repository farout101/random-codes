package com.farout.framwork_test_2.repository;

import com.farout.framwork_test_2.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByProductCode(String productCode);
    List<Product> findByCategory(String category);
    List<Product> findByStatus(Product.ProductStatus status);

    @Query("SELECT p.category, COUNT(p) FROM Product p GROUP BY p.category")
    List<Object[]> countProductsByCategory();

    @Query("SELECT SUM(p.price * p.quantity) FROM Product p")
    BigDecimal getTotalInventoryValue();

    List<Product> findByQuantityLessThan(Integer quantity);

    //Search with pagination
    @Query("SELECT p FROM Product p WHERE " +
            "LOWER(p.productCode) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(p.productName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(p.category) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Product> searchByTerm(
            @Param("search") String search,
            Pageable pageable);

}

