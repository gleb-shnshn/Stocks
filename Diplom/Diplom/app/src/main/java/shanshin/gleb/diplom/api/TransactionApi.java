package shanshin.gleb.diplom.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import shanshin.gleb.diplom.model.StockAmountAndId;
import shanshin.gleb.diplom.responses.BuyAndSellResponse;
import shanshin.gleb.diplom.responses.TransactionHistoryResponse;

public interface TransactionApi {

    @POST("/api/transaction/buy")
    Call<BuyAndSellResponse> buyStocks(@Header("Authorization") String accessToken, @Body StockAmountAndId stockAmountAndId);

    @POST("/api/transaction/sell")
    Call<BuyAndSellResponse> sellStocks(@Header("Authorization") String accessToken, @Body StockAmountAndId stockAmountAndId);

    @GET("/api/transaction/history")
    Call<TransactionHistoryResponse> getTransactionHistory(@Header("Authorization") String accessToken, @Query("search") String search, @Query("count") int count, @Query("itemId") int itemId);

}
