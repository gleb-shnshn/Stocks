package shanshin.gleb.diplom;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.ethanhua.skeleton.RecyclerViewSkeletonScreen;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.ViewSkeletonScreen;


import java.lang.annotation.Annotation;
import java.util.Locale;

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
    private RecyclerView stocksView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerViewSkeletonScreen skeletonStocks;
    private ViewSkeletonScreen skeletonHeader;
    private TextView nameView, balanceView;
    private FloatingActionButton fabView;
    private Toolbar toolbar;
    private RelativeLayout cardView;
    private BottomSheetDialog bottomSheetDialog;


    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_case);
        initializeViews();
        setSkeletonLoading();
        getInfoAboutAccount();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SearchActivity.REQUEST_CODE && resultCode == SearchActivity.NEED_UPDATE)
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
        stocksView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

    }

    private void getInfoAboutAccount() {
        swipeRefreshLayout.setRefreshing(true);
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
        balanceView.setText(String.format(Locale.ENGLISH,"%.2f", infoResponse.balance) + "\u20BD  ");
        stocksView.setAdapter(new StockAdapter(this, infoResponse.stocks, null, null));

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
        switchToSearchActivity(SearchActivity.SEARCH_STOCKS);
    }

    @Override
    public void stockClicked(Stock stock) {
        App.getInstance().getDialogHandler().initializeDialog(bottomSheetDialog, stock, false, this);
    }

    @Override
    public void requestSuccess() {
        getInfoAboutAccount();
    }

    @Override
    public void requestError() {

    }

    public void switchToSearchActivity(int activityCode) {
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra("activityCode", activityCode);
        startActivityForResult(intent, SearchActivity.REQUEST_CODE);
    }

    public void switchToTransactionHistory(View view) {
        switchToSearchActivity(SearchActivity.TRANSACTION_HISTORY);
    }
}