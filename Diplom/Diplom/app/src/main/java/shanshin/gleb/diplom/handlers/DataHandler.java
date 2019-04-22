package shanshin.gleb.diplom.handlers;

import android.content.Context;
import android.content.SharedPreferences;

import shanshin.gleb.diplom.App;

public class DataHandler {
    private final SharedPreferences SHARED_PREFERENCES;

    private int activityCode;
    private String query;

    public int getActivityCode() {
        return activityCode;
    }

    public void setActivityCode(int activityCode) {
        this.activityCode = activityCode;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public DataHandler() {
        this.SHARED_PREFERENCES = App.getInstance().getSharedPreferences("tokens", Context.MODE_PRIVATE);
    }

    public String getRefreshToken() {
        return SHARED_PREFERENCES.getString("refreshToken", "");
    }

    public String getAccessToken() {
        return SHARED_PREFERENCES.getString("accessToken", "");
    }

    public void saveTokens(String accessToken, String refreshToken) {
        SharedPreferences.Editor ed = SHARED_PREFERENCES.edit();
        ed.putString("accessToken", accessToken);
        ed.putString("refreshToken", refreshToken);
        ed.apply();
    }

}
