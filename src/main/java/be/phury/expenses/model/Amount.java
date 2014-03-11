package be.phury.expenses.model;

import java.io.Serializable;

public class Amount implements Serializable {
    private static final String DEFAULT_CURRENCY = "EUR";
    
    private final double value;
    private final String currency;
    
    public Amount(double value) {
        this.value = value;
        this.currency = DEFAULT_CURRENCY;
    }

    public double getValue() {
        return value;
    }

    public String getCurrency() {
        return currency;
    }
}
