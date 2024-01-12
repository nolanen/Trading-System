import com.ib.client.Contract;
import com.ib.client.Order;

import java.util.Objects;

public class OrderDetails {
    private Contract contract;

    private Order order;

    public OrderDetails(Contract contract, Order order) {
        this.contract = contract;
        this.order = order;
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderDetails that = (OrderDetails) o;
        return Objects.equals(contract, that.contract) &&
                Objects.equals(order, that.order);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contract, order);
    }
}

