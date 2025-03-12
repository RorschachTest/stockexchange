import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import engine.Exchange;
import entities.Order;
import entities.Stock;
import entities.User;
import exceptions.OrderNotFoundException;

public class Main {

    public static void runSingleThreadedTest(Exchange exchange, User user1, User user2) {
        // Place orders
        Order buyOrder = new Order("order1", user1.getUserId(), "AAPL", Order.OrderType.BUY, 10, 150.0);
        Order sellOrder = new Order("order2", user2.getUserId(), "AAPL", Order.OrderType.SELL, 5, 160.0);

        try {
            exchange.placeOrder(buyOrder);
            exchange.placeOrder(sellOrder);

            // Check order status
            System.out.println("Buy order status: " + exchange.getOrderStatus(buyOrder.getOrderId()));
            System.out.println("Sell order status: " + exchange.getOrderStatus(sellOrder.getOrderId()));

            // Update order price
            exchange.updateOrderPrice(buyOrder, 170.0);
            System.out.println("Updated buy order price: " + buyOrder.getPrice());

            // Update order quantity
            exchange.updateOrderQuantity(sellOrder, 5);
            System.out.println("Updated sell order quantity: " + sellOrder.getQuantity());

            // Cancel order
            exchange.cancelOrder(buyOrder);
            System.out.println("Buy order status after cancellation: " + exchange.getOrderStatus(buyOrder.getOrderId()));
            exchange.shutdown();
        } catch (OrderNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void runConcurrentTest(Exchange exchange, User user1, User user2) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        for (int i = 0; i < 10; i++) {
            Order buyOrder = new Order("order" + i, user1.getUserId(), "AAPL", Order.OrderType.BUY, 10, 150.0 + i);
            Order sellOrder = new Order("order" + (i + 10), user2.getUserId(), "AAPL", Order.OrderType.SELL, 5, 155.0 + i);
            Runnable placeOrdersTask = () -> {
                try {
                    exchange.placeOrder(buyOrder);
                    Thread.sleep(1000);
                    exchange.placeOrder(sellOrder);
                } catch (OrderNotFoundException| InterruptedException e) {
                    e.printStackTrace();
                }
            };
            executorService.submit(placeOrdersTask);
        };
        executorService.awaitTermination(100, TimeUnit.SECONDS);
    }

    public static void main(String[] args) {
        // Create stocks
        Stock stock1 = new Stock("1", "AAPL", "Apple Inc.");
        Stock stock2 = new Stock("2", "GOOGL", "Alphabet Inc.");

        // Create exchange
        Exchange exchange = new Exchange();
        exchange.addStock(stock1);
        exchange.addStock(stock2);

        // Create users
        User user1 = new User("1", "user1", "User One", "8527931355");
        User user2 = new User("2", "user2", "User Two", "8527931355");

        try{
            // Run concurrent test
            runConcurrentTest(exchange, user1, user2);
            // runSingleThreadedTest(exchange, user1, user2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
