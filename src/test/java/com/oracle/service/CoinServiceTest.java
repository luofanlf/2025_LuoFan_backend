package com.oracle.service;

import com.oracle.dto.request.CoinRequest;
import com.oracle.dto.response.CoinResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * CoinService unitTest
 */
@DisplayName("CoinService unitTest")
class CoinServiceTest {

    private CoinService coinService;

    @BeforeEach
    void setUp() {
        coinService = new CoinService();
    }

    @Nested
    @DisplayName("basic calculation")
    class NormalChangeCalculationTests {

        @Test
        @DisplayName("basic calculation1 - 4.67")
        void shouldCalculateChangeCorrectly() {
            // Given
            BigDecimal totalAmount = new BigDecimal("4.67");
            List<BigDecimal> denominations = Arrays.asList(
                new BigDecimal("2.00"), new BigDecimal("1.00"), 
                new BigDecimal("0.50"), new BigDecimal("0.20"), 
                new BigDecimal("0.10"), new BigDecimal("0.05"), 
                new BigDecimal("0.02"), new BigDecimal("0.01")
            );
            CoinRequest request = new CoinRequest(totalAmount, denominations);

            // When
            CoinResponse response = coinService.calculateCoinChange(request);

            // Then
            assertThat(response.getTotalAmount()).isEqualTo(totalAmount);
            assertThat(response.getTotalCoins()).isEqualTo(6);
            
            Map<String, Integer> solution = response.getSolution();
            assertThat(solution.get("2.00")).isEqualTo(2);
            assertThat(solution.get("0.50")).isEqualTo(1);
            assertThat(solution.get("0.10")).isEqualTo(1);
            assertThat(solution.get("0.05")).isEqualTo(1);
            assertThat(solution.get("0.02")).isEqualTo(1);
            
            // verify the total amount
            BigDecimal calculatedTotal = solution.entrySet().stream()
                .map(entry -> new BigDecimal(entry.getKey()).multiply(new BigDecimal(entry.getValue())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            assertThat(calculatedTotal).isEqualTo(totalAmount);
        }

        @Test
        @DisplayName("basic calculation2 - 11")
        void shouldCalculateSimpleChange() {
            // Given
            BigDecimal totalAmount = new BigDecimal("11.00");
            List<BigDecimal> denominations = Arrays.asList(
                new BigDecimal("5.00"), new BigDecimal("2.00"), new BigDecimal("1.00")
            );
            CoinRequest request = new CoinRequest(totalAmount, denominations);

            // When
            CoinResponse response = coinService.calculateCoinChange(request);

            // Then
            assertThat(response.getTotalAmount()).isEqualTo(totalAmount);
            assertThat(response.getTotalCoins()).isEqualTo(3);
            
            Map<String, Integer> solution = response.getSolution();
            assertThat(solution.get("5.00")).isEqualTo(2);
            assertThat(solution.get("1.00")).isEqualTo(1);
        }


    }

    @Nested
    @DisplayName("best algorithm confirmation")
    class OptimalSolutionTests {

        @Test
        @DisplayName("dp vs greedy")
        void shouldUseOptimalOverGreedyAlgorithm() {
            // Given - 这是一个经典的动态规划优于贪心算法的例子
            BigDecimal totalAmount = new BigDecimal("12.00");
            List<BigDecimal> denominations = Arrays.asList(
                new BigDecimal("10.00"), new BigDecimal("6.00"), new BigDecimal("1.00")
            );
            CoinRequest request = new CoinRequest(totalAmount, denominations);

            // When
            CoinResponse response = coinService.calculateCoinChange(request);

            // Then - 最优解是2个6元（2个硬币），贪心算法会选择1个10元+2个1元（3个硬币）
            assertThat(response.getTotalCoins()).isEqualTo(2);
            Map<String, Integer> solution = response.getSolution();
            assertThat(solution.get("6.00")).isEqualTo(2);   // 2个6元
        }
    }


    @Nested
    @DisplayName("boundary situation test")
    class BoundaryTests {

        @Test
        @DisplayName("minimum amount - 0.01")
        void shouldHandleMinimumAmount() {
            // Given
            BigDecimal totalAmount = new BigDecimal("0.01");
            List<BigDecimal> denominations = Arrays.asList(
                new BigDecimal("0.01"), new BigDecimal("0.05"), new BigDecimal("0.10")
            );
            CoinRequest request = new CoinRequest(totalAmount, denominations);

            // When
            CoinResponse response = coinService.calculateCoinChange(request);

            // Then
            assertThat(response.getTotalCoins()).isEqualTo(1);
            assertThat(response.getSolution().get("0.01")).isEqualTo(1);
        }

        @Test
        @DisplayName("single denomination")
        void shouldHandleSingleDenomination() {
            // Given
            BigDecimal totalAmount = new BigDecimal("5.00");
            List<BigDecimal> denominations = Collections.singletonList(new BigDecimal("1.00"));
            CoinRequest request = new CoinRequest(totalAmount, denominations);

            // When
            CoinResponse response = coinService.calculateCoinChange(request);

            // Then
            assertThat(response.getTotalCoins()).isEqualTo(5);
            assertThat(response.getSolution().get("1.00")).isEqualTo(5);
        }

        @Test
        @DisplayName("amount equal to max denomination")
        void shouldHandleExactLargestDenomination() {
            // Given
            BigDecimal totalAmount = new BigDecimal("10.00");
            List<BigDecimal> denominations = Arrays.asList(
                new BigDecimal("10.00"), new BigDecimal("5.00"), new BigDecimal("1.00")
            );
            CoinRequest request = new CoinRequest(totalAmount, denominations);

            // When
            CoinResponse response = coinService.calculateCoinChange(request);

            // Then
            assertThat(response.getTotalCoins()).isEqualTo(1);
            assertThat(response.getSolution().get("10.00")).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("performance test")
    class PerformanceTests {

        @Test
        @DisplayName("big amount performance test")
        void shouldHandleLargeAmountEfficiently() {
            // Given
            BigDecimal totalAmount = new BigDecimal("999.99");
            List<BigDecimal> denominations = Arrays.asList(
                new BigDecimal("50.00"), new BigDecimal("20.00"), 
                new BigDecimal("10.00"), new BigDecimal("5.00"), 
                new BigDecimal("2.00"), new BigDecimal("1.00"), 
                new BigDecimal("0.50"), new BigDecimal("0.20"), 
                new BigDecimal("0.10"), new BigDecimal("0.05"), 
                new BigDecimal("0.02"), new BigDecimal("0.01")
            );
            CoinRequest request = new CoinRequest(totalAmount, denominations);

            // When & Then - 应该在合理时间内完成
            long startTime = System.currentTimeMillis();
            CoinResponse response = coinService.calculateCoinChange(request);
            long endTime = System.currentTimeMillis();

            assertThat(endTime - startTime).isLessThan(1000); // 应该在1秒内完成
            assertThat(response.getTotalAmount()).isEqualTo(totalAmount);
            assertThat(response.getSolution()).isNotEmpty();
        }
    }
} 