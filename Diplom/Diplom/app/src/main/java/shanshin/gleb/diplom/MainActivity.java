package shanshin.gleb.diplom;

import android.content.Intent;

import shanshin.gleb.diplom.api.AuthApi;
import shanshin.gleb.diplom.model.LoginAndPassword;
import shanshin.gleb.diplom.responses.AuthErrorResponse;
import shanshin.gleb.diplom.responses.AuthErrorResponse.InvalidField;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import shanshin.gleb.diplom.responses.AuthSuccessResponse;

public class MainActivity extends AppCompatActivity {
    EditText loginField, passwordField;

    public void updateContentViewOnUiThread(final int layout) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(layout);
                initializeViews();
            }
        });
    }

    public void initializeViews() {
        loginField = findViewById(R.id.loginInput);
        passwordField = findViewById(R.id.passInput);
    }

    public void switchToRegistration(View view) {
        setContentView(R.layout.activity_registration);
        initializeViews();
    }

    public void switchToLogin(View view) {
        setContentView(R.layout.activity_login);
        initializeViews();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateContentViewOnUiThread(R.layout.activity_loading);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (App.getInstance().getAccessToken().equals("")) {
                    updateContentViewOnUiThread(R.layout.activity_login);
                }else {
                    switchToStockCase();
                }
            }

        }).start();
    }

    public void performRequest(String request) {
        getRequestCallByStringName(request).enqueue(new Callback<AuthSuccessResponse>() {
            @Override
            public void onResponse(Call<AuthSuccessResponse> call, Response<AuthSuccessResponse> response) {
                try {
                    if (response != null && !response.isSuccessful() && response.errorBody() != null) {
                        Converter<ResponseBody, AuthErrorResponse> errorConverter =
                                App.getInstance().getRetrofit().responseBodyConverter(AuthErrorResponse.class, new Annotation[0]);
                        AuthErrorResponse errorResponse = errorConverter.convert(response.errorBody());
                        for (InvalidField invalidField : errorResponse.invalidFields) {
                            App.getInstance().showError(invalidField.message);
                        }
                    } else {
                        AuthSuccessResponse successResponse = response.body();
                        log(successResponse.accessToken);
                        App.getInstance().saveTokens(successResponse.accessToken, successResponse.refreshToken);
                        switchToStockCase();
                    }

                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(Call<AuthSuccessResponse> call, Throwable t) {

            }
        });

    }

    private void switchToStockCase() {
        Intent intent = new Intent(MainActivity.this, StockCaseActivity.class);
        startActivity(intent);
        finish();
    }

    private Call<AuthSuccessResponse> getRequestCallByStringName(String request) {
        AuthApi authApi = App.getInstance().getRetrofit().create(AuthApi.class);
        LoginAndPassword data = getDataFromFields();
        if (request.equals("login")) {
            return authApi.loginUser(data);
        } else if (request.equals("register")) {
            return authApi.registerUser(data);
        }
        return null;
    }


    public void clickToLogin(View view) {
        performRequest("login");
    }

    public void clickToRegister(View view) {
        performRequest("register");
    }

    public LoginAndPassword getDataFromFields() {
        String login = loginField.getText().toString();
        String password = passwordField.getText().toString();
        return new LoginAndPassword(login, password);
    }


    private void log(String msg) {
        Log.d("tokens", msg);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


}
