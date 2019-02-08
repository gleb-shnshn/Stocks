package ru.tinkoff.demo.homework_one;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private DataHolder holder;

    private Button moveToSecondButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        moveToSecondButton = findViewById(R.id.openSecondButton);
        moveToSecondButton.setEnabled(false);
        findViewById(R.id.initButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                init();
            }
        });
        findViewById(R.id.openSecondButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSecond();
            }
        });
    }

    private void init() {
        holder = new DataHolder() {
            @Override
            public String getData() {
                return "Hello Tinkoff FinTech!!!";
            }
        };
        TextView hashCodeText = findViewById(R.id.hasCodeText);
        TextView dataText = findViewById(R.id.dataText);
        hashCodeText.setText(String.valueOf(holder.hashCode()));
        dataText.setText(holder.getData());
        moveToSecondButton.setEnabled(true);
    }

    private void openSecond() {
        ((App) getApplication()).setHolder(holder);
        startActivity(new Intent(this, SecondActivity.class));
        finish();
    }

}
