package shanshin.gleb.diplom;

import com.google.gson.annotations.SerializedName;

public class DefaultErrorResponse {

    @SerializedName("code")
    String code;

    @SerializedName("message")
    String message;

}
