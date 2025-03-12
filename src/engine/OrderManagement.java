package engine;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import entities.Order;
import entities.Trade;
import entities.Order.OrderStatus;
import exceptions.OrderNotFoundException;

public class OrderManagement {
    Map<String, Map<String, Order>> orderHistory;
    Map<String, String> orderToUserMapping;
    
    public OrderManagement() {
        this.orderHistory = new ConcurrentHashMap<>();
        this.orderToUserMapping = new ConcurrentHashMap<>();
    }

    public void addOrder(Order order) {
        orderHistory.computeIfAbsent(order.getUserId(), key -> new ConcurrentHashMap<>())
            .put(order.getOrderId(), order);
        orderToUserMapping.put(order.getOrderId(), order.getUserId());
    }

    public void updateOrder(Order order) throws OrderNotFoundException {
        if(!orderHistory.containsKey(order.getUserId())
        || !orderHistory.get(order.getUserId()).containsKey(order.getOrderId())
        || !orderToUserMapping.containsKey(order.getOrderId())) {
            throw new OrderNotFoundException("Order not found");
        }

        orderHistory.get(orderToUserMapping.get(order.getOrderId()))
            .put(order.getOrderId(), order);
    }

    public void updateOrderStatus(String orderId, OrderStatus status) throws OrderNotFoundException {
        if(!orderToUserMapping.containsKey(orderId)
        || !orderHistory.containsKey(orderToUserMapping.get(orderId))
        || !orderHistory.get(orderToUserMapping.get(orderId)).containsKey(orderId)) {
            throw new OrderNotFoundException("Order not found");
        }

        orderHistory.get(orderToUserMapping.get(orderId)).get(orderId).setStatus(status);
    }

    public void updateOrderStatus(List<Trade> trades) {
        trades.forEach(trade -> {
            try {
                updateOrderStatus(trade.getBuyerOrderId(), OrderStatus.COMPLETE);
                updateOrderStatus(trade.getSellerOrderId(), OrderStatus.COMPLETE);
            } catch (OrderNotFoundException e) {
                e.printStackTrace();
            }
        });
        
    }

}
