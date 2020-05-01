package shanshin.gleb.diplom.model;

import com.google.gson.annotations.SerializedName;

public class RefreshToken {
    @SerializedName("refreshToken")
    String refreshToken;

    public RefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
