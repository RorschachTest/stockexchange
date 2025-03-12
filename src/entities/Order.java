package entities;

import java.time.LocalDateTime;

public class Order {
    public enum OrderType { BUY, SELL }
    public enum OrderStatus { OPEN, COMPLETED, CANCELLED }

    private final String orderId;
    private final String userId;
    private final String stockSymbol;
    private final OrderType orderType;
    private int quantity;
    private double price;
    private LocalDateTime orderAcceptedTimestamp;
    private OrderStatus orderStatus;

    public Order(String orderId, String userId, String stockSymbol, OrderType orderType, int quantity, double price) {
        this.orderId = orderId;
        this.userId = userId;
        this.stockSymbol = stockSymbol;
        this.orderType = orderType;
        this.quantity = quantity;
        this.price = price;
        this.orderAcceptedTimestamp = LocalDateTime.now();
        this.orderStatus = OrderStatus.OPEN;
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

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(double newPrice) {
        this.price = newPrice;
    }

    public void setOrderAcceptedTimestamp(LocalDateTime orderAcceptedTimestamp) {
        this.orderAcceptedTimestamp = orderAcceptedTimestamp;
    }

    @Override
    public String toString() {
        return "Order [orderId=" + orderId + ", userId=" + userId + ", stockSymbol=" + stockSymbol + ", orderType="
                + orderType + ", quantity=" + quantity + ", price=" + price + ", orderAcceptedTimestamp="
                + orderAcceptedTimestamp + ", orderStatus=" + orderStatus + "]";
    }
}
