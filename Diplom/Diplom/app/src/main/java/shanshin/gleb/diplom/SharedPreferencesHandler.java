package shanshin.gleb.diplom;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHandler {
    private SharedPreferences sPref;

    public SharedPreferencesHandler() {
        this.sPref = App.getInstance().getSharedPreferences("tokens", Context.MODE_PRIVATE);
    }

    public String getRefreshToken() {
        return sPref.getString("refreshToken", "");
    }

    public String getAccessToken() {
        return sPref.getString("accessToken", "");
    }

    public void saveTokens(String accessToken, String refreshToken) {
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("accessToken", accessToken);
        ed.putString("refreshToken", refreshToken);
        ed.apply();
    }
}
