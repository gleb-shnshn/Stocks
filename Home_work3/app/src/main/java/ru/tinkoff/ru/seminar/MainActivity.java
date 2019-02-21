package ru.tinkoff.ru.seminar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ru.tinkoff.ru.seminar.model.Weather;

@SuppressWarnings("unused")
public class MainActivity extends AppCompatActivity {

    private Spinner spinner;
    private Button performBtn;
    private ProgressBar progressBar;
    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        spinner = findViewById(R.id.spinner);
        performBtn = findViewById(R.id.performBtn);
        progressBar = findViewById(R.id.progressBar);
        resultTextView = findViewById(R.id.resultTextView);
        performBtn.setOnClickListener(v -> performRequest(spinner.getSelectedItem().toString()));
    }

    private void setEnablePerformButton(boolean enable) {
        performBtn.setEnabled(enable);
    }

    @SuppressLint("DefaultLocale")
    private void printResult(@NonNull Weather weather, @NonNull List<Weather> forecast) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(
                String.format(
                        "CurrentWeather\nDesc: %s \nTimeUnix: %d\nTemp: %.1f\nSpeed wind: %.1f",
                        weather.description,
                        weather.time,
                        weather.temp,
                        weather.speedWind
                )
        );

        if (!forecast.isEmpty()) {
            Weather firstForecastWeather = forecast.get(0);
            stringBuilder.append("\n");
            stringBuilder.append(String.format(
                    "Forecast\nDesc: %s \nTimeUnix: %d\nTemp: %.1f\nSpeed wind: %.1f",
                    firstForecastWeather.description,
                    firstForecastWeather.time,
                    firstForecastWeather.temp,
                    firstForecastWeather.speedWind
            ));
        }
        resultTextView.setText(stringBuilder.toString());
    }

    private void showProgress(boolean visible) {
        progressBar.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    private void showError(@NonNull String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    private void performRequest(@NonNull String city) {
        // Здесь необходимо написать свой код.
    }

}

