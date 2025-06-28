package com.oracle.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

public class CoinRequest {

    @JsonProperty("totalAmount")
    @NotNull(message = "total amount is required")
    @DecimalMin(value = "0.01", message = "total amount must be greater than 0")
    private BigDecimal totalAmount;
    
    @JsonProperty("denominations")
    @NotEmpty(message = "denominations is required")
    @Size(min = 1, message = "denominations must be greater than 0")
    private List<BigDecimal> denominations;
    
    // constructor
    public CoinRequest() {}
    
    public CoinRequest(BigDecimal totalAmount, List<BigDecimal> denominations) {
        this.totalAmount = totalAmount;
        this.denominations = denominations;
    }
    
    // getter/setter
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    
    public List<BigDecimal> getDenominations() { return denominations; }
    public void setDenominations(List<BigDecimal> denominations) { this.denominations = denominations; }
}