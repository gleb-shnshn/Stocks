package shanshin.gleb.diplom;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;


import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import shanshin.gleb.diplom.api.StocksApi;
import shanshin.gleb.diplom.api.TransactionApi;
import shanshin.gleb.diplom.model.Stock;
import shanshin.gleb.diplom.model.StockAmountAndId;
import shanshin.gleb.diplom.responses.BuyAndSellResponse;
import shanshin.gleb.diplom.responses.DefaultErrorResponse;
import shanshin.gleb.diplom.responses.FieldErrorResponse;
import shanshin.gleb.diplom.responses.FieldErrorResponse.InvalidField;
import shanshin.gleb.diplom.responses.StocksResponse;

public class StockSearchActivity extends AppCompatActivity implements StockContatiner {
    SearchView searchView;
    TextView titleText;
    StocksApi stocksApi;
    RecyclerView stocksView;
    StockAdapter stockAdapter;
    String lastQuery = "";
    BottomSheetDialog bottomSheetDialog;
    final int DEFAULT_COUNT = 50;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stocks);
        stocksView = findViewById(R.id.stocksView);
        searchView = findViewById(R.id.searchView);
        stockAdapter = new StockAdapter(this, new ArrayList<Stock>());
        stocksView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        stocksView.setAdapter(stockAdapter);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        titleText = findViewById(R.id.title);
        bottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_dialog_layout, null);
        bottomSheetDialog.setContentView(sheetView);
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
        stocksApi = App.getInstance().getRetrofit().create(StocksApi.class);
    }

    private void updateStockList(String query) {
        if (lastQuery.length() < query.length() && stockAdapter.getItemCount() < DEFAULT_COUNT) {
            stockAdapter.setStocks(localQuery(query));
            return;
        }
        stockAdapter.setStocks(localQuery(query));
        stocksApi.getStocks(App.getInstance().getDataHandler().getAccessToken(), query, DEFAULT_COUNT).enqueue(new Callback<StocksResponse>() {
            @Override
            public void onResponse(Call<StocksResponse> call, Response<StocksResponse> response) {
                try {
                    if (response != null && !response.isSuccessful() && response.errorBody() != null) {
                        Converter<ResponseBody, DefaultErrorResponse> errorConverter =
                                App.getInstance().getRetrofit().responseBodyConverter(DefaultErrorResponse.class, new Annotation[0]);
                        DefaultErrorResponse errorResponse = errorConverter.convert(response.errorBody());
                        App.getInstance().getUtils().showError(errorResponse.message);
                    } else {
                        StocksResponse stocksResponse = response.body();
                        stockAdapter.setStocks(stocksResponse.items);
                    }
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(Call<StocksResponse> call, Throwable t) {

            }
        });
    }

    private ArrayList<Stock> localQuery(String query) {
        ArrayList<Stock> newStocks = new ArrayList<>();
        for (Stock stock : stockAdapter.getStocks()) {
            if (stock.name.contains(query))
                newStocks.add(stock);
        }
        lastQuery = query;
        return newStocks;
    }

    @Override
    public void stockClicked(final Stock stock) {
        App.getInstance().initializeDialog(bottomSheetDialog, stock.name, "Купить");
        bottomSheetDialog.show();
        final EditText countField = bottomSheetDialog.findViewById(R.id.countInput);
        countField.setHint(countField.getHint() + "(макс. - " + stock.count + " шт.)");
        final CircularProgressButton dialogButton = bottomSheetDialog.findViewById(R.id.dialogButton);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!App.getInstance().getUtils().checkingCount(countField.getText().toString(), stock.count))
                    return;
                dialogButton.startMorphAnimation();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            buyStock(Integer.parseInt(countField.getText().toString()), stock.id);
                            dialogButton.startMorphRevertAnimation();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
    }


    private void buyStock(int count, int id) throws IOException {
        TransactionApi transactionApi = App.getInstance().getRetrofit().create(TransactionApi.class);
        StockAmountAndId data = new StockAmountAndId(count, id);
        Response<BuyAndSellResponse> response = transactionApi.buyStocks(App.getInstance().getDataHandler().getAccessToken(), data).execute();
        if (!response.isSuccessful() && response.errorBody() != null) {
            Converter<ResponseBody, FieldErrorResponse> errorConverter =
                    App.getInstance().getRetrofit().responseBodyConverter(FieldErrorResponse.class, new Annotation[0]);
            FieldErrorResponse errorResponse = errorConverter.convert(response.errorBody());

            for (InvalidField invalidField : errorResponse.invalidFields) {
                App.getInstance().getUtils().showError(invalidField.message);
            }
        } else {
            updateStockList(lastQuery);
        }
        /*        enqueue(new Callback<BuyAndSellResponse>() {
            @Override
            public void onResponse(Call<BuyAndSellResponse> call, Response<BuyAndSellResponse> response) {
                try {
                    if (!response.isSuccessful() && response.errorBody() != null) {
                        Converter<ResponseBody, FieldErrorResponse> errorConverter =
                                App.getInstance().getRetrofit().responseBodyConverter(FieldErrorResponse.class, new Annotation[0]);
                        FieldErrorResponse errorResponse = errorConverter.convert(response.errorBody());

                        for (InvalidField invalidField : errorResponse.invalidFields) {
                            App.getInstance().getUtils().showError(invalidField.message);
                        }
                    } else {

                    }

                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(Call<BuyAndSellResponse> call, Throwable t) {

            }
        });*/
    }
}
