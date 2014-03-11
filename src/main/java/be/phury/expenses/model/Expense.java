package be.phury.expenses.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class Expense extends Entity {
    
    private Amount amount;
    private List<String> types;
    private String detail;
    private Long timestamp;
    private Long createdTimestamp;
    private Long lastModifiedTimestamp;
    private List<Expense> details;

    public Expense(String uuid) {
        super(uuid);
    }
    
    public Expense(double amount, String detail, Long timestamp, List<String> types) {
        this(amount, detail, timestamp, types, new Date().getTime());
    }

    public Expense(double amount, String detail, Long timestamp, List<String> types, Long createdTimestamp) {
        this(new Amount(amount), detail, timestamp, types, createdTimestamp, createdTimestamp);
    }
    
    public Expense(Amount amount, String detail, Long timestamp, List<String> types, Long createdTimestamp, Long lastModifiedTimestamp) {
        this.amount = amount;
        this.types = types;
        this.detail = detail;
        this.timestamp = timestamp;
        this.createdTimestamp = createdTimestamp;
        this.lastModifiedTimestamp = lastModifiedTimestamp;
    }

    public Amount getAmount() {
        return amount;
    }

    public List<String> getTypes() {
        return types;
    }

    public String getDetail() {
        return detail;
    }

    public String getTimestampAsString() {
        return formatTimestamp(getTimestamp());
    }
    
    public Long getTimestamp() {
        return timestamp;
    }

    public Long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public Long getLastModifiedTimestamp() {
        return lastModifiedTimestamp;
    }

    public List<Expense> getDetails() {
        return details;
    }
    
    public Expense addDetail(Expense expense) {
        getDetails().add(expense);
        return this;
    }
    
    private String formatTimestamp(Long timestamp) {
        return new SimpleDateFormat("dd MMMM yyyy").format(new Date(timestamp));
    }
}
