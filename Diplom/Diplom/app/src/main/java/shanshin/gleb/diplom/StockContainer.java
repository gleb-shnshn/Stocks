package shanshin.gleb.diplom;

import shanshin.gleb.diplom.model.UniversalStock;

public interface StockContainer {
    void onStockClick(UniversalStock stock);
    void onStockLongClick(UniversalStock stock);

    void runOnUiThread(Runnable runnable);
    void requestSuccess();
}
