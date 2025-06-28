package com.oracle.api;

import com.oracle.dto.request.CoinRequest;
import com.oracle.dto.response.CoinResponse;
import com.oracle.service.CoinService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/coins")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CoinResource{
    private final CoinService coinService;

    public CoinResource(CoinService coinService){
        this.coinService = coinService;
    }

    @POST
    public CoinResponse calculateCoinChange(CoinRequest request){
        return coinService.calculateCoinChange(request);
    }
}