package shanshin.gleb.diplom;

import android.content.Intent;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import shanshin.gleb.diplom.api.AuthApi;
import shanshin.gleb.diplom.model.LoginAndPassword;
import shanshin.gleb.diplom.responses.AuthErrorResponse;
import shanshin.gleb.diplom.responses.AuthErrorResponse.InvalidField;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import shanshin.gleb.diplom.responses.AuthSuccessResponse;

public class MainActivity extends AppCompatActivity {
    CircularProgressButton circularProgressButton;
    EditText loginField, passwordField;
    TextView switchButton;
    boolean isLoginOrRegistration = true;

    public void updateContentViewOnUiThread(final int layout, final boolean needInit) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(layout);
                if (needInit)
                    initializeViews();
            }
        });
    }

    public void initializeViews() {
        loginField = findViewById(R.id.loginInput);
        passwordField = findViewById(R.id.passInput);
        circularProgressButton = findViewById(R.id.progressButton);
        switchButton = findViewById(R.id.switchButton);
        updateTextOnButtons();
    }

    public void switchView(View view) {
        isLoginOrRegistration = !isLoginOrRegistration;
        updateTextOnButtons();
    }

    private void updateTextOnButtons() {
        switchButton.setText(isLoginOrRegistration ? "Создать аккаунт" : "Уже зарегистрированы?");
        circularProgressButton.setText(isLoginOrRegistration ? "Войти" : "Создать аккаунт");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateContentViewOnUiThread(R.layout.activity_loading, false);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (App.getInstance().getAccessToken().equals("")) {
                    updateContentViewOnUiThread(R.layout.activity_auth, true);
                } else {
                    switchToStockCase();
                }
            }

        }).start();
    }

    public void performRequest() {
        showProgress(true);
        getRequestCall().enqueue(new Callback<AuthSuccessResponse>() {
            @Override
            public void onResponse(Call<AuthSuccessResponse> call, Response<AuthSuccessResponse> response) {
                try {
                    showProgress(false);
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

    private void showProgress(boolean visible) {
        if (visible) {
            circularProgressButton.startMorphAnimation(); //превращение в loader
        } else {
            circularProgressButton.startMorphRevertAnimation(); //превращение в кнопку
        }

    }

    private void switchToStockCase() {
        Intent intent = new Intent(MainActivity.this, StockCaseActivity.class);
        startActivity(intent);
        finish();
    }

    private Call<AuthSuccessResponse> getRequestCall() {
        AuthApi authApi = App.getInstance().getRetrofit().create(AuthApi.class);
        LoginAndPassword data = getDataFromFields();
        if (isLoginOrRegistration) {
            return authApi.loginUser(data);
        } else {
            return authApi.registerUser(data);
        }
    }


    public void clickToPerform(View view) {
        performRequest();
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
