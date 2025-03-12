package engine.matchingstrategy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import entities.Order;
import entities.Trade;

public class DefaultMatchingStrategy implements MatchingStrategy {

    @Override
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
}
