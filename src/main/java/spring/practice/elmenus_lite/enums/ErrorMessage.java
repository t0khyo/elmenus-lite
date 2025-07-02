package spring.practice.elmenus_lite.enums;

import lombok.RequiredArgsConstructor;

import java.text.MessageFormat;

@RequiredArgsConstructor
public enum ErrorMessage {

    CART_ITEM_NOT_FOUND("Cart Item not found with id: {0}"),
    CART_NOT_FOUND("Cart not found with id: {0}"),
    CART_NOT_FOUND_FOR_CUSTOMER("Cart Not found with customer id: {0}"),
    CUSTOMER_NOT_FOUND("Customer not found with id: {0}"),
    MENU_ITEM_NOT_FOUND("Menu item not found with id: {0}"),
    ORDER_STATUS_NOT_FOUND("Order status not found: {0}"),
    RESTAURANT_NOT_FOUND("Restaurant not found with ID: {0}"),
    ADDRESS_NOT_FOUND("Address not found with ID: {0}"),
    PAYMENT_SETTING_NOT_FOUND("Payment setting not found with ID: {0}"),
    PROMOTION_NOT_FOUND("Promotion not found with code: {0}"),
    ENTITY_OWNERSHIP_VIOLATION("{0} does not belong to the specified customer"),
    RESTAURANT_INACTIVE("Restaurant is currently inactive"),
    PROMOTION_EXPIRED("Promotion code is not valid or has expired"),
    CART_EMPTY("Cart is empty"),
    MENU_ITEM_WRONG_RESTAURANT("Menu item {0} does not belong to the specified restaurant"),
    MENU_ITEM_UNAVAILABLE("Menu item {0} is currently unavailable");

    private final String messageTemplate;

    public String getFinalMessage(Object... params) {
        return MessageFormat.format(messageTemplate, params);
    }
}