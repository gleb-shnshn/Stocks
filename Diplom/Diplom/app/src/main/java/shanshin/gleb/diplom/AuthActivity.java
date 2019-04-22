package shanshin.gleb.diplom;

import android.content.Intent;

import shanshin.gleb.diplom.api.AuthApi;
import shanshin.gleb.diplom.model.LoginAndPassword;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import shanshin.gleb.diplom.responses.AuthSuccessResponse;

public class AuthActivity extends AppCompatActivity implements View.OnClickListener {
    private LoadingButton loadingButton;
    private EditText loginField, passwordField;
    private TextView switchButton;
    private boolean isLoginOrRegistration = true;

    private void updateContentViewOnUiThread(final int layout, final boolean needInit) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(layout);
                if (needInit)
                    initializeViews();
            }
        });
    }

    private void initializeViews() {
        loginField = findViewById(R.id.loginInput);
        passwordField = findViewById(R.id.passInput);
        loadingButton = findViewById(R.id.progressButton);
        switchButton = findViewById(R.id.switchButton);
        loadingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performRequest();
            }
        });
        updateTextOnButtons();
    }

    public void onClick(View view) {
        isLoginOrRegistration = !isLoginOrRegistration;
        updateTextOnButtons();
    }

    private void updateTextOnButtons() {
        switchButton.setText(isLoginOrRegistration ? "Создать аккаунт" : "Уже зарегистрированы?");
        loadingButton.setText(isLoginOrRegistration ? "Войти" : "Создать аккаунт");
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
                if (App.getInstance().getDataHandler().getAccessToken().equals("")) {
                    updateContentViewOnUiThread(R.layout.activity_auth, true);
                } else {
                    switchToStockCase();
                }
            }

        }).start();
    }

    private void performRequest() {
        switchButton.setOnClickListener(null);
        showProgress(true);
        getRequestCall().enqueue(new Callback<AuthSuccessResponse>() {
            @Override
            public void onResponse(Call<AuthSuccessResponse> call, Response<AuthSuccessResponse> response) {
                try {
                    showProgress(false);
                    if (response.code() == 401) {
                        App.getInstance().getUtils().showError("Неверный логин или пароль");
                    } else if (!response.isSuccessful() && response.errorBody() != null) {
                        App.getInstance().getErrorHandler().handleFieldError(response.errorBody());
                    } else {
                        AuthSuccessResponse successResponse = response.body();
                        App.getInstance().getDataHandler().saveTokens(successResponse.accessToken, successResponse.refreshToken);
                        switchToStockCase();
                    }

                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(Call<AuthSuccessResponse> call, Throwable t) {
                showProgress(false);
                App.getInstance().getUtils().showError(getString(R.string.no_connection));
            }
        });

    }

    private void showProgress(boolean visible) {
        if (visible) {
            loadingButton.startLoading(); //превращение в loader
            switchButton.setOnClickListener(null);
        } else {
            loadingButton.stopLoading(); //превращение в кнопку
            switchButton.setOnClickListener(this);
        }

    }

    private void switchToStockCase() {
        Intent intent = new Intent(AuthActivity.this, StockCaseActivity.class);
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

    private LoginAndPassword getDataFromFields() {
        String login = loginField.getText().toString();
        String password = passwordField.getText().toString();
        return new LoginAndPassword(login, password);
    }

    @Override
    public void onBackPressed() {
    }
}
