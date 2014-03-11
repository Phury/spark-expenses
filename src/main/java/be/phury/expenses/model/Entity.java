package be.phury.expenses.model;

import java.io.Serializable;
import java.util.UUID;

public abstract class Entity implements Serializable {
    
    private final String uuid;
    
    protected Entity() {
        this.uuid = UUID.randomUUID().toString();
    }
    
    protected Entity(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }
}
