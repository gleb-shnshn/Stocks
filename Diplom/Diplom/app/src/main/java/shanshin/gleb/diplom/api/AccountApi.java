package shanshin.gleb.diplom.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import shanshin.gleb.diplom.responses.InfoResponse;

public interface AccountApi {

    @GET("api/account/info/full")
    Call<InfoResponse> getAccountInfo(@Header("Authorization") String accessToken, @Header("Cache-Control") String cacheControl);

}
