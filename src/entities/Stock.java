package entities;

public class Stock {
    private final String stockId;
    private final String stockName;
    private final String symbol;

    public Stock(String stockId, String stockName, String symbol) {
        this.stockId = stockId;
        this.stockName = stockName;
        this.symbol = symbol;
    }

    public String getStockId() {
        return stockId;
    }

    public String getStockName() {
        return stockName;
    }

    public String getSymbol() {
        return symbol;
    }
    
}
