package shanshin.gleb.diplom.pagination;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PageKeyedDataSource;
import androidx.paging.PagedList;
import shanshin.gleb.diplom.model.UniversalStock;

public class StockViewModel extends ViewModel {

    private LiveData stockPagedList;
    private UniversalStockDataSourceFactory stockDataSourceFactory;

    public UniversalStockDataSourceFactory getDataSourceFactory(){
        return stockDataSourceFactory;
    }
    public LiveData<PagedList<UniversalStock>> getStockPagedList(){
        return stockPagedList;
    }
    public StockViewModel() {
        stockDataSourceFactory = new UniversalStockDataSourceFactory();
        //LiveData<PageKeyedDataSource<Integer, UniversalStock>> liveDataSource = stockDataSourceFactory.getItemLiveDataSource();

        PagedList.Config pagedListConfig =
                (new PagedList.Config.Builder())
                        .setEnablePlaceholders(false)
                        .setPageSize(UniversalStockDataSource.PAGE_SIZE).build();

        stockPagedList = new LivePagedListBuilder(stockDataSourceFactory, pagedListConfig)
                .build();

    }
}
