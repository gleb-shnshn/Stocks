package shanshin.gleb.diplom;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.ethanhua.skeleton.RecyclerViewSkeletonScreen;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.ViewSkeletonScreen;


import java.lang.annotation.Annotation;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import shanshin.gleb.diplom.api.AccountApi;
import shanshin.gleb.diplom.model.Stock;
import shanshin.gleb.diplom.responses.DefaultErrorResponse;
import shanshin.gleb.diplom.responses.InfoResponse;


public class StockCaseActivity extends AppCompatActivity implements StockContatiner {
    RecyclerView stocksView;
    SwipeRefreshLayout swipeRefreshLayout;
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

        setSkeletonLoading();
        swipeRefreshLayout.setRefreshing(true);
        getInfoAboutAccount();
    }

    private void initializeViews() {
        stocksView = findViewById(R.id.stocksView);
        cardView = findViewById(R.id.card);
        nameView = findViewById(R.id.name);
        toolbar = findViewById(R.id.main_toolbar);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getInfoAboutAccount();
            }
        });
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
        accountApi.getAccountInfo(App.getInstance().getDataHandler().getAccessToken()).enqueue(new Callback<InfoResponse>() {
            @Override
            public void onResponse(Call<InfoResponse> call, Response<InfoResponse> response) {
                try {
                    if (response != null && !response.isSuccessful() && response.errorBody() != null) {
                        Converter<ResponseBody, DefaultErrorResponse> errorConverter =
                                App.getInstance().getRetrofit().responseBodyConverter(DefaultErrorResponse.class, new Annotation[0]);
                        DefaultErrorResponse errorResponse = errorConverter.convert(response.errorBody());
                        App.getInstance().getUtils().showError(errorResponse.message);
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
        stocksView.setAdapter(new StockAdapter(this, infoResponse.stocks));

    }

    private void cancelSkeletonLoading() {
        swipeRefreshLayout.setRefreshing(false);
        toolbar.setVisibility(View.VISIBLE);
        skeletonHeader.hide();
        skeletonStocks.hide();
        fabView.show();
    }

    public void setSkeletonLoading() {
        toolbar.setVisibility(View.GONE);
        fabView.hide();
        stocksView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
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

    @Override
    public void stockClicked(Stock stock) {
        App.getInstance().initializeDialog(bottomSheetDialog, stock.name, "Продать");
        bottomSheetDialog.show();
    }
}