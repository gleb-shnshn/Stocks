package shanshin.gleb.diplom;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;


import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import shanshin.gleb.diplom.api.StocksApi;
import shanshin.gleb.diplom.api.TransactionApi;
import shanshin.gleb.diplom.model.UniversalStock;
import shanshin.gleb.diplom.responses.StocksResponse;
import shanshin.gleb.diplom.responses.TransactionHistoryResponse;

public class SearchActivity extends AppCompatActivity implements StockContatiner {
    static final int NEED_UPDATE = 211;
    static final int REQUEST_CODE = 582;

    SearchView searchView;
    TextView titleText;
    StocksApi stocksApi;
    TransactionApi transactionApi;
    RecyclerView stocksView;
    StockAdapter stockAdapter;
    String lastQuery = "";
    BottomSheetDialog bottomSheetDialog;
    final int DEFAULT_COUNT = 50;
    static final int TRANSACTION_HISTORY = 1;
    static final int SEARCH_STOCKS = 2;
    int activityCode;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(0);
        setContentView(R.layout.activity_search);
        initializeViews();
        updateStockList("");
    }

    private void initializeViews() {
        searchView = findViewById(R.id.searchView);
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

        titleText = findViewById(R.id.title);

        activityCode = getIntent().getIntExtra("activityCode", 0);
        if (activityCode == TRANSACTION_HISTORY) {
            titleText.setText(getString(R.string.transaction_history_title));
            transactionApi = App.getInstance().getRetrofit().create(TransactionApi.class);
        } else if (activityCode == SEARCH_STOCKS) {
            titleText.setText(getString(R.string.stock_search_title));
            stocksApi = App.getInstance().getRetrofit().create(StocksApi.class);
        } else
            throw new RuntimeException(getString(R.string.wrong_code_exception));


        stocksView = findViewById(R.id.stocksView);
        stocksView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        stockAdapter = new StockAdapter(this, new ArrayList<UniversalStock>(), activityCode);
        stocksView.setAdapter(stockAdapter);
        stocksView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        bottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_dialog_layout, null);
        bottomSheetDialog.setContentView(sheetView);

    }

    private void updateStockList(String query) {
        int itemCount = stockAdapter.getItemCount();
        int lastLength = lastQuery.length();

        stockAdapter.setStocks(App.getInstance().getUtils().localQuery(query, stockAdapter.getStocks()));
        lastQuery = query;

        if (lastLength < query.length() && itemCount < DEFAULT_COUNT) {
            return;
        }
        if (activityCode == TRANSACTION_HISTORY)
            transactionApi.getTransactionHistory(App.getInstance().getDataHandler().getAccessToken(), query, DEFAULT_COUNT, 0).enqueue(new Callback<TransactionHistoryResponse>() {
                @Override
                public void onResponse(Call<TransactionHistoryResponse> call, Response<TransactionHistoryResponse> response) {
                    try {
                        handleHistoryResponse(response);
                    } catch (Exception ignored) {
                    }
                }

                @Override
                public void onFailure(Call<TransactionHistoryResponse> call, Throwable t) {
                }
            });
        else
            stocksApi.getStocksWithOffset(App.getInstance().getDataHandler().getAccessToken(), query, DEFAULT_COUNT, 0).enqueue(new Callback<StocksResponse>() {
                @Override
                public void onResponse(Call<StocksResponse> call, Response<StocksResponse> response) {
                    try {
                        handleStocksResponse(response);
                    } catch (Exception ignored) {
                    }
                }

                @Override
                public void onFailure(Call<StocksResponse> call, Throwable t) {

                }
            });
    }

    private boolean handleResponseErrors(boolean isSuccessful, ResponseBody errorBody) throws IOException {
        if (!isSuccessful && errorBody != null) {
            App.getInstance().getErrorHandler().handleDefaultError(errorBody);
            return false;
        } else {
            return true;
        }
    }

    private void handleHistoryResponse(Response<TransactionHistoryResponse> response) throws IOException {
        if (handleResponseErrors(response.isSuccessful(), response.errorBody())) {
            TransactionHistoryResponse transactionResponse = response.body();
            stockAdapter.setStocks(App.getInstance().getMapUtils().mapTransactionStocksToUniversalStocks(transactionResponse.items));
        }
    }

    private void handleStocksResponse(Response<StocksResponse> response) throws IOException {
        if (handleResponseErrors(response.isSuccessful(), response.errorBody())) {
            StocksResponse stocksResponse = response.body();
            stockAdapter.setStocks(App.getInstance().getMapUtils().mapStocksToUniversalStocks(stocksResponse.items, activityCode));
        }
    }

    @Override
    public void stockClicked(UniversalStock stock) {
        if (activityCode == TRANSACTION_HISTORY)
            return;

        App.getInstance().getDialogHandler().initializeDialog(bottomSheetDialog, stock.id, stock.nameField, true, this);
    }


    @Override
    public void requestSuccess() {
        setResult(NEED_UPDATE);
        updateStockList(lastQuery);

    }

}
