package shanshin.gleb.diplom.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import shanshin.gleb.diplom.model.StockAmountAndId;
import shanshin.gleb.diplom.responses.BuyAndSellResponse;

public interface TransactionApi {

    @POST("/api/transaction/buy")
    Call<BuyAndSellResponse> buyStocks(@Header("Authorization") String accessToken, @Body StockAmountAndId stockAmountAndId);

    @POST("/api/transaction/sell")
    Call<BuyAndSellResponse> sellStocks(@Header("Authorization") String accessToken, @Body StockAmountAndId stockAmountAndId);

}
