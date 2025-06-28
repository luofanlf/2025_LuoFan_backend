package com.oracle.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class CoinResponse {

    @JsonProperty("totalAmount")
    private BigDecimal totalAmount;
    
    @JsonProperty("solution")
    private Map<String, Integer> solution; 
    
    @JsonProperty("totalCoins")
    private Integer totalCoins; 
    


    // 构造函数
    public CoinResponse() {}
    
    public CoinResponse(BigDecimal totalAmount, Map<String, Integer> solution, 
                       Integer totalCoins) {
        this.totalAmount = totalAmount;
        this.solution = solution;
        this.totalCoins = totalCoins;
    }
    
    // 静态工厂方法
    public static CoinResponse success(BigDecimal totalAmount, Map<String, Integer> solution) {
        int totalCoins = solution.values().stream().mapToInt(Integer::intValue).sum();
        return new CoinResponse(totalAmount, solution, totalCoins);
    }
    
    public static CoinResponse impossible(BigDecimal totalAmount) {
        return new CoinResponse(totalAmount, new HashMap<>(), 0);
    }
    
    // getter/setter
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    
    public Map<String, Integer> getSolution() { return solution; }
    public void setSolution(Map<String, Integer> solution) { this.solution = solution; }
    
    public Integer getTotalCoins() { return totalCoins; }
    public void setTotalCoins(Integer totalCoins) { this.totalCoins = totalCoins; }
    

}