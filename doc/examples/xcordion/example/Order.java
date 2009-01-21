package xcordion.example;

import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class Order {
    static private BigDecimal TAX_RATE = new BigDecimal("0.175");

    private List<Item> items = new ArrayList<Item>();
    private boolean takeaway;

    public List<Item> getItems() {
        return items;
    }

    public boolean isTakeaway() {
        return takeaway;
    }

    public void setTakeaway(boolean takeaway) {
        this.takeaway = takeaway;
    }

    public BigDecimal getTax() {
        if (takeaway) {
            return BigDecimal.ZERO;
        }
        BigDecimal result = BigDecimal.ZERO;
        for (Item i : items) {
            result = result.add(i.getPrice().multiply(new BigDecimal(i.getQuantity())).multiply(TAX_RATE));
        }
        result = result.setScale(2, RoundingMode.HALF_UP);
        return result;
    }

    public BigDecimal getTotal() {
        BigDecimal result = BigDecimal.ZERO;
        for (Item i : items) {
            result = result.add(i.getPrice().multiply(new BigDecimal(i.getQuantity())));
        }
        return result.add(getTax());
    }
}
