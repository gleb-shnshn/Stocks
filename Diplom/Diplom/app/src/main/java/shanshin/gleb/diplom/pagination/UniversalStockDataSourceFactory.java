package shanshin.gleb.diplom.pagination;


import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.PageKeyedDataSource;
import shanshin.gleb.diplom.model.UniversalStock;

public class UniversalStockDataSourceFactory extends DataSource.Factory {
    private MutableLiveData<PageKeyedDataSource<Integer, UniversalStock>> itemLiveDataSource = new MutableLiveData<>();
    private UniversalStockDataSource stockDataSource;

    @Override
    public DataSource<Integer, UniversalStock> create() {
        stockDataSource = new UniversalStockDataSource();

        itemLiveDataSource.postValue(stockDataSource);

        return stockDataSource;
    }

    public MutableLiveData<PageKeyedDataSource<Integer, UniversalStock>> getItemLiveDataSource() {
        return itemLiveDataSource;
    }

    public void onQueryUpdated() {
        stockDataSource.invalidate();
    }
}
