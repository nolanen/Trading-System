public class RiskManager {

    private ClientSocketManager clientSocketManager;
    private final double maxPositionDollarValue;
    private double accountCashAvailable;

    // need to convert cad to usd
    // currently manually calculating exchange rate in ewrapper callback

    public RiskManager(ClientSocketManager clientSocketManager) {
        this.clientSocketManager = clientSocketManager;
        clientSocketManager.clientSocket.reqAccountSummary(9001, "All", "TotalCashValue");
        this.maxPositionDollarValue = 1000.00;
    }

    public void setAccountCashAvailable(double accountCashAvailable) {
        this.accountCashAvailable = accountCashAvailable;
    }

    public double getPositionDollarSize() {
        return accountCashAvailable < maxPositionDollarValue ? accountCashAvailable : maxPositionDollarValue;
    }

    public double getMaxPositionDollarValue() {
        return maxPositionDollarValue;
    }
}
