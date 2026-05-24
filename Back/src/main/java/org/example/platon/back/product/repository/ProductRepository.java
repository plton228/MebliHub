package org.example.platon.back.product.repository;

import org.example.platon.back.product.model.Product;
import org.example.platon.back.product.repository.custom.ProductRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, String>, ProductRepositoryCustom {

    // ── Derived query methods ───────────────────────────────────────────────
    // Spring Data читає назву методу і автоматично генерує MongoDB-запит.
    // findByNameContainingIgnoreCase → { "name": { $regex: "...", $options: "i" } }

    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // { "price": { $gte: minPrice, $lte: maxPrice } }
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    // { "price": { $lte: maxPrice } }
    List<Product> findByPriceLessThanEqualOrderByPriceAsc(BigDecimal maxPrice);

    // { "stock": { $gt: threshold } }
    List<Product> findByStockGreaterThan(int threshold);

    // { "stock": 0 }
    long countByStock(int stock);

    boolean existsByNameIgnoreCase(String name);

    // ── @Query — явний MongoDB-запит у форматі JSON ─────────────────────────
    // Знаходить доступні товари (ціна ≤ maxPrice І є в наявності)
    @Query("{ 'price': { $lte: ?0 }, 'stock': { $gt: 0 } }")
    List<Product> findAffordableInStock(BigDecimal maxPrice);

    // Знаходить товари у ціновому діапазоні, повертає тільки name/price/imageUrl
    @Query(
        value  = "{ 'price': { $gte: ?0, $lte: ?1 } }",
        fields = "{ 'name': 1, 'price': 1, 'imageUrl': 1, 'stock': 1 }",
        sort   = "{ 'price': 1 }"
    )
    List<Product> findByPriceRangeProjected(BigDecimal minPrice, BigDecimal maxPrice);

    // Пошук товарів за регулярним виразом у назві або описі
    @Query("{ $or: [ { 'name': { $regex: ?0, $options: 'i' } }, { 'description': { $regex: ?0, $options: 'i' } } ] }")
    List<Product> findByNameOrDescriptionContaining(String pattern);

    // Товари з критично малим залишком
    @Query("{ 'stock': { $gt: 0, $lte: ?0 } }")
    List<Product> findLowStockProducts(int threshold);
}
