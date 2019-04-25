package shanshin.gleb.diplom;

import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import shanshin.gleb.diplom.api.StocksApi;
import shanshin.gleb.diplom.handlers.GeneralUtils;
import shanshin.gleb.diplom.model.ChartData;
import shanshin.gleb.diplom.responses.StockHistoryResponse;

import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ChartActivity extends AppCompatActivity implements View.OnClickListener {
    private ProgressBar progressBar;
    private CandleStickChart chart;
    private List<ChartData> chartDataList = new ArrayList<>();
    private CandleDataSet dataSet;
    private int stockId;
    private String priceDelta, price, priceEnd;
    private boolean redOrGreen;
    private RadioButton currentChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getInstance().setCurrentActivity(this);
        setContentView(R.layout.activity_chart);

        initializeProperties();
        initializeOnClickListeners();
        initializeChartProperties();

        updateChart("total");
    }
    @Override
    protected void onStop() {
        App.getInstance().setCurrentActivity(null);
        super.onStop();
    }

    private void initializeChartProperties() {
        chart = findViewById(R.id.chart);
        chart.getLegend().setEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.setHighlightPerDragEnabled(false);
        chart.setBorderColor(getResources().getColor(R.color.light_grey));
        chart.requestDisallowInterceptTouchEvent(true);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
        xAxis.setTextSize(10f);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(2);
        xAxis.setTextColor(getResources().getColor(R.color.grey));
        xAxis.setCenterAxisLabels(true);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(true);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setValueFormatter(new ValueFormatter() {

            private final SimpleDateFormat FORMATTER = new SimpleDateFormat("dd MMM HH:mm", Locale.ENGLISH);

            @Override
            public String getFormattedValue(float value) {
                try {
                    return FORMATTER.format(GeneralUtils.DATE_FORMAT_PARSER.parse(chartDataList.get((int) value).date).getTime());
                } catch (Exception e) {
                    return "";
                }
            }
        });


        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setSpaceTop(10);
        leftAxis.setMaxWidth(50);
        leftAxis.setTextColor(getResources().getColor(R.color.grey));

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setSpaceTop(10);
        rightAxis.setMaxWidth(50);
        leftAxis.setTextColor(getResources().getColor(R.color.grey));

        dataSet = new CandleDataSet(new ArrayList<CandleEntry>(), "");
        dataSet.setColor(getResources().getColor(R.color.grey));
        dataSet.setAxisDependency(AxisDependency.LEFT);
        dataSet.setDrawValues(false);
        dataSet.setShadowColor(getResources().getColor(R.color.grey));
        dataSet.setShadowWidth(0.8f);
        dataSet.setDecreasingColor(getResources().getColor(R.color.errorColor));
        dataSet.setDecreasingPaintStyle(Paint.Style.FILL);
        dataSet.setIncreasingColor(getResources().getColor(R.color.colorPrimary));
        dataSet.setIncreasingPaintStyle(Paint.Style.FILL);
        dataSet.setNeutralColor(Color.LTGRAY);

    }

    private void initializeOnClickListeners() {
        findViewById(R.id.day).setOnClickListener(this);
        findViewById(R.id.week).setOnClickListener(this);
        findViewById(R.id.month).setOnClickListener(this);
        findViewById(R.id.six_months).setOnClickListener(this);
        findViewById(R.id.year).setOnClickListener(this);
        findViewById(R.id.total).setOnClickListener(this);

        progressBar = findViewById(R.id.progress);

        currentChecked = findViewById(R.id.total);
        currentChecked.setChecked(true);
    }

    private void setProgressEnabled(boolean enabled) {
        progressBar.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

    private void initializeProperties() {
        stockId = getIntent().getIntExtra("stockId", 0);
        priceDelta = getIntent().getStringExtra("priceDelta");
        price = getIntent().getStringExtra("price");
        priceEnd = getIntent().getStringExtra("priceEnd");
        redOrGreen = getIntent().getBooleanExtra("redOrGreen", false);
    }

    private void updateChart(final String range) {
        App.getInstance().getRetrofit().create(StocksApi.class).getStockHistory(stockId, range).enqueue(new Callback<StockHistoryResponse>() {
            @Override
            public void onResponse(Call<StockHistoryResponse> call, Response<StockHistoryResponse> response) {
                initializeStock(response);
                setProgressEnabled(false);
            }

            @Override
            public void onFailure(Call<StockHistoryResponse> call, Throwable t) {
                setProgressEnabled(false);
                App.getInstance().getErrorHandler().handleNoConnection();
            }
        });
    }

    private void initializeStock(Response<StockHistoryResponse> response) {
        View stock = findViewById(R.id.stock);
        TextView stockName = stock.findViewById(R.id.stock_name);
        TextView stockCode = stock.findViewById(R.id.stock_count);
        TextView stockPrice = stock.findViewById(R.id.stock_price);
        TextView stockPriceEnd = stock.findViewById(R.id.stock_price_end);
        TextView stockDelta = stock.findViewById(R.id.stock_delta);
        stock.findViewById(R.id.line).setVisibility(View.GONE);

        StockHistoryResponse stockResponse = response.body();

        if (stockResponse.name.length() > 10) {
            stockName.setText(stockResponse.name.substring(0, 9) + App.getInstance().getString(R.string.dots));
        } else {
            stockName.setText(stockResponse.name);
        }

        stockCode.setText(stockResponse.code);
        stockPrice.setText(price);
        stockPriceEnd.setText(priceEnd);
        stockDelta.setText(priceDelta);

        stockDelta.setTextColor(redOrGreen ? getResources().getColor(R.color.errorColor) :
                getResources().getColor(R.color.colorPrimary));

        Uri iconUri = Uri.parse(App.getInstance().getString(R.string.server_url) + stockResponse.iconUrl.substring(1));
        GlideToVectorYou
                .init()
                .with(this)
                .setPlaceHolder(R.drawable.white_circle, R.drawable.white_circle)
                .load(iconUri, (ImageView) findViewById(R.id.icon));
        setData(stockResponse.history);
    }

    private void setData(List<ChartData> chartDataList) {
        this.chartDataList = chartDataList;
        sortChartDataList(chartDataList);
        ArrayList<CandleEntry> values = new ArrayList<>();
        float minPrice = Float.MAX_VALUE, maxPrice = Float.MIN_VALUE;
        for (int i = 1; i < chartDataList.size(); i++) {
            ChartData chartData = chartDataList.get(i);
            if (chartData.price < minPrice)
                minPrice = chartData.price;
            if (chartData.price > maxPrice)
                maxPrice = chartData.price;
            values.add(new CandleEntry(i, chartDataList.get(i - 1).price * 1.01f, chartData.price * 0.99f, chartDataList.get(i - 1).price, chartData.price));
        }

        dataSet.setValues(values);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMinimum(minPrice * 0.95f);
        leftAxis.setAxisMaximum(maxPrice * 1.05f);

        CandleData data = new CandleData(dataSet);
        chart.setData(data);
        chart.invalidate();
    }

    private void sortChartDataList(List<ChartData> chartDataList) {
        Collections.sort(chartDataList, new Comparator<ChartData>() {
            @Override
            public int compare(ChartData data1, ChartData data2) {
                try {
                    long time1 = GeneralUtils.DATE_FORMAT_PARSER.parse(data1.date).getTime(), time2 = GeneralUtils.DATE_FORMAT_PARSER.parse(data2.date).getTime();
                    if (time1 > time2) {
                        return 1;
                    } else if (time1 == time2)
                        return 0;
                    return -1;
                } catch (ParseException e) {
                    return 0;
                }
            }
        });
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == currentChecked.getId())
            return;
        setProgressEnabled(true);
        currentChecked.setChecked(false);
        String range = "";
        switch (view.getId()) {
            case R.id.day:
                range = "day";
                break;
            case R.id.week:
                range = "week";
                break;
            case R.id.month:
                range = "month";
                break;
            case R.id.six_months:
                range = "6months";
                break;
            case R.id.year:
                range = "year";
                break;
            case R.id.total:
                range = "total";
                break;
        }
        currentChecked = findViewById(view.getId());
        updateChart(range);
    }
}
