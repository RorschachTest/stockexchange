package entities;

import java.time.LocalDateTime;

public class Order {
    public enum OrderType { BUY, SELL }
    public enum OrderStatus { OPEN, COMPLETE, CANCELLED }

    private final String orderId;
    private final String userId;
    private final String stockSymbol;
    private final OrderType orderType;
    private int quantity;
    private double price;
    private final LocalDateTime orderAcceptedTimestamp;
    private OrderStatus status;

    public Order(String orderId, String userId, String stockSymbol, OrderType orderType, int quantity, double price,
            LocalDateTime orderAcceptedTimestamp) {
        this.orderId = orderId;
        this.userId = userId;
        this.stockSymbol = stockSymbol;
        this.orderType = orderType;
        this.quantity = quantity;
        this.price = price;
        this.orderAcceptedTimestamp = orderAcceptedTimestamp;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getUserId() {
        return userId;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public LocalDateTime getOrderAcceptedTimestamp() {
        return orderAcceptedTimestamp;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(double newPrice) {
        this.price = newPrice;
    }
    
    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
