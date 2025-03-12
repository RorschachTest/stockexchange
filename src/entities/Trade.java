package entities;

import java.time.LocalDateTime;

public class Trade {
    private final String tradeId;
    private final String buyerOrderId;
    private final String sellerOrderId;
    private final String stockSymbol;
    private final int quantity;
    private final double price;
    private final LocalDateTime tradeTime;

    public Trade(String tradeId, String buyerOrderId, String sellerOrderId, String stockSymbol, int quantity, double price, LocalDateTime tradeTime) {
        this.tradeId = tradeId;
        this.buyerOrderId = buyerOrderId;
        this.sellerOrderId = sellerOrderId;
        this.stockSymbol = stockSymbol;
        this.quantity = quantity;
        this.price = price;
        this.tradeTime = tradeTime;
    }

    public String getTradeId() {
        return tradeId;
    }

    public String getBuyerOrderId() {
        return buyerOrderId;
    }

    public String getSellerOrderId() {
        return sellerOrderId;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public LocalDateTime getTradeTime() {
        return tradeTime;
    }

    @Override
    public String toString() {
        return "Trade [tradeId=" + tradeId + ", buyerOrderId=" + buyerOrderId + ", sellerOrderId=" + sellerOrderId
                + ", stockSymbol=" + stockSymbol + ", quantity=" + quantity + ", price=" + price + ", tradeTime="
                + tradeTime + "]";
    }
}
