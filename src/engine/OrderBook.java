package engine;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import entities.Order;
import entities.Trade;

public class OrderBook {
    private final String stockSymbol;
    private final PriorityBlockingQueue<Order> buyOrders;
    private final PriorityBlockingQueue<Order> sellOrders;
    private final Lock lock;

    public OrderBook(String stockSymbol) {
        this.stockSymbol = stockSymbol;
        this.buyOrders = new PriorityBlockingQueue<>(10, (o1, o2) -> Double.compare(o2.getPrice(), o1.getPrice())); // Max-heap for buy orders
        this.sellOrders = new PriorityBlockingQueue<>(10, (o1, o2) -> Double.compare(o1.getPrice(), o2.getPrice())); // Min-heap for sell orders
        this.lock = new ReentrantLock();
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

     public List<Trade> matchOrders(Queue<Order> buyOrders, Queue<Order> sellOrders, String stockSymbol) {
        List<Trade> trades = new ArrayList<>();
        
        while (!buyOrders.isEmpty() && !sellOrders.isEmpty()) {
            Order buyOrder = buyOrders.peek();
            Order sellOrder = sellOrders.peek();

            if (buyOrder.getPrice() >= sellOrder.getPrice()) {
                // Match found
                double transactionPrice = (buyOrder.getOrderAcceptedTimestamp().isBefore(sellOrder.getOrderAcceptedTimestamp())) ? buyOrder.getPrice() : sellOrder.getPrice();
                int quantity = Math.min(buyOrder.getQuantity(), sellOrder.getQuantity());

                // Create transaction
                Trade transaction = new Trade("txn_" + System.currentTimeMillis(), buyOrder.getOrderId(), sellOrder.getOrderId(), stockSymbol, quantity, transactionPrice, LocalDateTime.now());
                System.out.println("Transaction executed: " + transaction);

                // Update order quantities
                if (buyOrder.getQuantity() > quantity) {
                    buyOrder.setQuantity(buyOrder.getQuantity() - quantity);
                } else {
                    buyOrders.poll();
                }

                if (sellOrder.getQuantity() > quantity) {
                    sellOrder.setQuantity(sellOrder.getQuantity() - quantity);
                } else {
                    sellOrders.poll();
                }

                trades.add(transaction);
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
