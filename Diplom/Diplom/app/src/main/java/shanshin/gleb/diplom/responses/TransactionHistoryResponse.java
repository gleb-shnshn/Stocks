package shanshin.gleb.diplom.responses;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import shanshin.gleb.diplom.model.TransactionStock;

public class TransactionHistoryResponse {
    @SerializedName("nextItemId")
    public int nextItemId;

    @SerializedName("prevItemId")
    public int prevItemId;

    @SerializedName("items")
    public ArrayList<TransactionStock> items;
}
