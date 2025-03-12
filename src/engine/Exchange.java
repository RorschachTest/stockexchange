package engine;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import entities.Order;
import entities.Stock;
import exceptions.OrderNotFoundException;
import entities.Order.OrderStatus;

public class Exchange {
    private final Map<String, Stock> stocks;
    private final TradingEngine tradingEngine;
    private final OrderManagement orderManagement;

    public Exchange() {
        this.stocks = new ConcurrentHashMap<>();
        this.orderManagement = new OrderManagement();
        this.tradingEngine = new TradingEngine();
    }

    public void addStock(Stock stock) {
        stocks.put(stock.getSymbol(), stock);
    }

    public void placeOrder(Order order) throws OrderNotFoundException {
        orderManagement.setOrderStatus(order.getOrderId(), OrderStatus.OPEN);
        tradingEngine.addOrder(order).ifPresent(orderManagement::updateOrderStatus);
    }

    public void updateOrderPrice(Order order, double newPrice) {
        tradingEngine.updateOrderPrice(order, newPrice);
    }

    public void updateOrderQuantity(Order order, int newQuantity) {
        tradingEngine.updateOrderQuantity(order, newQuantity);
    }

    public void cancelOrder(Order order) {
        orderManagement.setOrderStatus(order.getOrderId(), OrderStatus.CANCELLED);
        tradingEngine.cancelOrder(order);
    }

    public OrderStatus getOrderStatus(String orderId) throws OrderNotFoundException {
        return orderManagement.getOrderStatus(orderId);
    }

    public void shutdown() {
        tradingEngine.shutdown();
    }
    
}
