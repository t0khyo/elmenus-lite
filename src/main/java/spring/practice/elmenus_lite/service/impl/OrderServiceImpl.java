package spring.practice.elmenus_lite.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spring.practice.elmenus_lite.dto.OrderRequest;
import spring.practice.elmenus_lite.dto.OrderSummaryResponse;
import spring.practice.elmenus_lite.dto.PaymentResult;
import spring.practice.elmenus_lite.enums.ErrorMessage;
import spring.practice.elmenus_lite.exception.ResourceNotFoundException;
import spring.practice.elmenus_lite.mapper.OrderMapper;
import spring.practice.elmenus_lite.model.*;
import spring.practice.elmenus_lite.model.enums.OrderStatusEnum;
import spring.practice.elmenus_lite.model.enums.TransactionStatusEnum;
import spring.practice.elmenus_lite.repostory.OrderItemRepository;
import spring.practice.elmenus_lite.repostory.OrderRepository;
import spring.practice.elmenus_lite.repostory.OrderStatusRepository;
import spring.practice.elmenus_lite.service.OrderService;
import spring.practice.elmenus_lite.service.PromotionService;
import spring.practice.elmenus_lite.service.helper.CartHelper;
import spring.practice.elmenus_lite.service.helper.CustomerHelper;
import spring.practice.elmenus_lite.util.PaymentUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j // For logging
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderStatusRepository orderStatusRepository;
    //TODO: Refactor
    private final PaymentUtils paymentUtils;
    private final PromotionService promotionService;
    private final CartHelper cartHelper;
    private final CustomerHelper customerHelper;

    @Override
    @Transactional
    public OrderSummaryResponse placeOrder(OrderRequest orderRequest,Integer userID) {
        log.info("Starting order placement for customer: {}", userID);
        // Step 1: Entity Fetching & Validation
        Customer customer = customerHelper.fetchCustomerByUserId(userID);

        CompletableFuture<Address> addressFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("Thread : " + Thread.currentThread().getName());
            return customerHelper.fetchAndValidateAddress(orderRequest.addressId(), customer.getId());
        });
        CompletableFuture<PreferredPaymentSetting> preferredPaymentSettingFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("Thread : " + Thread.currentThread().getName());
            return customerHelper.fetchAndValidatePaymentSetting(
                    orderRequest.preferredPaymentSettingId(), customer.getId());
        });
        CompletableFuture<Promotion> promotionFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("Thread : " + Thread.currentThread().getName());
            return promotionService.fetchAndValidatePromotion(orderRequest.promotionCode());
        });

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                addressFuture,
                preferredPaymentSettingFuture,
                promotionFuture
        );

        allFutures.join();
        Address address = addressFuture.join();
        Promotion promotion = promotionFuture.join();
        PreferredPaymentSetting preferredPaymentSetting = preferredPaymentSettingFuture.join();

        // Step 2: Cart Validation
        Cart cart = getCart(customer);

        List<CartItem> cartItems = cartHelper.validateCartItems(cart.getId());
        log.info("Cart found with {} items.", cartItems.size());

        // Step 3: Calculate Totals
        BigDecimal subtotal = cartHelper.calculateSubtotal(cartItems);
        BigDecimal discountAmount = promotionService.calculateDiscountAmount(subtotal, promotion);
        BigDecimal total = subtotal.subtract(discountAmount);
        log.info("Calculate Total for Items : {} .", total);

        //TODO:  Step 4: Process Payment
        //PaymentResult paymentResult = paymentService.processPayment(customer.getId(), total, paymentSetting.getPreferredPaymentSettingId());
        PaymentResult dummyPaymentResult = new PaymentResult(TransactionStatusEnum.SUCCESS, "Cash", UUID.randomUUID().toString());

        // Step 5: Create Order
        Order order = createOrder(customer, address, promotion, subtotal, discountAmount, total, dummyPaymentResult.status());

        // Step 6: Create Order Items
        createOrderItems(order, cartItems);

        // Step 7: Clear Cart (Only if Payment Successful)
        cartHelper.cleanupCart(cart.getId(), dummyPaymentResult.status());

        // Step 8: Return OrderSummary
        return buildOrderSummary(order, dummyPaymentResult);
    }

    @Override
    public OrderSummaryResponse getOrderSummary(Integer orderId) {
        Order order = fetchAndValidateOrder(orderId);
        Transaction transaction = paymentUtils.fetchTransactionByOrderId(orderId);
        return orderMapper.toOrderSummary(order, transaction.getPaymentMethod().getPaymentType());
    }

    //Helper Method
    private Order createOrder(Customer customer, Address address, Promotion promotion,
                              BigDecimal subtotal, BigDecimal discountAmount, BigDecimal total,
                              TransactionStatusEnum transactionStatus) {

        OrderStatusEnum orderStatus = mapTransactionStatusToOrderStatus(transactionStatus);

        OrderStatus orderStatusEntity = orderStatusRepository.findByName(orderStatus.getStatus())
                .orElseThrow(() -> new ResourceNotFoundException("Order status not found: " + orderStatus.getStatus()));

        Order order = Order.builder()
                .customer(customer)
                .address(address)
                .orderStatus(orderStatusEntity)
                .promotion(promotion)
                .discountAmount(discountAmount)
                .subtotal(subtotal)
                .total(total)
                .orderDate(LocalDateTime.now())
                .build();

        order = orderRepository.save(order);
        return order;
    }

    private void createOrderItems(Order order, List<CartItem> cartItems) {
        for (CartItem cartItem : cartItems) {
            MenuItem menuItem = cartItem.getMenuItem(); // Already validated

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .menuItem(menuItem)
                    .unitPrice(menuItem.getPrice())
                    .quantity(cartItem.getQuantity())
                    .build();

            orderItemRepository.save(orderItem);
        }
    }

    private OrderStatusEnum mapTransactionStatusToOrderStatus(TransactionStatusEnum transactionStatus) {
        return switch (transactionStatus) {
            case SUCCESS -> OrderStatusEnum.CONFIRMED;
            case FAILED -> OrderStatusEnum.FAILED;
            case PENDING -> OrderStatusEnum.PENDING;
        };
    }

    private OrderSummaryResponse buildOrderSummary(Order order, PaymentResult paymentResult) {
        OrderStatusEnum orderStatus = mapTransactionStatusToOrderStatus(paymentResult.status());

        return OrderSummaryResponse.builder()
                .id(order.getId())
                .orderDate(order.getOrderDate())
                .totalAmount(order.getTotal())
                .status(orderStatus.getStatus())
                .paymentType(paymentResult.paymentMethod())
                .build();
    }

    private  Cart getCart(Customer customer) {
        Cart cart = customer.getCart();
        if (cart == null) {
            throw new ResourceNotFoundException("No Cart for Customer:" + customer.getId());
        }
        return cart;
    }

    public Order fetchAndValidateOrder(Integer orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.ORDER_NOT_FOUND.getFinalMessage(orderId)));
    }


}
