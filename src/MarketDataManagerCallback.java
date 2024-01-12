import com.ib.client.Contract;

public interface MarketDataManagerCallback {
    void onScannerResult(String symbol);
}
