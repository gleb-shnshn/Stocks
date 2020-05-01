package shanshin.gleb.diplom.handlers;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.view.View;

import com.github.twocoffeesoneteam.glidetovectoryou.GlideApp;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;

import java.io.IOException;
import java.lang.annotation.Annotation;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.ResponseBody;
import retrofit2.Callback;
import retrofit2.Converter;
import shanshin.gleb.diplom.App;
import shanshin.gleb.diplom.LoadingButton;
import shanshin.gleb.diplom.R;
import shanshin.gleb.diplom.StockCaseActivity;
import shanshin.gleb.diplom.responses.DefaultErrorResponse;
import shanshin.gleb.diplom.responses.FieldErrorResponse;
import shanshin.gleb.diplom.responses.InfoResponse;

public class ErrorHandler {
    public void handleFieldError(ResponseBody errorBody) {
        Converter<ResponseBody, FieldErrorResponse> errorConverter =
                App.getInstance().getRetrofit().responseBodyConverter(FieldErrorResponse.class, new Annotation[0]);

        FieldErrorResponse errorResponse;
        try {
            errorResponse = errorConverter.convert(errorBody);
        } catch (IOException ignored) {
            return;
        }

        for (FieldErrorResponse.InvalidField invalidField : errorResponse.invalidFields) {
            App.getInstance().getUtils().showError(invalidField.message);
        }
    }

    public void handleDefaultError(ResponseBody errorBody) throws IOException {
        Converter<ResponseBody, DefaultErrorResponse> errorConverter =
                App.getInstance().getRetrofit().responseBodyConverter(DefaultErrorResponse.class, new Annotation[0]);
        DefaultErrorResponse errorResponse = errorConverter.convert(errorBody);
        App.getInstance().getUtils().showError(errorResponse.message);
    }

    public void handleNoConnection() {
        final AppCompatActivity activity = App.getInstance().getCurrentActivity();
        final ConnectivityManager connectivityManager = (ConnectivityManager)
                App.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.setContentView(R.layout.activity_no_internet);
                final LoadingButton lb = activity.findViewById(R.id.button);
                lb.setText(activity.getString(R.string.try_again));
                lb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        lb.startLoading();
                        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                        if (networkInfo != null && networkInfo.isConnected()){
                            activity.startActivity(activity.getIntent());
                            activity.finish();
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                lb.stopLoading();
                            }
                        },500);

                    }
                });
            }
        });

    }
}
