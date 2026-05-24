package org.example.platon.back.product.controller;

import jakarta.validation.Valid;
import org.example.platon.back.product.dto.ProductRequest;
import org.example.platon.back.product.dto.ProductStats;
import org.example.platon.back.product.model.Product;
import org.example.platon.back.product.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // ── Базовий CRUD (Public GET, Admin POST/PUT/DELETE) ─────────────────────

    @GetMapping
    public ResponseEntity<Page<Product>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(productService.getAllProducts(PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable String id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> updateProduct(@PathVariable String id,
                                                  @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // ── Пошук і фільтрація (Public) ──────────────────────────────────────────

    /**
     * Комплексний пошук через MongoTemplate (Criteria API).
     * GET /api/v1/products/search?name=диван&minPrice=1000&maxPrice=50000&inStockOnly=true
     */
    @GetMapping("/search")
    public ResponseEntity<Page<Product>> searchProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean inStockOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                productService.searchProducts(name, minPrice, maxPrice, inStockOnly, PageRequest.of(page, size))
        );
    }

    /**
     * Пошук за назвою (derived method: findByNameContainingIgnoreCase).
     * GET /api/v1/products/by-name?name=крісло
     */
    @GetMapping("/by-name")
    public ResponseEntity<Page<Product>> searchByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(productService.searchByName(name, PageRequest.of(page, size)));
    }

    /**
     * Фільтр за ціновим діапазоном (@Query + проекція полів).
     * GET /api/v1/products/price-range?min=1000&max=20000
     */
    @GetMapping("/price-range")
    public ResponseEntity<List<Product>> getByPriceRange(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        return ResponseEntity.ok(productService.getByPriceRange(min, max));
    }

    /**
     * Тільки товари в наявності (derived: findByStockGreaterThan(0)).
     * GET /api/v1/products/in-stock
     */
    @GetMapping("/in-stock")
    public ResponseEntity<List<Product>> getInStockProducts() {
        return ResponseEntity.ok(productService.getInStockProducts());
    }

    /**
     * Товари в наявності, дешевші за вказану суму (@Query compound).
     * GET /api/v1/products/affordable?maxPrice=15000
     */
    @GetMapping("/affordable")
    public ResponseEntity<List<Product>> getAffordableInStock(
            @RequestParam BigDecimal maxPrice) {
        return ResponseEntity.ok(productService.getAffordableInStock(maxPrice));
    }

    /**
     * Товари з залишком менше порогу (@Query).
     * GET /api/v1/products/low-stock?threshold=5  — тільки для ADMIN
     */
    @GetMapping("/low-stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Product>> getLowStockProducts(
            @RequestParam(defaultValue = "5") int threshold) {
        return ResponseEntity.ok(productService.getLowStockProducts(threshold));
    }

    // ── Аналітика (Admin only) ────────────────────────────────────────────────

    /**
     * Агрегаційна статистика по товарах (MongoTemplate pipeline).
     * GET /api/v1/products/stats
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductStats> getProductStats() {
        return ResponseEntity.ok(productService.getProductStats());
    }
}
