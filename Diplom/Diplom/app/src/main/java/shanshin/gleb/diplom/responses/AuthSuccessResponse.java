package shanshin.gleb.diplom.responses;

import com.google.gson.annotations.SerializedName;

public class AuthSuccessResponse {

    @SerializedName("accessToken")
    public String accessToken;

    @SerializedName("refreshToken")
    public String refreshToken;
}
