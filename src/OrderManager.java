import com.ib.client.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class OrderManager {
    final private ClientSocketManager clientSocketManager;
    final private PositionManager positionManager;
    final private ExecutionManager executionManager;
    final private RiskManager riskManager;

    private OrderManagerCallback orderManagerCallback;

    private HashMap<String, OrderDetails> openOrders;
    private Queue<OrderDetails> orderQueue;
    private final Object openOrdersLock = new Object(); // Lock for synchronizing access to openOrders
    private final Object orderQueueLock = new Object(); // Lock for synchronizing access to openOrders
    private int nextOrderId;

    public OrderManager(ClientSocketManager clientSocketManager, PositionManager positionManager, ExecutionManager executionManager, RiskManager riskManager) {
        this.clientSocketManager = clientSocketManager;
        this.positionManager = positionManager;
        this.executionManager = executionManager;
        this.riskManager = riskManager;
        openOrders = new HashMap<>();
        orderQueue = new LinkedList<>();
        processOrderQueue();
    }

    public void setOrderManagerCallback(OrderManagerCallback orderManagerCallback) {
        this.orderManagerCallback = orderManagerCallback;
    }

    public Queue<OrderDetails> getOrderQueue() {
        return orderQueue;
    }

    public void reqNextOrderId() {
        clientSocketManager.clientSocket.reqIds(1);
    }

    public void setNextOrderId(int nextOrderId) {
        this.nextOrderId = nextOrderId;
    }

    public int getNextOrderId() {
        return nextOrderId++;
    }

    public void processOrderQueue() {
        new Thread(() -> {
            while (clientSocketManager.isConnected()) {
                if(!orderQueue.isEmpty()) {
                    OrderDetails newOrder = orderQueue.remove();
                    if(validateOrder(newOrder)) {
                        synchronized (openOrdersLock) {
                            openOrders.put(newOrder.getContract().symbol(), newOrder);
                        }
                        //calculate quantity
                        if(newOrder.getOrder().getAction().equals("BUY")) {
                            System.out.println(newOrder.getOrder().lmtPrice() + " " + riskManager.getPositionDollarSize());
                            double quantity = Math.floor(riskManager.getPositionDollarSize() / newOrder.getOrder().lmtPrice());
                            System.out.println(quantity);
                            newOrder.getOrder().totalQuantity(Decimal.get(quantity));
                        } else if (newOrder.getOrder().getAction().equals("SELL")) {
                            Decimal quantity = positionManager.getPositions().get(newOrder.getContract().symbol()).getQuantity();
                            newOrder.getOrder().totalQuantity(quantity);
                        }
                        executionManager.executeOrder(getNextOrderId(), newOrder.getContract(), newOrder.getOrder());
                    }
                }
            }
        }).start();
    }

    public boolean checkForOpenOrder(OrderDetails orderDetails) {
        synchronized (openOrdersLock) {

            if(openOrders.containsKey(orderDetails.getContract().symbol())) {
                return !openOrders.get(orderDetails.getContract().symbol()).getOrder().action().equals(orderDetails.getOrder().action());
            }
            return false;
        }
    }

    public boolean validateOrder(OrderDetails orderDetails) {
        if(checkForOpenOrder(orderDetails)) {
            System.out.println("Order declined. Existing order.");
            return false;
        }
        if(orderDetails.getOrder().getAction().equals("BUY") && positionManager.checkForLongPosition(orderDetails)) {
            return false;
        } else if(orderDetails.getOrder().getAction().equals("SELL") && positionManager.checkForLongPosition(orderDetails)) {
            return true;
        }

        return true;
    }

    public void onOrderExecution(Contract contract, Execution execution) {
        synchronized (openOrdersLock) {

            openOrders.remove(contract.symbol());
        }
        if(execution.side().equals("BOT")) {
            orderManagerCallback.onOpenLongPosition(contract.symbol());
        } else if(execution.side().equals("SLD")) {
            orderManagerCallback.onCloseLongPosition(contract.symbol());
        }
    }
}
