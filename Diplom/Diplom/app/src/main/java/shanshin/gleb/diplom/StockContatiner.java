package shanshin.gleb.diplom;

import shanshin.gleb.diplom.model.UniversalStock;

public interface StockContatiner {
    void stockClicked(UniversalStock stock);
    void runOnUiThread(Runnable runnable);
    void requestSuccess();
}
