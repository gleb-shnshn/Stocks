package shanshin.gleb.diplom;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import shanshin.gleb.diplom.api.StocksApi;
import shanshin.gleb.diplom.model.ChartData;
import shanshin.gleb.diplom.responses.StockHistoryResponse;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChartActivity extends AppCompatActivity implements View.OnClickListener {

    private LineChart chart;
    private int stockId;
    private String priceDelta, price, priceEnd;
    private boolean redOrGreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        initializeProperties();
        initializeOnClickListeners();

        updateChart("day");

        chart = findViewById(R.id.chart);
        // no description text
        chart.getDescription().setEnabled(false);

        // enable touch gestures
        chart.setTouchEnabled(true);

        chart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);
        chart.setHighlightPerDragEnabled(true);

        // set an alternative background color
        chart.setBackgroundColor(getResources().getColor(R.color.light_grey));
        chart.setViewPortOffsets(0f, 0f, 0f, 0f);

        Legend l = chart.getLegend();
        l.setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(getResources().getColor(R.color.grey));
        xAxis.setCenterAxisLabels(true);
        xAxis.setValueFormatter(new ValueFormatter() {

            private final SimpleDateFormat mFormat = new SimpleDateFormat("dd MMM HH:mm", Locale.ENGLISH);

            @Override
            public String getFormattedValue(float value) {
                return mFormat.format(new Date((long) value));
            }
        });

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        leftAxis.setTextColor(getResources().getColor(R.color.grey));
        leftAxis.setDrawGridLines(false);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setYOffset(-9f);
        leftAxis.setTextColor(getResources().getColor(R.color.grey));

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    private void initializeOnClickListeners() {
        findViewById(R.id.day).setOnClickListener(this);
        findViewById(R.id.week).setOnClickListener(this);
        findViewById(R.id.month).setOnClickListener(this);
        findViewById(R.id.six_months).setOnClickListener(this);
        findViewById(R.id.year).setOnClickListener(this);
        findViewById(R.id.total).setOnClickListener(this);
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
                try {
                    inititalizeStock(response);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<StockHistoryResponse> call, Throwable t) {
                App.getInstance().getUtils().showError(getString(R.string.no_connection));
            }
        });
    }

    private void inititalizeStock(Response<StockHistoryResponse> response) throws ParseException {
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

        Glide
                .with(this)
                .load(App.getInstance().getString(R.string.server_url) + stockResponse.iconUrl.substring(1))
                .centerCrop()
                .placeholder(R.drawable.white_circle)
                .into((ImageView) findViewById(R.id.icon));

        setData(stockResponse.history);
    }

    private void setData(List<ChartData> chartDataList) throws ParseException {

        ArrayList<Entry> values = new ArrayList<>();
        DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        float minPrice = Float.MAX_VALUE, maxPrice = Float.MIN_VALUE;
        for (ChartData chartData : chartDataList) {
            if (chartData.price < minPrice)
                minPrice = chartData.price;
            if (chartData.price > maxPrice)
                maxPrice = chartData.price;
            values.add(new Entry(df.parse(chartData.date).getTime(), chartData.price));
        }
        Collections.sort(values, new Comparator<Entry>() {
            @Override
            public int compare(Entry entry, Entry entry2) {
                if (entry.getX() > entry2.getX()) {
                    return 1;
                } else if (entry.getX() == entry2.getX())
                    return 0;
                return -1;
            }
        });
        LineDataSet set1 = new LineDataSet(values, "DataSet 1");
        set1.setAxisDependency(AxisDependency.LEFT);
        set1.setColor(getResources().getColor(R.color.grey));
        set1.setValueTextColor(getResources().getColor(R.color.grey));
        set1.setLineWidth(3f);
        set1.setDrawCircles(true);
        set1.setCircleColor(getResources().getColor(R.color.grey));
        set1.setDrawValues(false);
        set1.setFillAlpha(65);
        set1.setDrawCircleHole(false);

        LineData data = new LineData(set1);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(9f);

        chart.setData(data);
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMinimum(minPrice * 0.95f);
        leftAxis.setAxisMaximum(maxPrice * 1.05f);
        chart.invalidate();
    }


    @Override
    public void onClick(View view) {
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
        Log.d("tagged", range);
        updateChart(range);
    }
}
