package shanshin.gleb.diplom.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import shanshin.gleb.diplom.model.LoginAndPassword;
import shanshin.gleb.diplom.responses.AuthSuccessResponse;

public interface AuthApi {

    @POST("/api/auth/signin")
    Call<AuthSuccessResponse> loginUser(@Body LoginAndPassword loginAndPassword);

    @POST("/api/auth/signup")
    Call<AuthSuccessResponse> registerUser(@Body LoginAndPassword loginAndPassword);

}
