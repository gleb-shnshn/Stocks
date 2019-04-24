package shanshin.gleb.diplom.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import shanshin.gleb.diplom.model.RefreshToken;
import shanshin.gleb.diplom.model.LoginAndPassword;
import shanshin.gleb.diplom.model.RegistrationData;
import shanshin.gleb.diplom.responses.AuthSuccessResponse;
import shanshin.gleb.diplom.responses.IconResponse;

public interface AuthApi {

    @POST("/api/auth/signin")
    Call<AuthSuccessResponse> loginUser(@Body LoginAndPassword loginAndPassword);

    @POST("/api/auth/signup/full")
    Call<AuthSuccessResponse> registerUser(@Body RegistrationData loginAndPassword);

    @POST("/api/auth/refresh")
    Call<AuthSuccessResponse> refreshToken(@Header("Authorization") String accessToken, @Body RefreshToken refreshToken);

    @GET("/public/icons/all")
    Call<IconResponse> getListOfIcons();
}
