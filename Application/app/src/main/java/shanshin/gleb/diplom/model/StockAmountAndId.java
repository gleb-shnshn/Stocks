package shanshin.gleb.diplom.model;

import com.google.gson.annotations.SerializedName;

public class StockAmountAndId {

    @SerializedName("stockId")
    int stockId;

    @SerializedName("amount")
    int amount;

    public StockAmountAndId(int amount, int stockId) {
        this.stockId = stockId;
        this.amount = amount;
    }
}
