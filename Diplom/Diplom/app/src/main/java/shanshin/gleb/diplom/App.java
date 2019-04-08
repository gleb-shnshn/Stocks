package shanshin.gleb.diplom;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
}
