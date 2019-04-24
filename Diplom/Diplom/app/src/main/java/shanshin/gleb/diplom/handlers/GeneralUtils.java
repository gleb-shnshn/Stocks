package shanshin.gleb.diplom.handlers;

import com.muddzdev.styleabletoast.StyleableToast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import shanshin.gleb.diplom.App;
import shanshin.gleb.diplom.R;
import shanshin.gleb.diplom.model.UniversalStock;

public class GeneralUtils {

    final public static DateFormat DATE_FORMAT_PARSER = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
    final private DateFormat DATE_FORMAT_FORMATTER = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

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

    public boolean isCountNotValid(String countText) {
        if (countText.equals("")) {
            showError(App.getInstance().getString(R.string.stock_count_empty));
            return true;
        }
        int requestedCount;
        try {
             requestedCount = Integer.parseInt(countText);
        } catch (Exception e) {
            showError(App.getInstance().getString(R.string.too_long_number));            
             return true;
        }
        if (requestedCount < 1) {

            showError(App.getInstance().getString(R.string.stock_count_not_null));
            return true;
        }
        return false;
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

    public String formatFloat(int length, float value) {
        return String.format(Locale.ENGLISH, "%." + length + "f", value);
    }

    public void setPriceAndPriceEndValue(UniversalStock uStock, float value) {
        String priceFull = formatFloat(2, value);
        uStock.priceField = priceFull.substring(0, priceFull.length() - 3);
        uStock.priceEndField = priceFull.substring(priceFull.length() - 3) + App.getInstance().getString(R.string.currency);
    }

    public String formatDate(String date) {
        try {
            return DATE_FORMAT_FORMATTER.format(DATE_FORMAT_PARSER.parse(date));
        } catch (ParseException e) {
            return "";
        }
    }
}
