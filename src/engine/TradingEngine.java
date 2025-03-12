package engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import engine.matchingstrategy.DefaultMatchingStrategy;
import entities.Order;
import entities.Trade;
import exceptions.NoPendingOrderMatchException;

public class TradingEngine {
    private final ConcurrentHashMap<String, OrderBook> orderBooks;
    private final ExecutorService executorService;

    public TradingEngine() {
        this.orderBooks = new ConcurrentHashMap<>();
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public Optional<List<Trade>> addOrder(Order order) {
        Future<List<Trade>> futureTrades = executorService.submit(() -> {
            OrderBook orderBook = orderBooks.computeIfAbsent(order.getStockSymbol(), k -> new OrderBook(order.getStockSymbol(), new DefaultMatchingStrategy()));
            orderBook.addOrder(order);
            return orderBook.matchOrders();
        });

        try {
            return Optional.ofNullable(futureTrades.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public Optional<List<Trade>> updateOrderPrice(Order order, double newPrice) {
        Future<List<Trade>> futureTrades = executorService.submit(() -> {
            try {
                OrderBook orderBook = orderBooks.get(order.getStockSymbol());
                if (orderBook != null) {
                    orderBook.updateOrderPrice(order, newPrice);
                    return orderBook.matchOrders();
                }
            } catch (NoPendingOrderMatchException e) {
                System.err.println("Order not found: " + e.getMessage());
            }
            return new ArrayList<>();
        });

        try {
            return Optional.ofNullable(futureTrades.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public Optional<List<Trade>> updateOrderQuantity(Order order, int newQuantity) {
        Future<List<Trade>> futureTrades = executorService.submit(() -> {
            try {
                OrderBook orderBook = orderBooks.get(order.getStockSymbol());
                if (orderBook != null) {
                    orderBook.updateOrderQuantity(order, newQuantity);
                    return orderBook.matchOrders();
                }
            } catch (NoPendingOrderMatchException e) {
                // Handle the exception (e.g., log it, rethrow it, etc.)
                System.err.println("Order not found: " + e.getMessage());
            }
            return new ArrayList<>();
        });

        try {
            return Optional.ofNullable(futureTrades.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public void cancelOrder(Order order) {
        executorService.submit(() -> {
            try {
                OrderBook orderBook = orderBooks.get(order.getStockSymbol());
                if (orderBook != null) {
                    orderBook.cancelOrder(order);
                }
            } catch (NoPendingOrderMatchException e) {
                System.err.println("Order not found: " + e.getMessage());
            }
        });
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
