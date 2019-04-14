package shanshin.gleb.diplom;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class App extends Application {
    public static App instance;
    String server;
    Gson gson;
    Retrofit retrofit;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        server = getResources().getString(R.string.server_url);
        gson = new GsonBuilder().create();
        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(server)
                .build();

    }
    public String getAccessToken(){
        SharedPreferences sPref = getSharedPreferences("tokens", MODE_PRIVATE);
        return sPref.getString("accessToken", "");
    }

    public static App getInstance() {
        return instance;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public void showError(String errorMessage) {
        new StyleableToast
                .Builder(this)
                .text(errorMessage)
                .cornerRadius(5)
                .textSize(13)
                .textColor(getResources().getColor(R.color.white))
                .backgroundColor(getResources().getColor(R.color.errorColor))
                .show();
    }
}
