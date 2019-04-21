package shanshin.gleb.diplom.handlers;

import com.muddzdev.styleabletoast.StyleableToast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import shanshin.gleb.diplom.App;
import shanshin.gleb.diplom.R;
import shanshin.gleb.diplom.model.UniversalStock;

public class GeneralUtils {

    private DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
    private DateFormat dfNew = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

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

    public ArrayList<UniversalStock> localQuery(String query, List<UniversalStock> stocks) {
        ArrayList<UniversalStock> newStocks = new ArrayList<>();
        for (UniversalStock stock : stocks) {
            if (stock.nameField.contains(query))
                newStocks.add(stock);
        }
        return newStocks;
    }

    public String formatFloat(int length, float value) {
        return String.format(Locale.ENGLISH, "%." + length + "f", value);
    }

    public void setPriceAndPriceEndValue(UniversalStock uStock, float value) {
        String priceFull = formatFloat(2, value);
        uStock.priceField = priceFull.substring(0, priceFull.length() - 3);
        uStock.priceEndField = priceFull.substring(priceFull.length() - 3) + App.getInstance().getString(R.string.currency);
    }

    public String formatDate(String date) {
        if (date.contains("T")) {
            try {
                return dfNew.format(df.parse(date));
            } catch (ParseException e) {
                return "";
            }
        } else {
            return date;
        }

    }
}
