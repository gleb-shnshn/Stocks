package shanshin.gleb.diplom.handlers;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.IOException;

import retrofit2.Response;
import shanshin.gleb.diplom.App;
import shanshin.gleb.diplom.LoadingButton;
import shanshin.gleb.diplom.R;
import shanshin.gleb.diplom.StockContainer;
import shanshin.gleb.diplom.api.TransactionApi;
import shanshin.gleb.diplom.model.StockAmountAndId;
import shanshin.gleb.diplom.responses.BuyAndSellResponse;

public class BottomDialogHandler {

    public void initializeDialog(final BottomSheetDialog bottomSheetDialog, final int stockId, final String name, final boolean isBuyOrSell, final StockContainer context) {
        TextView stockName = bottomSheetDialog.findViewById(R.id.stock_name);
        final LoadingButton dialogButton = bottomSheetDialog.findViewById(R.id.dialogButton);
        stockName.setText(name);
        dialogButton.setText(App.getInstance().getString(isBuyOrSell ? R.string.buy : R.string.sell));
        bottomSheetDialog.show();
        final EditText countField = bottomSheetDialog.findViewById(R.id.countInput);
        countField.setText("");
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (App.getInstance().getUtils().isCountNotValid(countField.getText().toString()))
                    return;
                dialogButton.startLoading();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            performRequest(Integer.parseInt(countField.getText().toString()), stockId, context, isBuyOrSell, bottomSheetDialog);
                        } catch (IOException exception) {
                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    bottomSheetDialog.hide();
                                }
                            });
                            App.getInstance().getErrorHandler().handleNoConnection();
                        } finally {
                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialogButton.stopLoading();
                                }
                            });
                        }

                    }
                }).start();
            }
        });
    }

    private void performRequest(int count, int id, final StockContainer context, final boolean isBuyOrSell, final BottomSheetDialog bottomSheetDialog) throws IOException {
        TransactionApi transactionApi = App.getInstance().getRetrofit().create(TransactionApi.class);
        StockAmountAndId data = new StockAmountAndId(count, id);
        final Response<BuyAndSellResponse> response;
        if (isBuyOrSell)
            response = transactionApi.buyStocks(App.getInstance().getDataHandler().getAccessToken(), data).execute();
        else
            response = transactionApi.sellStocks(App.getInstance().getDataHandler().getAccessToken(), data).execute();

        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!response.isSuccessful() && response.errorBody() != null) {
                    App.getInstance().getErrorHandler().handleFieldError(response.errorBody());
                } else {
                    App.getInstance().getUtils().showSuccess(App.getInstance().getString(isBuyOrSell ? R.string.success_buy : R.string.success_sell));
                    context.onRequestSuccess();
                    bottomSheetDialog.hide();
                }
            }
        });
    }

}
