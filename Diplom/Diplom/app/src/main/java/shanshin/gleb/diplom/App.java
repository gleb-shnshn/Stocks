package shanshin.gleb.diplom;

import android.app.Application;
import android.content.Context;

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

    public static App getInstance() {
        return instance;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public static void showError(Context context, String errorMessage) {
        new StyleableToast
                .Builder(context)
                .text(errorMessage)
                .cornerRadius(5)
                .textSize(13)
                .textColor(context.getResources().getColor(R.color.white))
                .backgroundColor(context.getResources().getColor(R.color.errorColor))
                .show();
    }
}
