package engine;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import engine.matchingstrategy.DefaultMatchingStrategy;
import entities.Order;
import entities.Trade;

public class TradingEngine {
    private final ConcurrentHashMap<String, OrderBook> orderBooks;
    private final ExecutorService executorService;

    public TradingEngine() {
        this.orderBooks = new ConcurrentHashMap<>();
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }
    public List<Trade> addOrder(Order order) {
        Future<List<Trade>> futureTrades = executorService.submit(() -> {
            OrderBook orderBook = orderBooks.computeIfAbsent(order.getStockSymbol(), k -> new OrderBook(order.getStockSymbol(), new DefaultMatchingStrategy()));
            orderBook.addOrder(order);
            return orderBook.matchOrders();
        });

        try {
            return futureTrades.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Trade> updateOrderPrice(Order order, double newPrice) {
        Future<List<Trade>> futureTrades = executorService.submit(() -> {
            OrderBook orderBook = orderBooks.get(order.getStockSymbol());
            if (orderBook != null) {
                orderBook.updateOrderPrice(order, newPrice);
            }
            return orderBook.matchOrders();
        });

        try {
            return futureTrades.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }
    public List<Trade> updateOrderQuantity(Order order, int newQuantity) {
        Future<List<Trade>> futureTrades = executorService.submit(() -> {
            OrderBook orderBook = orderBooks.get(order.getStockSymbol());
            if (orderBook != null) {
                orderBook.updateOrderQuantity(order, newQuantity);
            }
            return orderBook.matchOrders();
        });

        try {
            return futureTrades.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Trade> cancelOrder(Order order) {
        Future<List<Trade>> futureTrades = executorService.submit(() -> {
            OrderBook orderBook = orderBooks.get(order.getStockSymbol());
            if (orderBook != null) {
                orderBook.cancelOrder(order);
            }
            return orderBook.matchOrders();
        });

        try {
            return futureTrades.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }
}
