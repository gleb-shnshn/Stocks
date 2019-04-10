package shanshin.gleb.diplom;

import com.google.gson.annotations.SerializedName;

public class Stock {

    @SerializedName("id")
    int id;

    @SerializedName("code")
    String code;

    @SerializedName("name")
    String name;

    @SerializedName("iconUrl")
    String iconUrl;

    @SerializedName("price")
    float price;

    @SerializedName("priceDelta")
    float priceDelta;

    @SerializedName("count")
    int count;

}
