package com.farout.framwork_test_2.service;

import com.farout.framwork_test_2.model.Product;
import com.farout.framwork_test_2.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getRecentProducts() {
        // Get latest 10 products
        return productRepository.findAll().stream()
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                .limit(10)
                .collect(Collectors.toList());
    }

    public Map<String, Long> getProductsByCategory() {
        List<Object[]> results = productRepository.countProductsByCategory();
        Map<String, Long> categoryCounts = new HashMap<>();

        for (Object[] result : results) {
            String category = (String) result[0];
            Long count = (Long) result[1];
            categoryCounts.put(category, count);
        }

        return categoryCounts;
    }

    public Map<String, Object> getDashboardStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // Total products
        long totalProducts = productRepository.count();
        stats.put("totalProducts", totalProducts);

        // Total categories
        List<Object[]> categories = productRepository.countProductsByCategory();
        stats.put("totalCategories", categories.size());

        // Total inventory value
        BigDecimal totalValue = productRepository.getTotalInventoryValue();
        stats.put("totalValue", totalValue != null ? totalValue : BigDecimal.ZERO);

        // Low stock count
        List<Product> lowStock = productRepository.findByQuantityLessThan(10);
        stats.put("lowStockCount", lowStock.size());

        // Out of stock count
        List<Product> outOfStock = productRepository.findByStatus(Product.ProductStatus.OUT_OF_STOCK);
        stats.put("outOfStockCount", outOfStock.size());

        return stats;
    }

    public List<Product> getLowStockProducts() {
        return new ArrayList<>(productRepository.findByQuantityLessThan(10));
    }

    public void deleteProduct(Long id)  {
        try {
            productRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Page<Product> getProductsWithPagination(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page - 1, size);
        if (search == null || search.isEmpty()) {
            return this.productRepository.findAll(pageable);
        } else {
// Search
            return productRepository.searchByTerm(search, pageable);
        }
    }

}