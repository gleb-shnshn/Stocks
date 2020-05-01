package shanshin.gleb.diplom.responses;

import com.google.gson.annotations.SerializedName;

public class DefaultErrorResponse {

    @SerializedName("code")
    public String code;

    @SerializedName("message")
    public String message;

}
