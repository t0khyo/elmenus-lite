package spring.practice.elmenus_lite.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import spring.practice.elmenus_lite.dto.OrderSummary;
import spring.practice.elmenus_lite.model.Order;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "id", source = "order.id")
    @Mapping(target = "orderDate", source = "order.orderDate")
    @Mapping(target = "totalAmount", source = "order.total")
    @Mapping(target = "status", source = "order.orderStatus.name")
    @Mapping(target = "paymentType", source = "paymentType")
    OrderSummary toOrderSummary(Order order, String paymentType);
}