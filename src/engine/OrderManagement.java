package engine;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import entities.Trade;
import entities.Order.OrderStatus;
import exceptions.OrderNotFoundException;

public class OrderManagement {
    Map<String, OrderStatus> orderStatus;
    
    public OrderManagement() {
        this.orderStatus = new ConcurrentHashMap<>();
    }

    public void setOrderStatus(String orderId, OrderStatus status) {
        orderStatus.put(orderId, status);
    }

    public void updateOrderStatus(List<Trade> trades) {
        trades.forEach(trade -> {
            setOrderStatus(trade.getBuyerOrderId(), OrderStatus.COMPLETED);
            setOrderStatus(trade.getSellerOrderId(), OrderStatus.COMPLETED);
        });
    }

    public OrderStatus getOrderStatus(String orderId) throws OrderNotFoundException {
        if(!orderStatus.containsKey(orderId)) {
            throw new OrderNotFoundException();
        }
        return orderStatus.get(orderId);
    }

}
