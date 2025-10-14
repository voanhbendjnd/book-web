package djnd.ben1607.drink_shop.mapper;

import djnd.ben1607.drink_shop.domain.entity.Order;
import djnd.ben1607.drink_shop.domain.entity.OrderItem;
import djnd.ben1607.drink_shop.domain.entity.User;
import djnd.ben1607.drink_shop.domain.request.OrderDTO;
import djnd.ben1607.drink_shop.domain.response.order.ResOrder;
import djnd.ben1607.drink_shop.domain.response.order.ResDataOrder;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for Order entity transformations
 * 
 * @Mapper: Đánh dấu interface này là MapStruct mapper
 * @Component: Đăng ký với Spring để có thể inject
 */
@Mapper(componentModel = "spring")
public interface OrderMapper {

    /**
     * Convert Order entity to OrderDTO
     * 
     * @param order Order entity
     * @return OrderDTO
     */
    @Mapping(source = "address.street", target = "street")
    @Mapping(source = "address.city", target = "city")
    @Mapping(source = "address.zipCode", target = "zipCode")
    OrderDTO toOrderDTO(Order order);

    /**
     * Convert OrderDTO to Order entity
     * 
     * @param orderDTO OrderDTO
     * @return Order entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "orderCreateDate", ignore = true)
    @Mapping(target = "orderUpdateDate", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "addressShipping", ignore = true)
    @Mapping(target = "phone", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "name", ignore = true)
    Order toOrder(OrderDTO orderDTO);

    /**
     * Update existing Order entity with data from OrderDTO
     * 
     * @param orderDTO OrderDTO
     * @param order    Existing Order entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "orderCreateDate", ignore = true)
    @Mapping(target = "orderUpdateDate", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "addressShipping", ignore = true)
    @Mapping(target = "phone", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "name", ignore = true)
    void updateOrderFromDTO(OrderDTO orderDTO, @MappingTarget Order order);

    /**
     * Convert Order entity to ResOrder (Response DTO)
     * 
     * @param order Order entity
     * @return ResOrder
     */
    @Mapping(source = "paymentMethod", target = "status")
    ResOrder toResOrder(Order order);

    /**
     * Convert Order entity to ResDataOrder (Response DTO)
     * 
     * @param order Order entity
     * @return ResDataOrder
     */
    @Mapping(source = "addressShipping", target = "address")
    @Mapping(source = "orderCreateDate", target = "createdAt")
    ResDataOrder toResDataOrder(Order order);

    /**
     * Convert List of Order entities to List of ResOrder
     * 
     * @param orders List of Order entities
     * @return List of ResOrder
     */
    List<ResOrder> toResOrderList(List<Order> orders);

    /**
     * Convert List of Order entities to List of OrderDTO
     * 
     * @param orders List of Order entities
     * @return List of OrderDTO
     */
    List<OrderDTO> toOrderDTOList(List<Order> orders);

    /**
     * Helper method to map OrderItems
     * 
     * @param orderItems List of OrderItem entities
     * @return List of mapped order items (can be customized based on ResOrderDTO
     *         structure)
     */
    @Named("mapOrderItems")
    default List<Object> mapOrderItems(List<OrderItem> orderItems) {
        if (orderItems == null) {
            return null;
        }
        // Trả về null, service layer sẽ xử lý việc mapping chi tiết
        // hoặc có thể tạo OrderItemMapper riêng nếu cần
        return null;
    }

    /**
     * Helper method to map User ID to User entity
     * Note: This will be handled in the service layer with proper user lookup
     * 
     * @param userId User ID
     * @return User entity (empty - to be populated in service)
     */
    default User mapUserIdToUser(Long userId) {
        // Trả về null, service layer sẽ xử lý việc tìm và set user
        return null;
    }
}
