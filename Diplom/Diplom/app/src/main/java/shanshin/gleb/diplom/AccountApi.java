package shanshin.gleb.diplom;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface AccountApi {

    @GET("api/account/info")
    Call<InfoResponse> getAccountInfo(@Header("Authorization") String accessToken);
}
