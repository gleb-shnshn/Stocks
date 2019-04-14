package shanshin.gleb.diplom;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.ethanhua.skeleton.RecyclerViewSkeletonScreen;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.ViewSkeletonScreen;

import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import shanshin.gleb.diplom.api.AccountApi;
import shanshin.gleb.diplom.responses.DefaultErrorResponse;
import shanshin.gleb.diplom.responses.InfoResponse;


public class StockCaseActivity extends AppCompatActivity {
    RecyclerView stocksView;
    RecyclerViewSkeletonScreen skeletonStocks;
    ViewSkeletonScreen skeletonHeader;
    TextView nameView, balanceView;
    FloatingActionButton fabView;
    Toolbar toolbar;
    CardView cardView;
    BottomSheetDialog bottomSheetDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_case);
        initializeViews();

        bottomSheetDialog.show();
        setSkeletonLoading();
        getInfoAboutAccount();
    }

    private void initializeViews() {
        stocksView = findViewById(R.id.stocksView);
        cardView = findViewById(R.id.card);
        nameView = findViewById(R.id.name);
        toolbar = findViewById(R.id.main_toolbar);
        balanceView = findViewById(R.id.balance);
        fabView = findViewById(R.id.addFloatingButton);
        bottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_dialog_layout, null);
        bottomSheetDialog.setContentView(sheetView);
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
        accountApi.getAccountInfo(App.getInstance().getAccessToken()).enqueue(new Callback<InfoResponse>() {
            @Override
            public void onResponse(Call<InfoResponse> call, Response<InfoResponse> response) {
                try {
                    if (response != null && !response.isSuccessful() && response.errorBody() != null) {
                        Converter<ResponseBody, DefaultErrorResponse> errorConverter =
                                App.getInstance().getRetrofit().responseBodyConverter(DefaultErrorResponse.class, new Annotation[0]);
                        DefaultErrorResponse errorResponse = errorConverter.convert(response.errorBody());
                        App.getInstance().showError(errorResponse.message);
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
        stocksView.setAdapter(new StockAdapter(getApplicationContext(), infoResponse.stocks));

    }

    private void cancelSkeletonLoading() {
        toolbar.setVisibility(View.VISIBLE);
        skeletonHeader.hide();
        skeletonStocks.hide();
    }

    public void setSkeletonLoading() {
        toolbar.setVisibility(View.GONE);
        stocksView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        skeletonHeader = Skeleton.bind(cardView)
                .load(R.layout.header_skeleton)
                .duration(1200)
                .show();
        skeletonStocks = Skeleton.bind(stocksView)
                .load(R.layout.stock_skeleton_item)
                .duration(1200)
                .adapter(new StockAdapter())
                .show();
    }

    public void switchToSearchStocks(View view) {
        startActivity(new Intent(this, StockSearchActivity.class));
    }
}