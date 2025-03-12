package engine.matchingstrategy;

import java.util.List;
import java.util.Queue;

import entities.Order;
import entities.Trade;

public interface MatchingStrategy {
    List<Trade> matchOrders(Queue<Order> buyOrders, Queue<Order> sellOrders, String stockSymbol);
}
