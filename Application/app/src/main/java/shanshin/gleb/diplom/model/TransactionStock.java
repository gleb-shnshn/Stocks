package shanshin.gleb.diplom.model;

import com.google.gson.annotations.SerializedName;

public class TransactionStock {

    @SerializedName("transactionId")
    public int transactionId;

    @SerializedName("stock")
    public Stock stock;

    @SerializedName("amount")
    public int amount;

    @SerializedName("totalPrice")
    public float totalPrice;

    @SerializedName("date")
    public String date;

    @SerializedName("type")
    public String type;

}
