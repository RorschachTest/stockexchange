package engine;

import java.util.Map;

import entities.Order;
import entities.Stock;

public class Exchange {
    private final Map<String, Stock> stocks;
    private final TradingEngine tradingEngine;
    private final OrderManagement orderManagement;

    public Exchange(Map<String, Stock> stocks) {
        this.stocks = stocks;
        this.orderManagement = new OrderManagement();
        this.tradingEngine = new TradingEngine();
    }

    public void addStock(Stock stock) {
        stocks.put(stock.getSymbol(), stock);
    }

    public void placeOrder(Order order) {
        tradingEngine.addOrder(order);
    }

    public void updateOrderPrice(Order order, double newPrice) {
        tradingEngine.updateOrderPrice(order, newPrice);
    }

    public void updateOrderQuantity(Order order, int newQuantity) {
        tradingEngine.updateOrderQuantity(order, newQuantity);
    }

    public void cancelOrder(Order order) {
        tradingEngine.cancelOrder(order);
    }
    
}
