package com.oracle.service;

import com.oracle.dto.request.CoinRequest;
import com.oracle.dto.response.CoinResponse;
import java.math.BigDecimal;
import java.util.*;

public class CoinService {

    public CoinResponse calculateCoinChange(CoinRequest request) {
        BigDecimal totalAmount = request.getTotalAmount();
        List<BigDecimal> denominations = request.getDenominations();
        
        // 转换为分（避免浮点数精度问题）
        int targetCents = totalAmount.multiply(new BigDecimal("100")).intValue();
        int[] coinsCents = denominations.stream()
            .mapToInt(d -> d.multiply(new BigDecimal("100")).intValue())
            .toArray();
        
        // 动态规划计算
        int[] dp = new int[targetCents + 1];
        int[] choice = new int[targetCents + 1];
        Arrays.fill(dp, Integer.MAX_VALUE);
        Arrays.fill(choice, -1);
        dp[0] = 0;
        
        // DP填表
        for (int i = 1; i <= targetCents; i++) {
            for (int j = 0; j < coinsCents.length; j++) {
                if (coinsCents[j] <= i && dp[i - coinsCents[j]] != Integer.MAX_VALUE) {
                    if (dp[i - coinsCents[j]] + 1 < dp[i]) {
                        dp[i] = dp[i - coinsCents[j]] + 1;
                        choice[i] = j;
                    }
                }
            }
        }
        
        // 无解
        if (dp[targetCents] == Integer.MAX_VALUE) {
            return new CoinResponse(totalAmount, new HashMap<>(), 0);
        }
        
        // 构建解决方案
        Map<String, Integer> solution = new HashMap<>();
        int current = targetCents;
        while (current > 0) {
            int coinIndex = choice[current];
            String denomination = denominations.get(coinIndex).toString();
            solution.put(denomination, solution.getOrDefault(denomination, 0) + 1);
            current -= coinsCents[coinIndex];
        }
        
        return new CoinResponse(totalAmount, solution, dp[targetCents]);
    }
}
