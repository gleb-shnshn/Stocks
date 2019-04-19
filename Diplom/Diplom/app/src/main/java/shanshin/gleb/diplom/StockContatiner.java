package shanshin.gleb.diplom;

import shanshin.gleb.diplom.model.Stock;

public interface StockContatiner {
    void stockClicked(Stock stock);
    void runOnUiThread(Runnable runnable);
    void requestSuccess();
    void requestError();
}
