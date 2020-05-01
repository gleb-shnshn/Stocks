package shanshin.gleb.diplom.responses;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import shanshin.gleb.diplom.model.ChartData;

public class StockHistoryResponse {

    @SerializedName("id")
    public int id;

    @SerializedName("code")
    public String code;

    @SerializedName("name")
    public String name;

    @SerializedName("iconUrl")
    public String iconUrl;

    @SerializedName("from")
    public String from;

    @SerializedName("to")
    public String to;

    @SerializedName("history")
    public List<ChartData> history;

}