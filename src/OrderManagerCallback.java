public interface OrderManagerCallback {
    void onOpenLongPosition(String symbol);
    void onCloseLongPosition(String symbol);
}
