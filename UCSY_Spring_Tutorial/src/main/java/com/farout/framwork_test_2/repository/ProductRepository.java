package com.farout.framwork_test_2.repository;

import com.farout.framwork_test_2.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
}

