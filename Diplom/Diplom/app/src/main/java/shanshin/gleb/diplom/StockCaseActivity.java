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


import java.util.ArrayList;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import shanshin.gleb.diplom.api.AccountApi;
import shanshin.gleb.diplom.model.UniversalStock;
import shanshin.gleb.diplom.responses.InfoResponse;


public class StockCaseActivity extends AppCompatActivity implements StockContatiner {
    private RecyclerView stocksView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerViewSkeletonScreen skeletonStocks;
    private ViewSkeletonScreen skeletonHeader;
    private TextView nameView, balanceView;
    private FloatingActionButton fabView;
    private Toolbar toolbar;
    private TextView noStocks;
    private RelativeLayout cardView;
    private StockAdapter stockAdapter;
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
        getInfoAboutAccount(getString(R.string.use_cached));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SearchActivity.REQUEST_CODE && resultCode == SearchActivity.NEED_UPDATE)
            getInfoAboutAccount(getString(R.string.ignore_cache));
    }

    private void initializeViews() {
        cardView = findViewById(R.id.card);
        nameView = findViewById(R.id.name);
        noStocks = findViewById(R.id.noStocks);
        toolbar = findViewById(R.id.main_toolbar);
        balanceView = findViewById(R.id.balance);
        fabView = findViewById(R.id.addFloatingButton);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getInfoAboutAccount(getString(R.string.ignore_cache));
            }
        });

        bottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_dialog_layout, null);
        bottomSheetDialog.setContentView(sheetView);

        stocksView = findViewById(R.id.stocksView);
        stocksView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        stocksView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) fabView.hide();
                else if (dy < 0) fabView.show();
            }
        });
        stockAdapter = new StockAdapter(this, new ArrayList<UniversalStock>(), null);
        stocksView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    private void getInfoAboutAccount(String headerValue) {
        swipeRefreshLayout.setRefreshing(true);
        AccountApi accountApi = App.getInstance().getRetrofit().create(AccountApi.class);
        accountApi.getAccountInfo(App.getInstance().getDataHandler().getAccessToken(), headerValue).enqueue(new Callback<InfoResponse>() {
            @Override
            public void onResponse(Call<InfoResponse> call, Response<InfoResponse> response) {
                try {
                    if (response != null && !response.isSuccessful() && response.errorBody() != null) {
                        App.getInstance().getErrorHandler().handleDefaultError(response.errorBody());
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
                cancelSkeletonLoading();
                swipeRefreshLayout.setRefreshing(false);
                App.getInstance().getUtils().showError(getString(R.string.no_connection));

            }
        });
    }

    private void fillActivityView(InfoResponse infoResponse) {
        nameView.setText(infoResponse.name);
        balanceView.setText(App.getInstance().getUtils().formatFloat(2, infoResponse.balance) + getString(R.string.currency));
        stocksView.setAdapter(stockAdapter);
        stockAdapter.setStocks(App.getInstance().getMapUtils().mapStocksToUniversalStocks(infoResponse.stocks, null));
        noStocks.setVisibility(stockAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        stocksView.setLayoutFrozen(stockAdapter.getItemCount() < 7);
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
        skeletonHeader = Skeleton.bind(cardView)
                .load(R.layout.header_skeleton)
                .show();
        skeletonStocks = Skeleton.bind(stocksView)
                .load(R.layout.stock_skeleton_item)
                .adapter(new StockAdapter())
                .show();
    }

    public void switchToSearchStocks(View view) {
        switchToSearchActivity(SearchActivity.SEARCH_STOCKS);
    }

    @Override
    public void stockClicked(UniversalStock stock) {
        App.getInstance().getDialogHandler().initializeDialog(bottomSheetDialog, stock.id, stock.nameField, false, this);
    }

    @Override
    public void requestSuccess() {
        getInfoAboutAccount(getString(R.string.ignore_cache));
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