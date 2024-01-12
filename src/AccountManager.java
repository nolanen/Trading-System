public class AccountManager {
    private final double maxPositionDollarValue;

    public AccountManager() {
        this.maxPositionDollarValue = 1000.00;
    }

    public double getMaxPositionDollarValue() {
        return maxPositionDollarValue;
    }
}
