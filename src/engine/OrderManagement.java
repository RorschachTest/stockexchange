package engine;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import entities.Order;
import entities.Trade;
import exceptions.OrderAlreadyExistsException;
import exceptions.OrderNotFoundException;

public class OrderManagement {
    Map<String, Map<String, Order>> orderHistory;
    Map<String, String> orderToUserMapping;
    
    public OrderManagement() {
        this.orderHistory = new ConcurrentHashMap<>();
        this.orderToUserMapping = new ConcurrentHashMap<>();
    }

    public void addOrder(Order order)  throws OrderAlreadyExistsException {
        orderHistory.computeIfAbsent(order.getUserId(), k -> new ConcurrentHashMap<>())
            .put(order.getOrderId(), order);
        orderToUserMapping.put(order.getOrderId(), order.getUserId());
    }

    public void updateOrder(Order order) throws OrderNotFoundException {
        if (!orderHistory.containsKey(order.getUserId()) 
        || !orderHistory.get(order.getUserId()).containsKey(order.getOrderId())
        || !orderToUserMapping.containsKey(order.getOrderId())
        ) {
            throw new OrderNotFoundException("Order with ID " + order.getOrderId() + " not found.");
        }
        orderHistory.get(order.getUserId()).put(order.getOrderId(), order);
    }

    public void updateOrderStatus(List<Trade> trades) {

    }

    public void updateOrderStatus(Trade trade) {

    }
}
