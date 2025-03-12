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

    public void addOrder(Order order) {
        executorService.submit(() -> {
            OrderBook orderBook = orderBooks.computeIfAbsent(order.getStockSymbol(), k -> new OrderBook(order.getStockSymbol(), new DefaultMatchingStrategy()));
            orderBook.addOrder(order);
            List<Trade> trades = orderBook.matchOrders();
            orderManagement.updateOrderStatus(trades);
        });
    }

    public List<Trade> updateOrderPrice(Order order, double newPrice) {
        Future<List<Trade>> futureTrades = executorService.submit(() -> {
            OrderBook orderBook = orderBooks.get(order.getStockSymbol());
            if (orderBook != null) {
            orderBook.updateOrderPrice(order, newPrice);
            }
            List<Trade> trades = orderBook.matchOrders();
            return trades;
        });

        try {
            List<Trade> trades = futureTrades.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void updateOrderQuantity(Order order, int newQuantity) {
        executorService.submit(() -> {
            OrderBook orderBook = orderBooks.get(order.getStockSymbol());
            if (orderBook != null) {
                orderBook.updateOrderQuantity(order, newQuantity);
            }
            List<Trade> trades = orderBook.matchOrders();
            orderManagement.updateOrderStatus(trades);
        });
    }

    public void cancelOrder(Order order) {
        executorService.submit(() -> {
            OrderBook orderBook = orderBooks.get(order.getStockSymbol());
            if (orderBook != null) {
                orderBook.cancelOrder(order);
            }
            List<Trade> trades = orderBook.matchOrders();
            orderManagement.updateOrderStatus(trades);
        });
    }

}
