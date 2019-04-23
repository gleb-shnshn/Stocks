package shanshin.gleb.diplom.handlers;

import java.util.ArrayList;
import java.util.List;

import shanshin.gleb.diplom.App;
import shanshin.gleb.diplom.R;
import shanshin.gleb.diplom.model.Stock;
import shanshin.gleb.diplom.model.TransactionStock;
import shanshin.gleb.diplom.model.UniversalStock;

public class MapStockUtils {
    public ArrayList<UniversalStock> mapTransactionStocksToUniversalStocks(List<TransactionStock> stocks) {
        ArrayList<UniversalStock> newStocks = new ArrayList<>();
        for (TransactionStock tStock : stocks) {
            UniversalStock uStock = new UniversalStock();
            uStock.nameField = tStock.stock.name;
            uStock.countField = tStock.stock.code + App.getInstance().getString(R.string.dot_divider) + tStock.amount + App.getInstance().getString(R.string.pcs);
            uStock.redOrGreen = tStock.type.equals(App.getInstance().getString(R.string.sell_internet_response));
            uStock.deltaField = App.getInstance().getUtils().formatDate(tStock.date);

            uStock.iconUrl = tStock.stock.iconUrl.substring(1);
            App.getInstance().getUtils().setPriceAndPriceEndValue(uStock, tStock.totalPrice);

            newStocks.add(uStock);
        }

        return newStocks;
    }

    public ArrayList<UniversalStock> mapStocksToUniversalStocks(List<Stock> stocks, Integer activityCode) {
        ArrayList<UniversalStock> newStocks = new ArrayList<>();
        for (Stock stock : stocks) {
            UniversalStock uStock = new UniversalStock();
            uStock.nameField = stock.name;

            if (activityCode == null)
                uStock.countField = stock.count + App.getInstance().getString(R.string.pcs);
            else
                uStock.countField = stock.code;

            uStock.redOrGreen = stock.priceDelta < 0;

            String deltaPercents = App.getInstance().getUtils().formatFloat(4, stock.priceDelta / stock.price);
            String arrow = stock.priceDelta < 0 ? App.getInstance().getString(R.string.arrowDown) : App.getInstance().getString(R.string.arrowUp);
            uStock.deltaField = arrow + stock.priceDelta + App.getInstance().getString(R.string.currency) + String.format(App.getInstance().getString(R.string.percent_formatter), deltaPercents);

            uStock.iconUrl = stock.iconUrl.substring(1);
            App.getInstance().getUtils().setPriceAndPriceEndValue(uStock, stock.price);

            uStock.id = stock.id;

            newStocks.add(uStock);
        }
        return newStocks;
    }
}
