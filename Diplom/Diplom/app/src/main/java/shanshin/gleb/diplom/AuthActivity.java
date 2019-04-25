package shanshin.gleb.diplom;

import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import shanshin.gleb.diplom.api.AuthApi;
import shanshin.gleb.diplom.model.LoginAndPassword;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import shanshin.gleb.diplom.model.RegistrationData;
import shanshin.gleb.diplom.responses.AuthSuccessResponse;
import shanshin.gleb.diplom.responses.IconResponse;

public class AuthActivity extends AppCompatActivity implements View.OnClickListener {
    private LoadingButton loadingButton;
    private EditText loginField, passwordField, nameField;
    private TextView iconLabel, nameLabel;
    private RecyclerView iconList;
    private TextView switchButton;
    private boolean isLoginOrRegistration = true;
    private ImageView mainIcon, appLabel;
    private String iconUrl;

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

    private void initializeRecycler(Response<IconResponse> response) {
        IconAdapter iconAdapter = new IconAdapter(response.body().iconUrls, this);
        iconList.setAdapter(iconAdapter);
        iconList.setLayoutManager(new LinearLayoutManager(App.getInstance(), RecyclerView.HORIZONTAL, false));
    }

    public void setMainIcon(Drawable drawable, String iconUrl) {
        this.iconUrl = iconUrl;
        mainIcon.setImageDrawable(drawable);
    }

    private void initializeViews() {
        loginField = findViewById(R.id.loginInput);
        passwordField = findViewById(R.id.passInput);
        loadingButton = findViewById(R.id.progressButton);
        switchButton = findViewById(R.id.switchButton);
        iconList = findViewById(R.id.iconView);
        App.getInstance().getRetrofit().create(AuthApi.class).getListOfIcons().enqueue(new Callback<IconResponse>() {
            @Override
            public void onResponse(Call<IconResponse> call, Response<IconResponse> response) {
                initializeRecycler(response);
            }

            @Override
            public void onFailure(Call<IconResponse> call, Throwable t) {
            }
        });
        nameLabel = findViewById(R.id.nameLabel);
        iconLabel = findViewById(R.id.iconLabel);
        nameField = findViewById(R.id.nameInput);
        mainIcon = findViewById(R.id.mainIcon);
        appLabel = findViewById(R.id.appLabel);
        loadingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performRequest();
            }
        });
        switchButton.setOnClickListener(this);
        updateTextOnButtons();
    }

    public void onClick(View view) {
        isLoginOrRegistration = !isLoginOrRegistration;
        updateTextOnButtons();
    }

    private void updateTextOnButtons() {
        switchButton.setText(isLoginOrRegistration ? getString(R.string.create_an_account) : getString(R.string.already_registered));
        loadingButton.setText(isLoginOrRegistration ? getString(R.string.sign_in) : getString(R.string.create_an_account));
        iconList.setVisibility(isLoginOrRegistration ? View.GONE : View.VISIBLE);
        nameLabel.setVisibility(isLoginOrRegistration ? View.GONE : View.VISIBLE);
        iconLabel.setVisibility(isLoginOrRegistration ? View.GONE : View.VISIBLE);
        nameField.setVisibility(isLoginOrRegistration ? View.GONE : View.VISIBLE);
        mainIcon.setVisibility(isLoginOrRegistration ? View.GONE : View.VISIBLE);
        appLabel.setVisibility(isLoginOrRegistration ? View.VISIBLE : View.GONE);
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
        showProgress(true);
        getRequestCall().enqueue(new Callback<AuthSuccessResponse>() {
            @Override
            public void onResponse(Call<AuthSuccessResponse> call, Response<AuthSuccessResponse> response) {
                try {
                    showProgress(false);
                    if (response.code() == 401) {
                        App.getInstance().getUtils().showError(getString(R.string.wrong_auth_data));
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
            loadingButton.startLoading();
            switchButton.setOnClickListener(null);
        } else {
            loadingButton.stopLoading();
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

        if (isLoginOrRegistration) {
            LoginAndPassword data = getLoginDataFromFields();
            return authApi.loginUser(data);
        } else {
            RegistrationData data = getRegDataFromFields();
            return authApi.registerUser(data);
        }
    }

    private LoginAndPassword getLoginDataFromFields() {
        String login = loginField.getText().toString();
        String password = passwordField.getText().toString();
        return new LoginAndPassword(login, password);
    }

    private RegistrationData getRegDataFromFields() {
        String login = loginField.getText().toString();
        String password = passwordField.getText().toString();
        String icon = getString(R.string.icon_url) + iconUrl;
        String name = nameField.getText().toString();
        return new RegistrationData(login, password, icon, name);
    }

    @Override
    public void onBackPressed() {
    }
}
