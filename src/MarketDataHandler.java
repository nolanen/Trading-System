import com.ib.client.Bar;
import com.ib.client.Contract;
import com.ib.client.ScannerSubscription;
import com.ib.client.TickAttrib;


import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class MarketDataHandler {

    final protected ClientSocketManager clientSocketManager;
    private MarketDataManagerCallback marketDataManagerCallback;

    private int tickerId = 0;
    private HashMap<Integer, String> tickerIdSymbol = new HashMap<>();

    private HashMap<String, Double> lastPriceBySymbol = new HashMap<>();
    private HashMap<String, Bar> prevBarBySymbol = new HashMap<>();

    public MarketDataHandler(ClientSocketManager clientSocketManager) {
        this.clientSocketManager = clientSocketManager;
    }

    public int getTickerId() {
        return tickerId++;
    }

    public void requestMarketDataFeed(Contract contract) {
        int id = getTickerId();
        clientSocketManager.clientSocket.reqMktData(id, contract, "", false, false,null);
        tickerIdSymbol.put(id, contract.symbol());
    }

    public void cancelMarketData(int tickerId) {
        clientSocketManager.clientSocket.cancelMktData(tickerId);
    }

    public void setMarketDataManagerCallback(MarketDataManagerCallback marketDataManagerCallback) {
        this.marketDataManagerCallback = marketDataManagerCallback;
    }

    public void tickPrice(int tickerId, int field, double price, TickAttrib attrib) {
        String symbol = tickerIdSymbol.get(tickerId);
        lastPriceBySymbol.put(symbol, price);
    }

    public double getLastPrice(String symbol) {
        return lastPriceBySymbol.get(symbol);
    }

    public void requestPrevOneMinBar(Contract contract) {
        int id = getTickerId();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm:ss");
        String formattedCurrentDateTime = ZonedDateTime.now(ZoneId.of("UTC")).withSecond(0).format(format);
        clientSocketManager.clientSocket.reqHistoricalData(id, contract, formattedCurrentDateTime,"60 S", "1 min", "TRADES", 1, 1, false, null);
        tickerIdSymbol.put(id, contract.symbol());
    }

    public void historicalData(int reqId, Bar bar) {
        String symbol = tickerIdSymbol.get(reqId);
        prevBarBySymbol.put(symbol, bar);
    }

    public Bar getPrevOneMinBar(String symbol) {
        return prevBarBySymbol.get(symbol);
    }

    public void requestUSMovers() {
        ScannerSubscription scannerSubscription = new ScannerSubscription();
        scannerSubscription.instrument("STK");
        scannerSubscription.locationCode("STK.US.MAJOR");
        scannerSubscription.scanCode("TOP_PERC_GAIN");
        scannerSubscription.belowPrice(10.00);
        scannerSubscription.aboveVolume(500000);
        scannerSubscription.numberOfRows(3);
        clientSocketManager.clientSocket.reqScannerSubscription(1, scannerSubscription, null, null);
    }

    public void onScannerResults(ArrayList<String> scannerResults) {
        clientSocketManager.clientSocket.cancelScannerSubscription(1);
        System.out.println(scannerResults);
        marketDataManagerCallback.onScannerResult(scannerResults.get(1));
    }
}
