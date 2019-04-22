package shanshin.gleb.diplom.pagination;

import java.io.IOException;

import androidx.annotation.NonNull;


import androidx.paging.PageKeyedDataSource;

import retrofit2.Response;
import shanshin.gleb.diplom.App;
import shanshin.gleb.diplom.SearchActivity;
import shanshin.gleb.diplom.api.StocksApi;
import shanshin.gleb.diplom.api.TransactionApi;
import shanshin.gleb.diplom.model.Page;
import shanshin.gleb.diplom.model.UniversalStock;
import shanshin.gleb.diplom.responses.StocksResponse;
import shanshin.gleb.diplom.responses.TransactionHistoryResponse;

public class UniversalStockDataSource extends PageKeyedDataSource<Integer, UniversalStock> {
    private int activityCode = App.getInstance().getDataHandler().getActivityCode();
    public static final int PAGE_SIZE = 25;
    private static final int FIRST_PAGE = 0;

    private Page getPage(int activityCode, int itemId) throws IOException {
        String query = App.getInstance().getDataHandler().getQuery();
        if (activityCode == SearchActivity.SEARCH_STOCKS) {
            Response<StocksResponse> response = App.getInstance().getRetrofit().create(StocksApi.class).
                    getStocks(App.getInstance().getDataHandler().getAccessToken(), query, PAGE_SIZE, itemId)
                    .execute();
            if (response.body() != null) {
                StocksResponse body = response.body();
                return new Page(body.prevItemId, body.nextItemId, App.getInstance().getMapUtils().mapStocksToUniversalStocks(body.items, activityCode));
            }
        } else {
            Response<TransactionHistoryResponse> response = App.getInstance().getRetrofit().create(TransactionApi.class).
                    getTransactionHistory(App.getInstance().getDataHandler().getAccessToken(), query, PAGE_SIZE, itemId)
                    .execute();
            if (response.body() != null) {
                TransactionHistoryResponse body = response.body();
                return new Page(body.prevItemId, body.nextItemId, App.getInstance().getMapUtils().mapTransactionStocksToUniversalStocks(body.items));

            }
        }
        return null;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params,
                            @NonNull final LoadInitialCallback<Integer, UniversalStock> callback) {
        try {
            Page page = getPage(activityCode, FIRST_PAGE);
            callback.onResult(page.stocks, null, page.nextItemId);
        } catch (Exception ignored) {
        }
    }


    @Override
    public void loadBefore(@NonNull final LoadParams<Integer> params,
                           @NonNull final LoadCallback<Integer, UniversalStock> callback) {
        try {
            Page page = getPage(activityCode, params.key);
            callback.onResult(page.stocks, page.prevItemId == 0 ? null : page.prevItemId);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void loadAfter(@NonNull final LoadParams<Integer> params,
                          @NonNull final LoadCallback<Integer, UniversalStock> callback) {
        try {
            Page page = getPage(activityCode, params.key);
            callback.onResult(page.stocks, page.nextItemId);
        } catch (Exception ignored) {
        }
    }
}