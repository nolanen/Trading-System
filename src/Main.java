import java.util.ArrayList;

public class Main {

    private static ArrayList<TradingStrategy> tradingStrategies = new ArrayList<>();
    public static void main(String[] args) {
        new EnvVariables();
        EWrapperImpl eWrapper = new EWrapperImpl();
        ClientSocketManager clientSocketManager = new ClientSocketManager(eWrapper);

        PositionManager positionManager = new PositionManager();
        ExecutionManager executionManager = new ExecutionManager(clientSocketManager, positionManager);
        eWrapper.setExecutionManager(executionManager);
        RiskManager riskManager = new RiskManager(clientSocketManager);
        eWrapper.setRiskManager(riskManager);
        OrderManager orderManager = new OrderManager(clientSocketManager, positionManager, executionManager, riskManager);
        eWrapper.setOrderManager(orderManager);
        MarketDataHandler marketDataHandler = new MarketDataHandler(clientSocketManager);
        eWrapper.setMarketDataHandler(marketDataHandler);

        tradingStrategies.add(new MomentumLong(orderManager, marketDataHandler, "AAPL"));
        tradingStrategies.add(new MomentumLong(orderManager, marketDataHandler, "NVDA"));

        for(TradingStrategy strategy: tradingStrategies) {
            new Thread(strategy).start();
        }

    }

}


// Error handling , Logging
// Build better Scanner implementation.