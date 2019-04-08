package shanshin.gleb.diplom;

import com.google.gson.annotations.SerializedName;

public class AuthSuccessResponse {

    @SerializedName("accessToken")
    String accessToken;

    @SerializedName("refreshToken")
    String refreshToken;
}
