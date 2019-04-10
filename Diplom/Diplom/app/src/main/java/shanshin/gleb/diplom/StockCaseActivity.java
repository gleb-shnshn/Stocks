package shanshin.gleb.diplom;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ethanhua.skeleton.RecyclerViewSkeletonScreen;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.ViewSkeletonScreen;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;


public class StockCaseActivity extends AppCompatActivity implements ObservableScrollViewCallbacks {
    ObservableRecyclerView stocksView;
    RecyclerViewSkeletonScreen skeletonStocks;
    ViewSkeletonScreen skeletonHeader;
    TextView nameView, balanceView;
    FloatingActionButton fabView;
    CardView cardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_case);
        initializeViews();
        setSkeletonLoading(stocksView, cardView);
        getInfoAboutAccount();
    }

    private void initializeViews() {
        stocksView = findViewById(R.id.stocksView);
        cardView = findViewById(R.id.card);
        nameView = findViewById(R.id.name);
        balanceView = findViewById(R.id.balance);
        fabView = findViewById(R.id.addFloatingButton);
        stocksView.setScrollViewCallbacks(this);
        stocksView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) fabView.hide();
                else if (dy < 0) fabView.show();
            }
        });
    }

    private void getInfoAboutAccount() {
        AccountApi accountApi = App.getInstance().getRetrofit().create(AccountApi.class);
        accountApi.getAccountInfo(getAccessToken()).enqueue(new Callback<InfoResponse>() {
            @Override
            public void onResponse(Call<InfoResponse> call, Response<InfoResponse> response) {
                try {
                    if (response != null && !response.isSuccessful() && response.errorBody() != null) {
                        Converter<ResponseBody, DefaultErrorResponse> errorConverter =
                                App.getInstance().getRetrofit().responseBodyConverter(DefaultErrorResponse.class, new Annotation[0]);
                        DefaultErrorResponse errorResponse = errorConverter.convert(response.errorBody());
                        App.showError(getApplicationContext(), errorResponse.message);
                    } else {
                        InfoResponse infoResponse = response.body();
                        cancelSkeletonLoading();
                        fillActivityView(infoResponse);
                    }

                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(Call<InfoResponse> call, Throwable t) {

            }
        });
    }

    private void fillActivityView(InfoResponse infoResponse) {
        nameView.setText(infoResponse.name);
        balanceView.setText(infoResponse.balance + "\u20BD");
        stocksView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        stocksView.setAdapter(new StockAdapter(getApplicationContext(), new ArrayList<>(infoResponse.stocks)));

    }

    private void cancelSkeletonLoading() {
        skeletonHeader.hide();
        skeletonStocks.hide();
    }

    public void setSkeletonLoading(RecyclerView stockList, CardView header) {
        stocksView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        skeletonHeader = Skeleton.bind(header)
                .load(R.layout.header_skeleton)
                .duration(1200)
                .show();
        skeletonStocks = Skeleton.bind(stockList)
                .load(R.layout.stock_skeleton_item)
                .duration(1200)
                .adapter(new StockAdapter())
                .show();
    }

    public String getAccessToken() {
        SharedPreferences sPref = getSharedPreferences("tokens", MODE_PRIVATE);
        return sPref.getString("accessToken", "");
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {

    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }
}