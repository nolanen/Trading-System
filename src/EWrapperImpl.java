import com.ib.client.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EWrapperImpl implements EWrapper {
    private OrderManager orderManager;
    private MarketDataHandler marketDataHandler;
    private ExecutionManager executionManager;
    private RiskManager riskManager;
    private ArrayList<String> scannerResults = new ArrayList<>();
    public void setOrderManager(OrderManager orderManager) {
        this.orderManager = orderManager;
        orderManager.reqNextOrderId();
    }

    public void setRiskManager(RiskManager riskManager) {
        this.riskManager = riskManager;
    }

    public void setMarketDataHandler(MarketDataHandler marketDataHandler) {
        this.marketDataHandler = marketDataHandler;
    }

    public void setExecutionManager(ExecutionManager executionManager) {
        this.executionManager = executionManager;
    }

    @Override
    public void tickPrice(int tickerId, int field, double price, TickAttrib attrib) {
        marketDataHandler.tickPrice(tickerId, field, price, attrib);
    }

    @Override
    public void tickSize(int tickerId, int field, Decimal size) {

    }

    @Override
    public void tickOptionComputation(int tickerId, int field, int tickAttrib, double impliedVol, double delta, double optPrice, double pvDividend, double gamma, double vega, double theta, double undPrice) {

    }

    @Override
    public void tickGeneric(int tickerId, int tickType, double value) {

    }

    @Override
    public void tickString(int tickerId, int tickType, String value) {

    }

    @Override
    public void tickEFP(int tickerId, int tickType, double basisPoints, String formattedBasisPoints, double impliedFuture, int holdDays, String futureLastTradeDate, double dividendImpact, double dividendsToLastTradeDate) {

    }

    @Override
    public void orderStatus(int orderId, String status, Decimal filled, Decimal remaining, double avgFillPrice, int permId, int parentId, double lastFillPrice, int clientId, String whyHeld, double mktCapPrice) {

    }

    @Override
    public void openOrder(int orderId, Contract contract, Order order, OrderState orderState) {

    }

    @Override
    public void openOrderEnd() {

    }

    @Override
    public void updateAccountValue(String key, String value, String currency, String accountName) {

    }

    @Override
    public void updatePortfolio(Contract contract, Decimal position, double marketPrice, double marketValue, double averageCost, double unrealizedPNL, double realizedPNL, String accountName) {

    }

    @Override
    public void updateAccountTime(String timeStamp) {

    }

    @Override
    public void accountDownloadEnd(String accountName) {

    }

    @Override
    public void nextValidId(int orderId) {
        if(orderManager != null) {
            orderManager.setNextOrderId(orderId);
        }
    }

    @Override
    public void contractDetails(int reqId, ContractDetails contractDetails) {

    }

    @Override
    public void bondContractDetails(int reqId, ContractDetails contractDetails) {

    }

    @Override
    public void contractDetailsEnd(int reqId) {

    }

    @Override
    public void execDetails(int reqId, Contract contract, Execution execution) {
        executionManager.onOrderExecution(reqId, contract, execution);
        orderManager.onOrderExecution(contract, execution);
    }

    @Override
    public void execDetailsEnd(int reqId) {

    }

    @Override
    public void updateMktDepth(int tickerId, int position, int operation, int side, double price, Decimal size) {

    }

    @Override
    public void updateMktDepthL2(int tickerId, int position, String marketMaker, int operation, int side, double price, Decimal size, boolean isSmartDepth) {

    }

    @Override
    public void updateNewsBulletin(int msgId, int msgType, String message, String origExchange) {

    }

    @Override
    public void managedAccounts(String accountsList) {

    }

    @Override
    public void receiveFA(int faDataType, String xml) {

    }

    @Override
    public void historicalData(int reqId, Bar bar) {
        marketDataHandler.historicalData(reqId, bar);
    }

    @Override
    public void scannerParameters(String xml) {
        System.out.println(xml);
    }

    @Override
    public void scannerData(int reqId, int rank, ContractDetails contractDetails, String distance, String benchmark, String projection, String legsStr) {
        System.out.println(contractDetails.contract());
        System.out.println("\n\nSYMBOL: " + contractDetails.contract().symbol());
        scannerResults.add(contractDetails.contract().symbol());
        //marketDataHandler.onScannerResults(scannerResults);
    }

    @Override
    public void scannerDataEnd(int reqId) {
        System.out.println("End");
        marketDataHandler.onScannerResults(scannerResults);
    }

    @Override
    public void realtimeBar(int reqId, long time, double open, double high, double low, double close, Decimal volume, Decimal wap, int count) {

    }

    @Override
    public void currentTime(long time) {

    }

    @Override
    public void fundamentalData(int reqId, String data) {

    }

    @Override
    public void deltaNeutralValidation(int reqId, DeltaNeutralContract deltaNeutralContract) {

    }

    @Override
    public void tickSnapshotEnd(int reqId) {

    }

    @Override
    public void marketDataType(int reqId, int marketDataType) {

    }

    @Override
    public void commissionReport(CommissionReport commissionReport) {

    }

    @Override
    public void position(String account, Contract contract, Decimal pos, double avgCost) {

    }

    @Override
    public void positionEnd() {

    }

    @Override
    public void accountSummary(int reqId, String account, String tag, String value, String currency) {
        if(tag.equals("TotalCashValue")) {
            System.out.println(tag + ": " + value + " " + currency + " acc: " + account);
            if(currency.equals("CAD")) { // check for right account if multiple
                riskManager.setAccountCashAvailable(Double.parseDouble(value) * 0.74);
            }
        }
    }

    @Override
    public void accountSummaryEnd(int reqId) {

    }

    @Override
    public void verifyMessageAPI(String apiData) {

    }

    @Override
    public void verifyCompleted(boolean isSuccessful, String errorText) {

    }

    @Override
    public void verifyAndAuthMessageAPI(String apiData, String xyzChallenge) {

    }

    @Override
    public void verifyAndAuthCompleted(boolean isSuccessful, String errorText) {

    }

    @Override
    public void displayGroupList(int reqId, String groups) {

    }

    @Override
    public void displayGroupUpdated(int reqId, String contractInfo) {

    }

    @Override
    public void error(Exception e) {
        System.out.println(e);
    }

    @Override
    public void error(String str) {
        System.out.println(str);
    }

    @Override
    public void error(int id, int errorCode, String errorMsg, String advancedOrderRejectJson) {

    }

    @Override
    public void connectionClosed() {
        System.out.println("Disconnected from Interactive Brokers.");
    }

    @Override
    public void connectAck() {
        System.out.println("Connected to Interactive Brokers.");
    }

    @Override
    public void positionMulti(int reqId, String account, String modelCode, Contract contract, Decimal pos, double avgCost) {

    }

    @Override
    public void positionMultiEnd(int reqId) {

    }

    @Override
    public void accountUpdateMulti(int reqId, String account, String modelCode, String key, String value, String currency) {

    }

    @Override
    public void accountUpdateMultiEnd(int reqId) {

    }

    @Override
    public void securityDefinitionOptionalParameter(int reqId, String exchange, int underlyingConId, String tradingClass, String multiplier, Set<String> expirations, Set<Double> strikes) {

    }

    @Override
    public void securityDefinitionOptionalParameterEnd(int reqId) {

    }

    @Override
    public void softDollarTiers(int reqId, SoftDollarTier[] tiers) {

    }

    @Override
    public void familyCodes(FamilyCode[] familyCodes) {

    }

    @Override
    public void symbolSamples(int reqId, ContractDescription[] contractDescriptions) {

    }

    @Override
    public void historicalDataEnd(int reqId, String startDateStr, String endDateStr) {

    }

    @Override
    public void mktDepthExchanges(DepthMktDataDescription[] depthMktDataDescriptions) {

    }

    @Override
    public void tickNews(int tickerId, long timeStamp, String providerCode, String articleId, String headline, String extraData) {

    }

    @Override
    public void smartComponents(int reqId, Map<Integer, Map.Entry<String, Character>> theMap) {

    }

    @Override
    public void tickReqParams(int tickerId, double minTick, String bboExchange, int snapshotPermissions) {

    }

    @Override
    public void newsProviders(NewsProvider[] newsProviders) {

    }

    @Override
    public void newsArticle(int requestId, int articleType, String articleText) {

    }

    @Override
    public void historicalNews(int requestId, String time, String providerCode, String articleId, String headline) {

    }

    @Override
    public void historicalNewsEnd(int requestId, boolean hasMore) {

    }

    @Override
    public void headTimestamp(int reqId, String headTimestamp) {

    }

    @Override
    public void histogramData(int reqId, List<HistogramEntry> items) {

    }

    @Override
    public void historicalDataUpdate(int reqId, Bar bar) {

    }

    @Override
    public void rerouteMktDataReq(int reqId, int conId, String exchange) {

    }

    @Override
    public void rerouteMktDepthReq(int reqId, int conId, String exchange) {

    }

    @Override
    public void marketRule(int marketRuleId, PriceIncrement[] priceIncrements) {

    }

    @Override
    public void pnl(int reqId, double dailyPnL, double unrealizedPnL, double realizedPnL) {

    }

    @Override
    public void pnlSingle(int reqId, Decimal pos, double dailyPnL, double unrealizedPnL, double realizedPnL, double value) {

    }

    @Override
    public void historicalTicks(int reqId, List<HistoricalTick> ticks, boolean done) {

    }

    @Override
    public void historicalTicksBidAsk(int reqId, List<HistoricalTickBidAsk> ticks, boolean done) {

    }

    @Override
    public void historicalTicksLast(int reqId, List<HistoricalTickLast> ticks, boolean done) {

    }

    @Override
    public void tickByTickAllLast(int reqId, int tickType, long time, double price, Decimal size, TickAttribLast tickAttribLast, String exchange, String specialConditions) {

    }

    @Override
    public void tickByTickBidAsk(int reqId, long time, double bidPrice, double askPrice, Decimal bidSize, Decimal askSize, TickAttribBidAsk tickAttribBidAsk) {

    }

    @Override
    public void tickByTickMidPoint(int reqId, long time, double midPoint) {

    }

    @Override
    public void orderBound(long orderId, int apiClientId, int apiOrderId) {

    }

    @Override
    public void completedOrder(Contract contract, Order order, OrderState orderState) {

    }

    @Override
    public void completedOrdersEnd() {

    }

    @Override
    public void replaceFAEnd(int reqId, String text) {

    }

    @Override
    public void wshMetaData(int reqId, String dataJson) {

    }

    @Override
    public void wshEventData(int reqId, String dataJson) {

    }

    @Override
    public void historicalSchedule(int reqId, String startDateTime, String endDateTime, String timeZone, List<HistoricalSession> sessions) {

    }

    @Override
    public void userInfo(int reqId, String whiteBrandingId) {

    }
}
