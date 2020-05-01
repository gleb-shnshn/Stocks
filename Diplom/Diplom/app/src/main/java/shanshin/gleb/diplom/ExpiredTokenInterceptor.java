package shanshin.gleb.diplom;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ExpiredTokenInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        if (response.code() == 401 && !App.getInstance().getDataHandler().getAccessToken().equals("")) {
            App.getInstance().refreshToken();
            Request original = chain.request();
            Request newRequest = original.newBuilder().header("Authorization", App.getInstance().getDataHandler().getAccessToken()).build();
            return chain.proceed(newRequest);
        }
        return response;
    }
}
