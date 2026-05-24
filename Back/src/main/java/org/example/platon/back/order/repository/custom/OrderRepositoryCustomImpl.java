package org.example.platon.back.order.repository.custom;

import org.bson.Document;
import org.example.platon.back.order.dto.OrderStats;
import org.example.platon.back.order.dto.RevenueByProduct;
import org.example.platon.back.order.model.Order;
import org.example.platon.back.order.model.OrderStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реалізація кастомних запитів через MongoTemplate.
 *
 * MongoTemplate — це низькорівневий клас Spring Data MongoDB,
 * який дає повний контроль над запитами: аналог JdbcTemplate у світі SQL.
 */
@Repository
public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public OrderRepositoryCustomImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * Загальна статистика замовлень через комбінацію count() та aggregation.
     *
     * Для кожного статусу виконується окремий запит:
     *   db.orders.countDocuments({ status: "CONFIRMED" })
     *
     * Для загальної виручки — aggregation pipeline:
     *   db.orders.aggregate([
     *     { $match: { status: "CONFIRMED" } },
     *     { $group: { _id: null, totalRevenue: { $sum: "$totalPrice" } } }
     *   ])
     */
    @Override
    public OrderStats getOrderStats() {
        // COUNT(*) — загальна кількість замовлень
        long totalOrders = mongoTemplate.count(new Query(), Order.class);

        // COUNT WHERE status = 'CONFIRMED'
        long confirmedCount = mongoTemplate.count(
                new Query(Criteria.where("status").is(OrderStatus.CONFIRMED)), Order.class);

        // COUNT WHERE status = 'PENDING'
        long pendingCount = mongoTemplate.count(
                new Query(Criteria.where("status").is(OrderStatus.PENDING)), Order.class);

        // COUNT WHERE status = 'CANCELLED'
        long cancelledCount = mongoTemplate.count(
                new Query(Criteria.where("status").is(OrderStatus.CANCELLED)), Order.class);

        // SUM(totalPrice) WHERE status = 'CONFIRMED' — лише підтверджена виручка
        Aggregation revenueAgg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("status").is(OrderStatus.CONFIRMED)),
                Aggregation.group().sum("totalPrice").as("totalRevenue")
        );
        AggregationResults<Document> revenueResult =
                mongoTemplate.aggregate(revenueAgg, "orders", Document.class);

        Document revenueDoc = revenueResult.getUniqueMappedResult();
        BigDecimal totalRevenue = revenueDoc != null
                ? toBigDecimal(revenueDoc.get("totalRevenue"))
                : BigDecimal.ZERO;

        return new OrderStats(totalOrders, totalRevenue, confirmedCount, pendingCount, cancelledCount);
    }

    /**
     * Виручка по товарах через GROUP BY aggregation pipeline.
     *
     * db.orders.aggregate([
     *   { $match: { status: "CONFIRMED" } },
     *   { $group: {
     *       _id: { productId: "$productId", productName: "$productName" },
     *       orderCount:   { $sum: 1 },
     *       totalRevenue: { $sum: "$totalPrice" }
     *   }},
     *   { $project: {
     *       productId:    "$_id.productId",
     *       productName:  "$_id.productName",
     *       orderCount:   1,
     *       totalRevenue: 1
     *   }},
     *   { $sort: { totalRevenue: -1 } }
     * ])
     */
    @Override
    public List<RevenueByProduct> getRevenueByProduct() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("status").is(OrderStatus.CONFIRMED)),
                Aggregation.group("productId", "productName")
                        .count().as("orderCount")
                        .sum("totalPrice").as("totalRevenue"),
                Aggregation.project("orderCount", "totalRevenue")
                        .and("_id.productId").as("productId")
                        .and("_id.productName").as("productName"),
                Aggregation.sort(Sort.Direction.DESC, "totalRevenue")
        );

        AggregationResults<Document> results =
                mongoTemplate.aggregate(aggregation, "orders", Document.class);

        return results.getMappedResults().stream()
                .map(doc -> new RevenueByProduct(
                        doc.getString("productId"),
                        doc.getString("productName"),
                        doc.getInteger("orderCount", 0),
                        toBigDecimal(doc.get("totalRevenue"))
                ))
                .collect(Collectors.toList());
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof BigDecimal bd) return bd;
        if (value instanceof Number n) return BigDecimal.valueOf(n.doubleValue());
        return BigDecimal.ZERO;
    }
}
