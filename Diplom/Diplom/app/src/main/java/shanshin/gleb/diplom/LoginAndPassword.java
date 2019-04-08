package shanshin.gleb.diplom;

import com.google.gson.annotations.SerializedName;

public class LoginAndPassword {
    @SerializedName("login")
    String login;
    @SerializedName("password")
    String password;

    public LoginAndPassword(String login, String password) {
        this.login = login;
        this.password = password;
    }
}
