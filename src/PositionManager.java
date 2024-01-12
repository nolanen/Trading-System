import com.ib.client.Contract;
import com.ib.client.Decimal;
import com.ib.controller.Position;

import java.util.HashMap;

public class PositionManager {
    private HashMap<String, Position> positions;

    private PositionManagerCallback positionManagerCallback;

    public PositionManager() {
        positions = new HashMap<>();
    }

    public HashMap<String, Position> getPositions() {
        return positions;
    }
    public void newPosition(String symbol, Decimal quantity) {
        positions.put(symbol, new Position(symbol, quantity));
    }

    public void updatePosition(String symbol, Decimal quantity) {
        if(positions.get(symbol) != null) {
            positions.get(symbol).update(quantity);
            if(positions.get(symbol).getQuantity().isZero()) {
                positions.remove(symbol);
            }
        }
    }

    public boolean checkForLongPosition(OrderDetails orderDetails) {
        return positions.containsKey(orderDetails.getContract().symbol());
    }

    public boolean checkForPosition(String symbol) {
        return positions.containsKey(symbol);
    }

    public void exitAllPositions() {
        positions.forEach((symbol, position) -> {
            // if long
            //if short
        });
    }

    public class Position {
        private String symbol;
        private Decimal quantity;

        public Position(String symbol, Decimal quantity) {
            this.symbol = symbol;
            this.quantity = quantity;
        }

        public void update(Decimal quantity) {

            this.quantity = this.quantity.add(quantity);

        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }


        public Decimal getQuantity() {
            return quantity;
        }

        public void setQuantity(Decimal quantity) {
            this.quantity = quantity;
        }
    }

}
