package shanshin.gleb.diplom;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.paging.PagedList;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;


import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import shanshin.gleb.diplom.model.UniversalStock;
import shanshin.gleb.diplom.pagination.StockViewModel;

public class SearchActivity extends AppCompatActivity implements StockContainer {
    static final int NEED_UPDATE = 211;
    static final int REQUEST_CODE = 582;

    public static final int TRANSACTION_HISTORY = 1;
    public static final int SEARCH_STOCKS = 2;

    private TextView titleText;
    private String lastQuery = "";
    private SwipeRefreshLayout swipeRefreshLayout;
    private BottomSheetDialog bottomSheetDialog;
    private int activityCode;
    private StockViewModel stockViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(0);
        setContentView(R.layout.activity_search);
        initializeViews();
    }

    private void initializeViews() {
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                titleText.setVisibility(View.GONE);
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                titleText.setVisibility(View.VISIBLE);
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                updateStockList(s);
                return false;
            }
        });

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateStockList(lastQuery);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        titleText = findViewById(R.id.title);

        activityCode = getIntent().getIntExtra("activityCode", 0);
        App.getInstance().getDataHandler().setActivityCode(activityCode);
        if (activityCode == TRANSACTION_HISTORY) {
            titleText.setText(getString(R.string.transaction_history_title));
        } else if (activityCode == SEARCH_STOCKS) {
            titleText.setText(getString(R.string.stock_search_title));
        } else
            throw new RuntimeException(getString(R.string.wrong_code_exception));


        RecyclerView stocksView = findViewById(R.id.stocksView);
        stocksView.setLayoutManager(new LinearLayoutManager(this));
        stocksView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        stocksView.setHasFixedSize(true);
        stockViewModel = ViewModelProviders.of(this).get(StockViewModel.class);
        final StockAdapter stockAdapter = new StockAdapter(this, null, activityCode);
        Observer<PagedList<UniversalStock>> observer = new Observer<PagedList<UniversalStock>>() {
            @Override
            public void onChanged(@Nullable PagedList<UniversalStock> items) {
                stockAdapter.submitList(items);
            }
        };
        stockViewModel.getStockPagedList().observe(this, observer);
        stocksView.setAdapter(stockAdapter);

        bottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_dialog_layout, null);
        bottomSheetDialog.setContentView(sheetView);

    }

    private void updateStockList(String query) {
        lastQuery = query;
        App.getInstance().getDataHandler().setQuery(lastQuery);
        stockViewModel.getDataSourceFactory().onQueryUpdated();

    }

    @Override
    public void onStockClick(UniversalStock stock) {
        if (activityCode == TRANSACTION_HISTORY)
            return;

        App.getInstance().getDialogHandler().initializeDialog(bottomSheetDialog, stock.id, stock.nameField, true, this);
    }

    @Override
    public void onStockLongClick(UniversalStock stock) {
        if (activityCode == TRANSACTION_HISTORY)
            return;
        Intent intent = new Intent(this, ChartActivity.class);
        intent.putExtra("stockId", stock.id);
        intent.putExtra("priceDelta", stock.deltaField);
        intent.putExtra("price", stock.priceField);
        intent.putExtra("priceEnd", stock.priceEndField);
        intent.putExtra("redOrGreen", stock.redOrGreen);
        startActivity(intent);
    }


    @Override
    public void requestSuccess() {
        setResult(NEED_UPDATE);
        updateStockList(lastQuery);

    }

}
