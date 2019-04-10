package shanshin.gleb.diplom;

import com.google.gson.annotations.SerializedName;

import java.util.List;

class InfoResponse {

    @SerializedName("name")
    String name;

    @SerializedName("balance")
    float balance;

    @SerializedName("stocks")
    List<Stock> stocks;

}
