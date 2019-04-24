package shanshin.gleb.diplom.responses;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import shanshin.gleb.diplom.model.Stock;

public class InfoResponse {

    @SerializedName("name")
    public String name;

    @SerializedName("surname")
    public String surname;

    @SerializedName("icon")
    public String icon;

    @SerializedName("balance")
    public float balance;

    @SerializedName("stocks")
    public List<Stock> stocks;

}
