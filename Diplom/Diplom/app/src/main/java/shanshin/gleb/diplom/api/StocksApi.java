package shanshin.gleb.diplom.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
import shanshin.gleb.diplom.responses.StocksResponse;

public interface StocksApi {

    @GET("api/stocks")
    Call<StocksResponse> getStocks(@Header("Authorization") String accessToken, @Query("search") String search, @Query("count") int count, @Query("itemId") int itemId);

}
