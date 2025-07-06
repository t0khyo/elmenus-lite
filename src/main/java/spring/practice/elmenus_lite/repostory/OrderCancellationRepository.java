package spring.practice.elmenus_lite.repostory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spring.practice.elmenus_lite.model.OrderCancellation;
import spring.practice.elmenus_lite.model.enums.CancellationInitiator;
import spring.practice.elmenus_lite.model.enums.CancellationProcessingStatus;
import spring.practice.elmenus_lite.model.enums.CancellationReason;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface OrderCancellationRepository extends JpaRepository<OrderCancellation, Long> {

    // Find cancellation by order
    Optional<OrderCancellation> findByOrderId(Long orderId);

    // Operational queries - for background processing
    @Query("SELECT oc FROM OrderCancellation oc " +
            "WHERE oc.processingStatus = :status " +
            "AND oc.createdAt >= :since " +
            "ORDER BY oc.createdAt ASC")
    List<OrderCancellation> findByProcessingStatusSince(
            @Param("status") CancellationProcessingStatus status,
            @Param("since") LocalDateTime since
    );

    // Find pending refunds
    @Query("SELECT oc FROM OrderCancellation oc " +
            "WHERE oc.refundAmount > 0 " +
            "AND oc.refundProcessedAt IS NULL " +
            "ORDER BY oc.createdAt ASC")
    List<OrderCancellation> findPendingRefunds();

    // Find cancellations requiring restaurant compensation
    @Query("SELECT oc FROM OrderCancellation oc " +
            "WHERE oc.restaurantCompensation > 0 " +
            "AND oc.restaurantNotifiedAt IS NULL")
    List<OrderCancellation> findPendingRestaurantCompensation();

    // Analytics queries
    List<OrderCancellation> findByInitiatedByAndCancelledAtBetween(
            CancellationInitiator initiator,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    List<OrderCancellation> findByCancellationReasonAndCancelledAtBetween(
            CancellationReason reason,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    // Customer service queries
    @Query("SELECT oc FROM OrderCancellation oc " +
            "JOIN oc.order o " +
            "WHERE o.customer.id = :customerId " +
            "ORDER BY oc.cancelledAt DESC")
    List<OrderCancellation> findByCustomerId(@Param("customerId") Integer customerId);

    // Restaurant queries
    @Query("SELECT oc FROM OrderCancellation oc " +
            "JOIN oc.order o " +
            "JOIN o.orderItems oi " +
            "JOIN oi.menuItem mi " +
            "JOIN mi.menu m " +
            "WHERE m.restaurant.id = :restaurantId " +
            "AND oc.cancelledAt BETWEEN :startDate AND :endDate")
    List<OrderCancellation> findByRestaurantIdAndDateRange(
            @Param("restaurantId") Long restaurantId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // Financial queries
    @Query("SELECT SUM(oc.refundAmount) FROM OrderCancellation oc " +
            "WHERE oc.refundProcessedAt BETWEEN :startDate AND :endDate")
    BigDecimal calculateRefundAmountByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}