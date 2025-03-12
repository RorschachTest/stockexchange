package engine;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import engine.matchingstrategy.MatchingStrategy;
import entities.Order;
import entities.Trade;
import exceptions.NoPendingOrderMatchException;

public class OrderBook {
    private final String stockSymbol;
    private final Queue<Order> buyOrders;
    private final Queue<Order> sellOrders;
    private final Lock lock;
    private MatchingStrategy matchingStrategy;

    public OrderBook(String stockSymbol, MatchingStrategy matchingStrategy) {
        this.stockSymbol = stockSymbol;
        this.buyOrders = new PriorityBlockingQueue<Order>(10, (o1, o2) -> Double.compare(o2.getPrice(), o1.getPrice())); // Max-heap for buy orders
        this.sellOrders = new PriorityBlockingQueue<Order>(10, (o1, o2) -> Double.compare(o1.getPrice(), o2.getPrice())); // Min-heap for sell orders
        this.lock = new ReentrantLock();
        this.matchingStrategy = matchingStrategy;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public Queue<Order> getBuyOrders() {
        return buyOrders;
    }

    public Queue<Order> getSellOrders() {
        return sellOrders;
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

    public void updateOrderPrice(Order order, double newPrice) throws NoPendingOrderMatchException {
        lock.lock();
        try {
            if (order.getOrderType() == Order.OrderType.BUY) {
                if (!buyOrders.contains(order)){
                    throw new NoPendingOrderMatchException();
                }
                buyOrders.remove(order);
                order.setPrice(newPrice);
                buyOrders.add(order);
            } else {
                if(!sellOrders.contains(order)){
                    throw new NoPendingOrderMatchException();
                }
                sellOrders.remove(order);
                order.setPrice(newPrice);
                sellOrders.add(order);
            }
        } finally {
            lock.unlock();
        }
    }

    public void updateOrderQuantity(Order order, int newQuantity) throws NoPendingOrderMatchException {
        lock.lock();
        try {
            if (order.getOrderType() == Order.OrderType.BUY) {
                if (!buyOrders.contains(order)){
                    throw new NoPendingOrderMatchException();
                }
                buyOrders.remove(order);
                order.setQuantity(newQuantity);
                buyOrders.add(order);
            } else {
                if(!sellOrders.contains(order)){
                    throw new NoPendingOrderMatchException();
                }
                sellOrders.remove(order);
                order.setQuantity(newQuantity);
                sellOrders.add(order);
            }
        } finally {
            lock.unlock();
        }
    }

    public void cancelOrder(Order order) throws NoPendingOrderMatchException {
        lock.lock();
        try {
            if (order.getOrderType() == Order.OrderType.BUY) {
                if(!buyOrders.contains(order)){
                    throw new NoPendingOrderMatchException();                    
                }
                buyOrders.remove(order);
            } else {
                if(!sellOrders.contains(order)){
                    throw new NoPendingOrderMatchException();
                }
                sellOrders.remove(order);
            }
        } finally {
            lock.unlock();
        }
    }

    List<Trade> matchOrders() {
        return matchingStrategy.matchOrders(buyOrders, sellOrders, this.stockSymbol);
    }
    
}
