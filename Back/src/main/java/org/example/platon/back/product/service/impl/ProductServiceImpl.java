package org.example.platon.back.product.service.impl;

import org.example.platon.back.product.dto.ProductRequest;
import org.example.platon.back.product.dto.ProductStats;
import org.example.platon.back.product.model.Product;
import org.example.platon.back.product.repository.ProductRepository;
import org.example.platon.back.product.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // ── Базовий CRUD ─────────────────────────────────────────────────────────

    @Override
    public Page<Product> getAllProducts(Pageable pageable) {
        // MongoRepository.findAll(Pageable) — вся колекція з пагінацією
        return productRepository.findAll(pageable);
    }

    @Override
    public Product getProductById(String id) {
        // MongoRepository.findById(id) → { "_id": ObjectId("...") }
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));
    }

    @Override
    public Product createProduct(ProductRequest request) {
        Product product = new Product(null, request.name(), request.description(),
                request.price(), request.imageUrl(), request.stock());
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(String id, ProductRequest request) {
        Product product = getProductById(id);
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setImageUrl(request.imageUrl());
        if (request.stock() != null) product.setStock(request.stock());
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(String id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found: " + id);
        }
        productRepository.deleteById(id);
    }

    // ── Пошук і фільтрація ───────────────────────────────────────────────────

    @Override
    public Page<Product> searchProducts(String name, BigDecimal minPrice, BigDecimal maxPrice,
                                         Boolean inStockOnly, Pageable pageable) {
        // → ProductRepositoryCustomImpl.searchWithFilters (MongoTemplate + Criteria)
        return productRepository.searchWithFilters(name, minPrice, maxPrice, inStockOnly, pageable);
    }

    @Override
    public Page<Product> searchByName(String name, Pageable pageable) {
        // → Derived method: { "name": { $regex: name, $options: "i" } }
        return productRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    @Override
    public List<Product> getByPriceRange(BigDecimal min, BigDecimal max) {
        // → @Query: { "price": { $gte: min, $lte: max } } sort: { "price": 1 }
        return productRepository.findByPriceRangeProjected(min, max);
    }

    @Override
    public List<Product> getInStockProducts() {
        // → Derived: { "stock": { $gt: 0 } }
        return productRepository.findByStockGreaterThan(0);
    }

    @Override
    public List<Product> getAffordableInStock(BigDecimal maxPrice) {
        // → @Query: { "price": { $lte: maxPrice }, "stock": { $gt: 0 } }
        return productRepository.findAffordableInStock(maxPrice);
    }

    @Override
    public List<Product> getLowStockProducts(int threshold) {
        // → @Query: { "stock": { $gt: 0, $lte: threshold } }
        return productRepository.findLowStockProducts(threshold);
    }

    // ── Аналітика ─────────────────────────────────────────────────────────────

    @Override
    public ProductStats getProductStats() {
        // → MongoTemplate aggregation pipeline (COUNT, MIN, MAX, AVG, SUM)
        return productRepository.getProductStatistics();
    }
}
