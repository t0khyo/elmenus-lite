package spring.practice.elmenus_lite.service;

import spring.practice.elmenus_lite.dto.request.CancelOrderRequest;
import spring.practice.elmenus_lite.dto.response.CancellationResponse;
import spring.practice.elmenus_lite.dto.response.CancellationEligibilityResponse;
import spring.practice.elmenus_lite.model.enums.CancellationInitiator;
import spring.practice.elmenus_lite.model.enums.CancellationReason;

import java.util.List;


public interface OrderCancellationService {


    CancellationEligibilityResponse checkCancellationEligibility(Long orderId, Integer customerId);


    CancellationResponse cancelOrder(Long orderId, CancelOrderRequest request, CancellationInitiator initiator);

    CancellationResponse cancelOrderByCustomer(Long orderId, Integer customerId,
                                               CancellationReason reason, String details);


    CancellationResponse cancelOrderByRestaurant(Long orderId, Long restaurantId,
                                                 CancellationReason reason, String details);
    CancellationResponse cancelOrderBySystem(Long orderId, CancellationReason reason, String details);


    CancellationResponse cancelOrderByAdmin(Long orderId, String adminId,
                                            CancellationReason reason, String details);


    CancellationResponse getCancellationDetails(Long orderId);


    void processPendingRefunds();
    void processRestaurantCompensations();
    void cleanupExpiredOrders();


    List<CancellationResponse> getCancellationsByCustomer(Integer customerId);
    List<CancellationResponse> getCancellationsByRestaurant(Long restaurantId);
}