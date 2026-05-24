package org.example.platon.back.product.service;

import org.example.platon.back.product.dto.ProductRequest;
import org.example.platon.back.product.dto.ProductStats;
import org.example.platon.back.product.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    // ── Базовий CRUD ────────────────────────────────────────────────────────
    Page<Product> getAllProducts(Pageable pageable);
    Product getProductById(String id);
    Product createProduct(ProductRequest request);
    Product updateProduct(String id, ProductRequest request);
    void deleteProduct(String id);

    // ── Пошук і фільтрація ──────────────────────────────────────────────────
    /** Фільтрований пошук через MongoTemplate (Criteria API). */
    Page<Product> searchProducts(String name, BigDecimal minPrice, BigDecimal maxPrice,
                                  Boolean inStockOnly, Pageable pageable);

    /** Пошук за назвою через Spring Data derived method. */
    Page<Product> searchByName(String name, Pageable pageable);

    /** Товари в ціновому діапазоні через @Query. */
    List<Product> getByPriceRange(BigDecimal min, BigDecimal max);

    /** Товари в наявності (stock > 0) через derived method. */
    List<Product> getInStockProducts();

    /** Доступні товари з урахуванням бюджету (@Query). */
    List<Product> getAffordableInStock(BigDecimal maxPrice);

    /** Товари з низьким залишком (@Query). */
    List<Product> getLowStockProducts(int threshold);

    // ── Аналітика ───────────────────────────────────────────────────────────
    /** Статистика через MongoTemplate aggregation pipeline. */
    ProductStats getProductStats();
}
