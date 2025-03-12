package engine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import engine.matchingstrategy.MatchingStrategy;
import entities.Order;
import entities.Trade;

public class OrderBook {
    private final String stockSymbol;
    private final PriorityBlockingQueue<Order> buyOrders;
    private final PriorityBlockingQueue<Order> sellOrders;
    private final Lock lock;
    private final MatchingStrategy matchingStrategy;

    public OrderBook(String stockSymbol, MatchingStrategy matchingStrategy) {
        this.stockSymbol = stockSymbol;
        this.buyOrders = new PriorityBlockingQueue<>((o1, o2) -> Double.compare(o2.getPrice(), o1.getPrice())); // Max-heap for buy orders
        this.sellOrders = new PriorityBlockingQueue<>((o1, o2) -> Double.compare(o1.getPrice(), o2.getPrice())); // Min-heap for sell orders
        this.lock = new ReentrantLock();
        this.matchingStrategy = matchingStrategy;
    }

    public void addOrder(Order order) {
        lock.lock();
        try {
            if (order.getOrderType() == Order.OrderType.BUY) {
                buyOrders.add(order);
            } else {
                sellOrders.add(order);
            }
        } finally {
            lock.unlock();
        }
    }

    public List<Trade> matchOrders() {
        List<Trade> trades = new ArrayList<>();
        while (!buyOrders.isEmpty() && !sellOrders.isEmpty()) {
            Trade trade = matchingStrategy.matchOrders(buyOrders, sellOrders, stockSymbol);
            if (trade != null) {
                trades.add(trade);
            } else {
                break;
            }
        }
        return trades;
    }

    public void updateOrderPrice(Order order, double newPrice) {
        lock.lock();
        try {
            if (order.getOrderType() == Order.OrderType.BUY) {
                buyOrders.remove(order);
                order.setPrice(newPrice);
                buyOrders.add(order);
            } else {
                sellOrders.remove(order);
                order.setPrice(newPrice);
                sellOrders.add(order);
            }
        } finally {
            lock.unlock();
        }
    }

    public void updateOrderQuantity(Order order, int newQuantity) {
        lock.lock();
        try {
            if (order.getOrderType() == Order.OrderType.BUY) {
                buyOrders.remove(order);
                order.setQuantity(newQuantity);
                buyOrders.add(order);
            } else {
                sellOrders.remove(order);
                order.setQuantity(newQuantity);
                sellOrders.add(order);
            }
        } finally {
            lock.unlock();
        }
    }

    public void cancelOrder(Order order) {
        lock.lock();
        try {
            if (order.getOrderType() == Order.OrderType.BUY) {
                buyOrders.remove(order);
            } else {
                sellOrders.remove(order);
            }
        } finally {
            lock.unlock();
        }
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public PriorityBlockingQueue<Order> getBuyOrders() {
        return buyOrders;
    }

    public PriorityBlockingQueue<Order> getSellOrders() {
        return sellOrders;
    }
}
