import com.ib.client.Contract;
import com.ib.client.Decimal;
import com.ib.client.Order;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MomentumLong extends TradingStrategy {

    public MomentumLong(OrderManager orderManager, MarketDataHandler marketDataHandler, String symbol) {
        super(orderManager, marketDataHandler, symbol);
    }

    @Override
    public void run() {
//        marketDataHandler.requestUSMovers();
//        while (contract == null) {
//            System.out.println("Awaiting scanner data");
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        contract = new Contract();
//        contract.symbol("AAPL");
//        contract.secType("STK");
//        contract.exchange("SMART");
//        contract.currency("USD");
        System.out.println("MAIN " + contract.symbol());
        startRealTimeFeed();
        startHistoricalDataFetcher();
        startStrategyLogicThread();
    }

    private void startRealTimeFeed() {
        Thread realTimeFeed = new Thread(() -> marketDataHandler.requestMarketDataFeed(contract));
        realTimeFeed.start();
    }

    private void startHistoricalDataFetcher() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(() -> {
            if (marketDataHandler.clientSocketManager.isConnected()) {
                marketDataHandler.requestPrevOneMinBar(contract);
            }
        }, 60 - LocalDateTime.now().get(ChronoField.SECOND_OF_MINUTE), 60, TimeUnit.SECONDS);
    }

    private void startStrategyLogicThread() {
        Thread strategyLogicThread = new Thread(() -> {
            while (marketDataHandler.clientSocketManager.isConnected()) {
                waitForDataAvailability();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                double lastPrice = marketDataHandler.getLastPrice(contract.symbol());
                double prevHigh = marketDataHandler.getPrevOneMinBar(contract.symbol()).high();
                double prevLow = marketDataHandler.getPrevOneMinBar(contract.symbol()).low();

                System.out.println(contract.symbol() + " Price: " + lastPrice + " Last High: " + prevHigh + "Last Low: " + prevLow);

                if (lastPrice > prevHigh && (getOrderReadyState(contract.symbol()) == orderReadyState.BUY_STATE || getOrderReadyState(contract.symbol()) == null)) {
                    System.out.println("BUY");
                    orderManager.getOrderQueue().add(new OrderDetails(contract, new Order(){{
                        action("BUY");
                        totalQuantity(Decimal.get(1));
                        orderType("LMT");
                        lmtPrice(lastPrice);
                    }}));
                    setOrderReadyStateByPosition(contract.symbol(), orderReadyState.AWAITING_EXEC_CALLBACK);
                } else if (lastPrice < prevLow && getOrderReadyState(contract.symbol()) == orderReadyState.SELL_STATE) {
                    System.out.println("SELL");
                    orderManager.getOrderQueue().add(new OrderDetails(contract, new Order(){{
                        action("SELL");
                        totalQuantity(Decimal.get(1));
                        orderType("LMT");
                        lmtPrice(lastPrice);
                    }}));
                    setOrderReadyStateByPosition(contract.symbol(), orderReadyState.AWAITING_EXEC_CALLBACK);
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        strategyLogicThread.start();
    }

    private void waitForDataAvailability() {
        while (marketDataHandler.getPrevOneMinBar(contract.symbol()) == null || marketDataHandler.getLastPrice(contract.symbol()) == 0.0) {
            System.out.println("Awaiting data availability");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

