package shanshin.gleb.diplom.model;

import com.google.gson.annotations.SerializedName;

public class ChartData {
    @SerializedName("date")
    public String date;

    @SerializedName("price")
    public float price;
}
