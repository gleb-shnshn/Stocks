package shanshin.gleb.diplom.handlers;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;
import shanshin.gleb.diplom.App;
import shanshin.gleb.diplom.LoadingButton;
import shanshin.gleb.diplom.R;
import shanshin.gleb.diplom.StockContatiner;
import shanshin.gleb.diplom.api.TransactionApi;
import shanshin.gleb.diplom.model.Stock;
import shanshin.gleb.diplom.model.StockAmountAndId;
import shanshin.gleb.diplom.responses.BuyAndSellResponse;
import shanshin.gleb.diplom.responses.FieldErrorResponse;

public class BottomDialogHandler {

    public void initializeDialog(final BottomSheetDialog bottomSheetDialog, final Stock stock, final boolean isBuyOrSell, final StockContatiner context) {
        TextView stockName = bottomSheetDialog.findViewById(R.id.stock_name);
        final LoadingButton dialogButton = bottomSheetDialog.findViewById(R.id.dialogButton);
        stockName.setText(stock.name);
        dialogButton.setText(App.getInstance().getString(isBuyOrSell ? R.string.buy : R.string.sell));
        bottomSheetDialog.show();
        final EditText countField = bottomSheetDialog.findViewById(R.id.countInput);
        countField.setText("");
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!App.getInstance().getUtils().checkingCount(countField.getText().toString()))
                    return;
                dialogButton.startLoading();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            performRequest(Integer.parseInt(countField.getText().toString()), stock.id, context, isBuyOrSell, bottomSheetDialog);
                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialogButton.stopLoading();
                                }
                            });
                        } catch (IOException ignored) {
                        }

                    }
                }).start();
            }
        });
    }

    private void performRequest(int count, int id, final StockContatiner context, final boolean isBuyOrSell, final BottomSheetDialog bottomSheetDialog) throws IOException {
        TransactionApi transactionApi = App.getInstance().getRetrofit().create(TransactionApi.class);
        StockAmountAndId data = new StockAmountAndId(count, id);
        Response<BuyAndSellResponse> response;
        if (isBuyOrSell)
            response = transactionApi.buyStocks(App.getInstance().getDataHandler().getAccessToken(), data).execute();
        else
            response = transactionApi.sellStocks(App.getInstance().getDataHandler().getAccessToken(), data).execute();

        if (!response.isSuccessful() && response.errorBody() != null) {
            handleError(context, response);
        } else {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    App.getInstance().getUtils().showSuccess(App.getInstance().getString(isBuyOrSell ? R.string.success_buy : R.string.success_sell));
                    context.requestSuccess();
                    bottomSheetDialog.hide();
                }
            });

        }
    }

    private void handleError(final StockContatiner context, Response<BuyAndSellResponse> response) throws IOException {
        Converter<ResponseBody, FieldErrorResponse> errorConverter =
                App.getInstance().getRetrofit().responseBodyConverter(FieldErrorResponse.class, new Annotation[0]);
        final FieldErrorResponse errorResponse = errorConverter.convert(response.errorBody());
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (FieldErrorResponse.InvalidField invalidField : errorResponse.invalidFields) {
                    App.getInstance().getUtils().showError(invalidField.message);
                }
                context.requestError();
            }
        });
    }

}
