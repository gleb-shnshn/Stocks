package shanshin.gleb.diplom.model;

import com.google.gson.annotations.SerializedName;

public class RegistrationData {
    @SerializedName("login")
    String login;

    @SerializedName("password")
    String password;

    @SerializedName("icon")
    String icon;

    @SerializedName("name")
    String name;


    public RegistrationData(String login, String password, String icon, String name) {
        this.login = login;
        this.password = password;
        this.icon = icon;
        this.name = name;
    }
}
