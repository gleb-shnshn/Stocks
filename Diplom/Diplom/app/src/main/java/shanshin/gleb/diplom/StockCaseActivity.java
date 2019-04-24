package shanshin.gleb.diplom;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ImageView;
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


public class StockCaseActivity extends AppCompatActivity implements StockContainer {
    private RecyclerView stocksRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerViewSkeletonScreen skeletonStocks;
    private ViewSkeletonScreen skeletonHeader;
    private TextView nameView, balanceView;
    private FloatingActionButton fabView;
    private Toolbar toolbar;
    private TextView noStocks;
    private ImageView icon;
    private RelativeLayout cardView;
    private StockAdapter stockAdapter;
    private BottomSheetDialog bottomSheetDialog;
    private boolean isIntentCreated;


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
    protected void onResume() {
        super.onResume();
        isIntentCreated = false;
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
        toolbar = findViewById(R.id.toolbar);
        icon = findViewById(R.id.icon);
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

        stocksRecyclerView = findViewById(R.id.stocksView);
        stocksRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        stocksRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) fabView.hide();
                else if (dy < 0) fabView.show();
            }
        });
        stockAdapter = new StockAdapter(this, new ArrayList<UniversalStock>(), null);
        stocksRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
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
        nameView.setText(infoResponse.name + infoResponse.surname);
        GlideToVectorYou.justLoadImage(this, Uri.parse(getString(R.string.server_url) + infoResponse.icon), icon);
        balanceView.setText(App.getInstance().getUtils().formatFloat(2, infoResponse.balance) + getString(R.string.currency)+" ");
        stocksRecyclerView.setAdapter(stockAdapter);
        stockAdapter.setStocks(App.getInstance().getMapUtils().mapStocksToUniversalStocks(infoResponse.stocks, null));
        noStocks.setVisibility(stockAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        setExpandingAndScrollingEnabled(stockAdapter.getItemCount()<7);
    }

    private void setExpandingAndScrollingEnabled(boolean enabled) {
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) findViewById(R.id.appbarlayout).getLayoutParams();
        ((AppBarLayoutBehavior)layoutParams.getBehavior()).setScrollBehavior(!enabled);

    }

    private void cancelSkeletonLoading() {
        swipeRefreshLayout.setRefreshing(false);
        toolbar.setVisibility(View.VISIBLE);
        skeletonHeader.hide();
        skeletonStocks.hide();
        fabView.show();
    }

    private void setSkeletonLoading() {
        toolbar.setVisibility(View.GONE);
        fabView.hide();
        skeletonHeader = Skeleton.bind(cardView)
                .load(R.layout.header_skeleton)
                .show();
        skeletonStocks = Skeleton.bind(stocksRecyclerView)
                .load(R.layout.stock_skeleton_item)
                .adapter(new StockAdapter())
                .show();
    }

    public void switchToSearchStocks(View view) {
        switchToSearchActivity(SearchActivity.SEARCH_STOCKS);
    }

    @Override
    public void onStockClick(UniversalStock stock) {
        App.getInstance().getDialogHandler().initializeDialog(bottomSheetDialog, stock.id, stock.nameField, false, this);
    }

    @Override
    public void onStockLongClick(UniversalStock stock) {
        Intent intent = new Intent(this, ChartActivity.class);
        intent.putExtra("stockId", stock.id);
        intent.putExtra("priceDelta", stock.deltaField);
        intent.putExtra("price", stock.priceField);
        intent.putExtra("priceEnd", stock.priceEndField);
        intent.putExtra("redOrGreen", stock.redOrGreen);
        startActivity(intent);
    }

    @Override
    public void onRequestSuccess() {
        getInfoAboutAccount(getString(R.string.ignore_cache));
    }

    private void switchToSearchActivity(int activityCode) {
        if (isIntentCreated)
            return;
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra("activityCode", activityCode);
        startActivityForResult(intent, SearchActivity.REQUEST_CODE);
        isIntentCreated = true;
    }

    public void switchToTransactionHistory(View view) {
        switchToSearchActivity(SearchActivity.TRANSACTION_HISTORY);
    }
}