package engine;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import engine.matchingstrategy.DefaultMatchingStrategy;
import entities.Order;
import entities.Trade;

public class TradingEngine {
    private final ConcurrentHashMap<String, OrderBook> orderBooks;
    private final ConcurrentHashMap<String, BlockingQueue<Order>> orderQueues;
    private final ExecutorService orderExecutorService;
    private final ExecutorService matchingExecutorService;
    private final OrderManagement orderManagement;
    private volatile boolean running = true;

    public TradingEngine(OrderManagement orderManagement) {
        this.orderBooks = new ConcurrentHashMap<>();
        this.orderQueues = new ConcurrentHashMap<>();
        this.orderExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.matchingExecutorService = Executors.newCachedThreadPool();
        this.orderManagement = orderManagement;

        this.matchingExecutorService.submit(this::matchOrdersContinuously);
    }

    public void addOrder(Order order) {
        orderExecutorService.submit(() -> {
            OrderBook orderBook = orderBooks.computeIfAbsent(order.getStockSymbol(), k -> new OrderBook(order.getStockSymbol()));
            orderBook.addOrder(order);
            BlockingQueue<Order> orderQueue = orderQueues.computeIfAbsent(order.getStockSymbol(), k -> new LinkedBlockingQueue<>());
            orderQueue.offer(order);
            synchronized (orderQueue) {
                orderQueue.notify();
            }
        });
    }

    public void updateOrderPrice(Order order, double newPrice) {
        orderExecutorService.submit(() -> {
            OrderBook orderBook = orderBooks.get(order.getStockSymbol());
            if (orderBook != null) {
                orderBook.updateOrderPrice(order, newPrice);
                BlockingQueue<Order> orderQueue = orderQueues.computeIfAbsent(order.getStockSymbol(), k -> new LinkedBlockingQueue<>());
                orderQueue.offer(order);
                synchronized (orderQueue) {
                    orderQueue.notify();
                }
            }
        });
    }

    public void updateOrderQuantity(Order order, int newQuantity) {
        orderExecutorService.submit(() -> {
            OrderBook orderBook = orderBooks.get(order.getStockSymbol());
            if (orderBook != null) {
                orderBook.updateOrderQuantity(order, newQuantity);
                BlockingQueue<Order> orderQueue = orderQueues.computeIfAbsent(order.getStockSymbol(), k -> new LinkedBlockingQueue<>());
                orderQueue.offer(order);
                synchronized (orderQueue) {
                    orderQueue.notify();
                }
            }
        });
    }

    public void cancelOrder(Order order) {
        orderExecutorService.submit(() -> {
            OrderBook orderBook = orderBooks.get(order.getStockSymbol());
            if (orderBook != null) {
                orderBook.cancelOrder(order);
                BlockingQueue<Order> orderQueue = orderQueues.computeIfAbsent(order.getStockSymbol(), k -> new LinkedBlockingQueue<>());
                orderQueue.offer(order);
                synchronized (orderQueue) {
                    orderQueue.notify();
                }
            }
        });
    }

    private void matchOrdersContinuously() {
        while (running) {
            for (String stockSymbol : orderQueues.keySet()) {
                BlockingQueue<Order> orderQueue = orderQueues.get(stockSymbol);
                matchingExecutorService.submit(() -> {
                    while (running) {
                        try {
                            Order order = orderQueue.take(); // Wait for a new order
                            OrderBook orderBook = orderBooks.get(stockSymbol);
                            if (orderBook != null) {
                                List<Trade> trades = orderBook.matchOrders();
                                orderManagement.updateOrder(trades);
                            }
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                });
            }
        }
    }

    public void shutdown() {
        running = false;
        matchingExecutorService.shutdownNow();
        orderExecutorService.shutdown();
    }
}
