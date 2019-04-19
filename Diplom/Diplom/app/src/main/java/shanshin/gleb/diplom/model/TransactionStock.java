package shanshin.gleb.diplom.model;

import com.google.gson.annotations.SerializedName;

public class TransactionStock {

    @SerializedName("transactionId")
    int transactionId;

    @SerializedName("stock")
    Stock stock;

    @SerializedName("amount")
    int amount;

    @SerializedName("totalPrice")
    float totalPrice;

    @SerializedName("date")
    String date;

    @SerializedName("type")
    String type;

}
