package ru.tinkoff.demo.homework_one;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity {

    private DataHolder holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);
        App application = (App) getApplication();
        holder = application.getHolder();
        showData();
    }

    private void showData() {
        TextView hashCodeText = findViewById(R.id.hasCodeText);
        TextView dataText = findViewById(R.id.dataText);
        hashCodeText.setText(String.valueOf(holder.hashCode()));
        dataText.setText(holder.getData());
    }

}