package shanshin.gleb.diplom;

import android.app.Application;
import android.content.Intent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import shanshin.gleb.diplom.api.AuthApi;
import shanshin.gleb.diplom.handlers.BottomDialogHandler;
import shanshin.gleb.diplom.handlers.ErrorHandler;
import shanshin.gleb.diplom.handlers.GeneralUtils;
import shanshin.gleb.diplom.handlers.MapStockUtils;
import shanshin.gleb.diplom.handlers.DataHandler;
import shanshin.gleb.diplom.model.RefreshToken;
import shanshin.gleb.diplom.responses.AuthSuccessResponse;

public class App extends Application {
    private static App instance;
    private Retrofit retrofit;
    private DataHandler sharedPrefsHandler;
    private GeneralUtils utils;
    private MapStockUtils mapUtils;
    private ErrorHandler errorHandler;
    private AppCompatActivity currentActivity;

    public BottomDialogHandler getDialogHandler() {
        return dialogHandler;
    }

    private BottomDialogHandler dialogHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        String server = getResources().getString(R.string.server_url);
        Gson gson = new GsonBuilder().create();
        sharedPrefsHandler = new DataHandler();
        utils = new GeneralUtils();
        dialogHandler = new BottomDialogHandler();
        mapUtils = new MapStockUtils();
        errorHandler = new ErrorHandler();
        File httpCacheDirectory = new File(getCacheDir(), "responses");
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new ExpiredTokenInterceptor())
                .cache(new Cache(httpCacheDirectory, 10 * 1024 * 1024))
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(server)
                .client(okHttpClient)
                .build();

    }

    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    public MapStockUtils getMapUtils() {
        return mapUtils;
    }

    public GeneralUtils getUtils() {
        return utils;
    }

    public static App getInstance() {
        return instance;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public void refreshToken() {
        AuthApi authApi = getInstance().getRetrofit().create(AuthApi.class);
        Call<AuthSuccessResponse> call = authApi.refreshToken(App.getInstance().getDataHandler().getAccessToken(), new RefreshToken(App.getInstance().getDataHandler().getRefreshToken()));
        try {
            App.getInstance().getDataHandler().saveTokens("", "");
            Response<AuthSuccessResponse> response = call.execute();
            if (response.code() != 401) {
                AuthSuccessResponse successResponse = response.body();
                App.getInstance().getDataHandler().saveTokens(successResponse.accessToken, successResponse.refreshToken);
            } else {
                App.getInstance().getUtils().showError(getString(R.string.sign_in_again));
                startActivity(new Intent(currentActivity, AuthActivity.class));
                currentActivity.finish();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DataHandler getDataHandler() {
        return sharedPrefsHandler;
    }

    public AppCompatActivity getCurrentActivity() {
        return currentActivity;
    }

    public void setCurrentActivity(AppCompatActivity currentActivity) {
        this.currentActivity = currentActivity;
    }
}
