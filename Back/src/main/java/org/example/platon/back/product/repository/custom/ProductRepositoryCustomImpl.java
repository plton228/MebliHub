package org.example.platon.back.product.repository.custom;

import org.bson.Document;
import org.example.platon.back.product.dto.ProductStats;
import org.example.platon.back.product.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Реалізація кастомних запитів через MongoTemplate.
 *
 * MongoTemplate — це низькорівневий клас Spring Data MongoDB,
 * який дає повний контроль над запитами: аналог JdbcTemplate у світі SQL.
 * Тут запити будуються програмно через Criteria API замість рядків.
 */
@Repository
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public ProductRepositoryCustomImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * Фільтрована пагінація товарів.
     *
     * Кожен ненульовий параметр додає умову до AND-запиту.
     * Еквівалент SQL:
     *   SELECT * FROM products
     *   WHERE name LIKE :name
     *     AND price BETWEEN :minPrice AND :maxPrice
     *     AND stock > 0
     *   LIMIT :size OFFSET :page * :size
     */
    @Override
    public Page<Product> searchWithFilters(String nameQuery,
                                           BigDecimal minPrice,
                                           BigDecimal maxPrice,
                                           Boolean inStockOnly,
                                           Pageable pageable) {
        List<Criteria> conditions = new ArrayList<>();

        if (nameQuery != null && !nameQuery.isBlank()) {
            // { "name": { $regex: "...", $options: "i" } }
            conditions.add(Criteria.where("name").regex(nameQuery.trim(), "i"));
        }
        if (minPrice != null) {
            // { "price": { $gte: minPrice } }
            conditions.add(Criteria.where("price").gte(minPrice));
        }
        if (maxPrice != null) {
            // { "price": { $lte: maxPrice } }
            conditions.add(Criteria.where("price").lte(maxPrice));
        }
        if (Boolean.TRUE.equals(inStockOnly)) {
            // { "stock": { $gt: 0 } }
            conditions.add(Criteria.where("stock").gt(0));
        }

        Criteria finalCriteria = conditions.isEmpty()
                ? new Criteria()
                : new Criteria().andOperator(conditions.toArray(new Criteria[0]));

        Query query = new Query(finalCriteria).with(pageable);
        Query countQuery = new Query(finalCriteria);

        List<Product> products = mongoTemplate.find(query, Product.class);
        long total = mongoTemplate.count(countQuery, Product.class);

        return new PageImpl<>(products, pageable, total);
    }

    /**
     * Агрегаційний pipeline для статистики товарів.
     *
     * MongoDB pipeline:
     *   db.products.aggregate([
     *     { $group: {
     *         _id: null,
     *         totalCount: { $sum: 1 },
     *         minPrice:   { $min: "$price" },
     *         maxPrice:   { $max: "$price" },
     *         avgPrice:   { $avg: "$price" },
     *         totalStock: { $sum: "$stock" }
     *     }}
     *   ])
     */
    @Override
    public ProductStats getProductStatistics() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group()
                        .count().as("totalCount")
                        .min("price").as("minPrice")
                        .max("price").as("maxPrice")
                        .avg("price").as("avgPrice")
                        .sum("stock").as("totalStock")
        );

        AggregationResults<Document> results =
                mongoTemplate.aggregate(aggregation, "products", Document.class);

        Document doc = results.getUniqueMappedResult();
        if (doc == null) {
            return new ProductStats(0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0);
        }

        return new ProductStats(
                doc.getInteger("totalCount", 0),
                toBigDecimal(doc.get("minPrice")),
                toBigDecimal(doc.get("maxPrice")),
                toBigDecimal(doc.get("avgPrice")).setScale(2, RoundingMode.HALF_UP),
                doc.getInteger("totalStock", 0)
        );
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof BigDecimal bd) return bd;
        if (value instanceof Number n) return BigDecimal.valueOf(n.doubleValue());
        return BigDecimal.ZERO;
    }
}
