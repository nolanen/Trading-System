import com.ib.client.Contract;
import com.ib.client.Decimal;
import com.ib.client.Execution;
import com.ib.client.Order;

public class ExecutionManager {
    final private ClientSocketManager clientSocketManager;
    final private PositionManager positionManager;

    public ExecutionManager(ClientSocketManager clientSocketManager, PositionManager positionManager) {
        this.clientSocketManager = clientSocketManager;
        this.positionManager = positionManager;
    }

    public void executeOrder(int orderId, Contract contract, Order order) {
        if(clientSocketManager.clientSocket.isConnected()) {
            clientSocketManager.clientSocket.placeOrder(orderId, contract, order);
        }
    }

    public void onOrderExecution(int reqId, Contract contract, Execution execution) {
        if(execution.side().equals("BOT")) {
            if(!positionManager.checkForPosition(contract.symbol())) {
                positionManager.newPosition(contract.symbol(), execution.shares());
            }
        } else if(execution.side().equals("SLD")) {
            if(positionManager.checkForPosition(contract.symbol())) {
                positionManager.updatePosition(contract.symbol(), execution.shares().multiply(Decimal.MINUS_ONE));
            }
        }
        clientSocketManager.clientSocket.reqAccountSummary(9002, "All", "TotalCashValue");
    }

}
