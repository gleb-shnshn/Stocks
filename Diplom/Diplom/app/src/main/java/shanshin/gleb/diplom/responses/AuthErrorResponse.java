package shanshin.gleb.diplom.responses;

import com.google.gson.annotations.SerializedName;

public class AuthErrorResponse {
    @SerializedName("code")
    public String code;

    @SerializedName("invalidFields")
    public InvalidField[] invalidFields;

    public class InvalidField {
        @SerializedName("field")
        public String field;
        @SerializedName("message")
        public String message;
    }
}
