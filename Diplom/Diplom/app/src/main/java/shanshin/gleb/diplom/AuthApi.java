package shanshin.gleb.diplom;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {

    @POST("/api/auth/signin")
    Call<AuthSuccessResponse> loginUser(@Body LoginAndPassword loginAndPassword);

    @POST("/api/auth/signup")
    Call<AuthSuccessResponse> registerUser(@Body LoginAndPassword loginAndPassword);

}
