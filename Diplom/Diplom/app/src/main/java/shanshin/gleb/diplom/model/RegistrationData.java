package shanshin.gleb.diplom.model;

import com.google.gson.annotations.SerializedName;

public class RegistrationData {
    @SerializedName("login")
    String login;

    @SerializedName("password")
    String password;

    @SerializedName("icon")
    String icon;

    @SerializedName("email")
    String email;

    @SerializedName("name")
    String name;

    @SerializedName("surname")
    String surname;

    public RegistrationData(String login, String password, String icon, String email, String name, String surname) {
        this.login = login;
        this.password = password;
        this.icon = icon;
        this.email = email;
        this.name = name;
        this.surname = surname;
    }
}
