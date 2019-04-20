package shanshin.gleb.diplom;

import com.muddzdev.styleabletoast.StyleableToast;

import java.util.ArrayList;
import java.util.List;

import shanshin.gleb.diplom.model.Stock;
import shanshin.gleb.diplom.model.TransactionStock;

public class GeneralUtils {

    public void showError(String errorMessage) {
        new StyleableToast
                .Builder(App.getInstance())
                .text(errorMessage)
                .cornerRadius(5)
                .textSize(13)
                .textColor(App.getInstance().getResources().getColor(R.color.white))
                .backgroundColor(App.getInstance().getResources().getColor(R.color.errorColor))
                .show();
    }

    public boolean checkingCount(String countText) {
        if (countText.equals("")) {
            showError("Введите количество акций");
            return false;
        }
        int requestedCount = Integer.parseInt(countText);
        if (requestedCount < 1) {
            showError("Количество акций должно быть больше нуля");
            return false;
        }
        return true;
    }

    public void showSuccess(String successMessage) {
        new StyleableToast
                .Builder(App.getInstance())
                .text(successMessage)
                .cornerRadius(5)
                .textSize(13)
                .textColor(App.getInstance().getResources().getColor(R.color.white))
                .backgroundColor(App.getInstance().getResources().getColor(R.color.successColor))
                .show();
    }

    public ArrayList<Stock> localQuery(String query, List<Stock> stocks) {
        ArrayList<Stock> newStocks = new ArrayList<>();
        for (Stock stock : stocks) {
            if (stock.name.contains(query))
                newStocks.add(stock);
        }
        return newStocks;
    }

    public ArrayList<TransactionStock> localTransactionQuery(String query, List<TransactionStock> stocks) {
        ArrayList<TransactionStock> newStocks = new ArrayList<>();
        for (TransactionStock transactionStock : stocks) {
            if (transactionStock.stock.name.contains(query))
                newStocks.add(transactionStock);
        }
        return newStocks;
    }
}
