import com.ib.client.Contract;
import java.util.HashMap;

abstract class TradingStrategy implements Runnable, OrderManagerCallback, MarketDataManagerCallback {
    protected MarketDataHandler marketDataHandler;
    protected OrderManager orderManager;

    protected Contract contract;

    protected enum orderReadyState {
        BUY_STATE,
        SELL_STATE,
        IDLE_STATE,
        AWAITING_EXEC_CALLBACK
    }

    private HashMap<String, orderReadyState> orderReadyStateByPosition = new HashMap<>();

    public TradingStrategy(OrderManager orderManager, MarketDataHandler marketDataHandler, String symbol) {
        contract = new Contract();
        contract.symbol(symbol);
        contract.secType("STK");
        contract.exchange("SMART");
        contract.currency("USD");
        this.orderManager = orderManager;
        this.orderManager.setOrderManagerCallback(this);
        this.marketDataHandler = marketDataHandler;
        this.marketDataHandler.setMarketDataManagerCallback(this);
    }

    @Override
    public abstract void run();

    public orderReadyState getOrderReadyState(String symbol) {
        return orderReadyStateByPosition.get(symbol);
    }

    public void setOrderReadyStateByPosition(String symbol, orderReadyState state) {
        orderReadyStateByPosition.put(symbol, state);
    }

    @Override
    public void onOpenLongPosition(String symbol) {

        orderReadyStateByPosition.put(symbol, orderReadyState.SELL_STATE);
    }
    @Override
    public void onCloseLongPosition(String symbol) {

        orderReadyStateByPosition.put(symbol, orderReadyState.BUY_STATE);
    }

    @Override
    public void onScannerResult(String symbol) {
        contract = new Contract();
        contract.symbol(symbol); //using aapl for testing, change to symbol to use scanner result
        contract.secType("STK");
        contract.exchange("SMART");
        contract.currency("USD");
    }
}


