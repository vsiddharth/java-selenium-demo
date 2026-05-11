package com.example.shop.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class Order {
    private String id;
    private String userId;
    private String userName;
    private List<OrderLine> lines;
    private BigDecimal total;
    private String status;
    private Instant createdAt;

    public Order() {}

    public Order(String id, String userId, String userName, List<OrderLine> lines,
                 BigDecimal total, String status, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.lines = lines;
        this.total = total;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public List<OrderLine> getLines() { return lines; }
    public void setLines(List<OrderLine> lines) { this.lines = lines; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
