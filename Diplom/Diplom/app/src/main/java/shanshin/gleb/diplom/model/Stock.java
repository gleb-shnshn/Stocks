package shanshin.gleb.diplom.model;

import com.google.gson.annotations.SerializedName;

public class Stock {

    @SerializedName("id")
    public int id;

    @SerializedName("code")
    public String code;

    @SerializedName("name")
    public String name;

    @SerializedName("iconUrl")
    public String iconUrl;

    @SerializedName("price")
    public float price;

    @SerializedName("priceDelta")
    public float priceDelta;

    @SerializedName("count")
    public int count;

}
