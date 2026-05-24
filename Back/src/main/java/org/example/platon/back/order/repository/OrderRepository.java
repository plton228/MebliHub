package org.example.platon.back.order.repository;

import org.example.platon.back.order.model.Order;
import org.example.platon.back.order.model.OrderStatus;
import org.example.platon.back.order.repository.custom.OrderRepositoryCustom;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<Order, String>, OrderRepositoryCustom {

    // ── Derived query methods ───────────────────────────────────────────────
    // { "userId": userId }
    List<Order> findByUserId(String userId);

    // { "userId": userId } + sort: { "createdAt": -1 }
    List<Order> findByUserIdOrderByCreatedAtDesc(String userId);

    // { "status": status }
    List<Order> findByStatus(OrderStatus status);

    // { "productId": productId }
    List<Order> findByProductId(String productId);

    // COUNT WHERE userId = userId
    long countByUserId(String userId);

    // COUNT WHERE status = status
    long countByStatus(OrderStatus status);

    // ── @Query — явний MongoDB JSON-запит ────────────────────────────────────

    // Замовлення конкретного юзера з конкретним статусом
    @Query("{ 'userId': ?0, 'status': ?1 }")
    List<Order> findByUserIdAndStatus(String userId, String status);

    // Всі підтверджені замовлення, відсортовані за датою (новіші спочатку)
    @Query(value = "{ 'status': 'CONFIRMED' }", sort = "{ 'createdAt': -1 }")
    List<Order> findAllConfirmedOrderByDateDesc();

    // Замовлення дорожче за заданий поріг (для аналізу великих покупок)
    @Query("{ 'totalPrice': { $gte: ?0 } }")
    List<Order> findOrdersAbovePrice(BigDecimal minTotal);

    // Замовлення після певної дати
    @Query("{ 'createdAt': { $gte: ?0 } }")
    List<Order> findOrdersCreatedAfter(LocalDateTime after);

    // Повертає тільки productName та totalPrice (проекція полів)
    @Query(
        value  = "{ 'userId': ?0 }",
        fields = "{ 'productName': 1, 'quantity': 1, 'totalPrice': 1, 'status': 1, 'createdAt': 1 }"
    )
    List<Order> findOrderSummariesByUserId(String userId);
}
