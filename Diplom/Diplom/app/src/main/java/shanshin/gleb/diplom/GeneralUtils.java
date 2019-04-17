package shanshin.gleb.diplom;

import com.muddzdev.styleabletoast.StyleableToast;

public class GeneralUtils {

    public void showError(final String errorMessage) {
        new StyleableToast
                .Builder(App.getInstance())
                .text(errorMessage)
                .cornerRadius(5)
                .textSize(13)
                .textColor(App.getInstance().getResources().getColor(R.color.white))
                .backgroundColor(App.getInstance().getResources().getColor(R.color.errorColor))
                .show();
    }
    public  boolean checkingCount(String countText, int availableCount) {
        int requestedCount = Integer.parseInt(countText);
        if (requestedCount > availableCount) {
            showError("Максимально доступное количество " + availableCount + " шт.");
            return false;
        } else if (requestedCount < 1) {
            showError("Количество акций должно быть больше нуля");
            return false;
        }
        return true;
    }
}
