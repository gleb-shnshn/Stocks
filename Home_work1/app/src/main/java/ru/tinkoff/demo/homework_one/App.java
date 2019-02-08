package ru.tinkoff.demo.homework_one;

import android.app.Application;


public class App extends Application {

    private DataHolder holder;

    public DataHolder getHolder() {
        return holder;
    }

    public void setHolder(DataHolder holder) {
        this.holder = holder;
    }

}