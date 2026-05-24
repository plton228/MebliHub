package org.example.platon.back.product.repository.custom;

import org.example.platon.back.product.dto.ProductStats;
import org.example.platon.back.product.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

/**
 * Кастомні методи репозиторію, що виконуються через MongoTemplate.
 * Тут розробник будує запити вручну за допомогою Criteria API
 * замість того, щоб покладатися на автогенерацію Spring Data.
 */
public interface ProductRepositoryCustom {

    /**
     * Фільтрація товарів за кількома параметрами одночасно.
     * Використовує MongoTemplate + Criteria (аналог WHERE ... AND ... в SQL).
     */
    Page<Product> searchWithFilters(
            String nameQuery,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean inStockOnly,
            Pageable pageable
    );

    /**
     * Агрегаційний pipeline: мін/макс/середня ціна, загальний залишок.
     * Аналог SELECT COUNT(*), MIN(price), MAX(price), AVG(price), SUM(stock).
     */
    ProductStats getProductStatistics();
}
