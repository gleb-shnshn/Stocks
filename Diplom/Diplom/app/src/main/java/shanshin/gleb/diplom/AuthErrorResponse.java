package shanshin.gleb.diplom;

import com.google.gson.annotations.SerializedName;

public class AuthErrorResponse {
    @SerializedName("code")
    String code;

    @SerializedName("invalidFields")
    InvalidField[] invalidFields;

    public class InvalidField {
        @SerializedName("field")
        String field;
        @SerializedName("message")
        String message;
    }
}
