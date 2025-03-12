package engine;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// import entities.Order;
import entities.Trade;
import entities.Order.OrderStatus;
import exceptions.OrderNotFoundException;

public class OrderManagement {
    // Map<String, Map<String, Order>> orderHistory;
    // Map<String, String> orderToUserMapping;
    Map<String, OrderStatus> orderStatus;
    
    public OrderManagement() {
        this.orderStatus = new ConcurrentHashMap<>();
    }

    // public void setOrderStatus(String orderId, OrderStatus status) throws OrderNotFoundException {
    //     if(!orderToUserMapping.containsKey(orderId)
    //     || !orderHistory.containsKey(orderToUserMapping.get(orderId))
    //     || !orderHistory.get(orderToUserMapping.get(orderId)).containsKey(orderId)) {
    //         throw new OrderNotFoundException("Order not found");
    //     }

    //     orderHistory.get(orderToUserMapping.get(orderId)).get(orderId).setStatus(status);
    // }

    public void setOrderStatus(String orderId, OrderStatus status) throws OrderNotFoundException {
        if(!orderStatus.containsKey(orderId)) {
            throw new OrderNotFoundException("Order not found");
        }

        orderStatus.put(orderId, status);
    }

    public void updateOrderStatus(List<Trade> trades) {
        trades.forEach(trade -> {
            try {
                setOrderStatus(trade.getBuyerOrderId(), OrderStatus.COMPLETE);
                setOrderStatus(trade.getSellerOrderId(), OrderStatus.COMPLETE);
            } catch (OrderNotFoundException e) {
                e.printStackTrace();
            }
        });
        
    }

}
