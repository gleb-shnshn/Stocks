package shanshin.gleb.diplom.responses;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import shanshin.gleb.diplom.model.Stock;

public class InfoResponse {

    @SerializedName("name")
    public String name;

    @SerializedName("balance")
    public float balance;

    @SerializedName("stocks")
    public List<Stock> stocks;

}
