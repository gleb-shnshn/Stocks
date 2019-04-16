package shanshin.gleb.diplom;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.muddzdev.styleabletoast.StyleableToast;

import java.io.IOException;

import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import shanshin.gleb.diplom.api.AuthApi;
import shanshin.gleb.diplom.model.RefreshToken;
import shanshin.gleb.diplom.responses.AuthSuccessResponse;

public class App extends Application {
    private static App instance;
    String server;
    Gson gson;
    Retrofit retrofit;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        server = getResources().getString(R.string.server_url);
        gson = new GsonBuilder().create();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new ExpiredTokenInterceptor())
                .build();

        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(server)
                .client(okHttpClient)
                .build();

    }

    public String getAccessToken() {
        SharedPreferences sPref = getSharedPreferences("tokens", MODE_PRIVATE);
        return sPref.getString("accessToken", "");
    }

    public static App getInstance() {
        return instance;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public void showError(final String errorMessage) {
        new StyleableToast
                .Builder(instance)
                .text(errorMessage)
                .cornerRadius(5)
                .textSize(13)
                .textColor(getResources().getColor(R.color.white))
                .backgroundColor(getResources().getColor(R.color.errorColor))
                .show();
    }

    public void initializeDialog(BottomSheetDialog bottomSheetDialog, String name, String buttonText) {
        TextView stockName = bottomSheetDialog.findViewById(R.id.stock_name);
        Button button = bottomSheetDialog.findViewById(R.id.button);
        stockName.setText(name);
        button.setText(buttonText);
    }

    public void saveTokens(String accessToken, String refreshToken) {
        SharedPreferences sPref = getSharedPreferences("tokens", MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("accessToken", accessToken);
        ed.putString("refreshToken", refreshToken);
        ed.apply();
    }

    public void updateTokens() {
        AuthApi authApi = getInstance().getRetrofit().create(AuthApi.class);
        try {
            Response<AuthSuccessResponse> response = authApi.refreshToken(App.getInstance().getAccessToken(), new RefreshToken(App.getInstance().getRefreshToken())).execute();
            if (response.code() != 401) {
                AuthSuccessResponse successResponse = response.body();
                saveTokens(successResponse.accessToken, successResponse.refreshToken);
            }
            else {
                startActivity(new Intent(this, MainActivity.class));
                saveTokens("","");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getRefreshToken() {
        SharedPreferences sPref = getSharedPreferences("tokens", MODE_PRIVATE);
        return sPref.getString("refreshToken", "");
    }

}
