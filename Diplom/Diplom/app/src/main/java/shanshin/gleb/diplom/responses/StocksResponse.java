package shanshin.gleb.diplom.responses;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import shanshin.gleb.diplom.model.Stock;

public class StocksResponse {

    @SerializedName("nextItemId")
    public int nextItemId;

    @SerializedName("prevItemId")
    public int prevItemId;

    @SerializedName("items")
    public ArrayList<Stock> items;

}
