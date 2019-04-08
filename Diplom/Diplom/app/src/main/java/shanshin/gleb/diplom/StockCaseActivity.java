package shanshin.gleb.diplom;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ethanhua.skeleton.RecyclerViewSkeletonScreen;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.ViewSkeletonScreen;

import java.util.ArrayList;


public class StockCaseActivity extends AppCompatActivity {
    RecyclerView stocksView;
    RecyclerViewSkeletonScreen skeletonStocks;
    ViewSkeletonScreen skeletonHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_case);
        stocksView = findViewById(R.id.stocksView);
        CardView cardView = findViewById(R.id.card);
        setSkeletonLoading(stocksView, cardView);

        stocksView.postDelayed(new Runnable() {
            @Override
            public void run() {
                cancelSkeletonLoading();
                final ArrayList<Stock> stocks = new ArrayList<>();
                stocks.add(new Stock("RedFox", 24, 4.61f, 0.01f));
                stocks.add(new Stock("RedFox", 24, 4.61f, -0.01f));
                stocks.add(new Stock("RedFox", 24, 4.61f, -0.01f));
                stocks.add(new Stock("RedFox", 24, 4.61f, 0.01f));
                stocks.add(new Stock("RedFox", 24, 4.61f, 0.01f));
                stocks.add(new Stock("RedFox", 24, 4.61f, -0.01f));
                stocks.add(new Stock("RedFox", 24, 4.61f, 0.01f));
                stocks.add(new Stock("RedFox", 24, 4.61f, -0.01f));
                stocksView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                stocksView.setAdapter(new StockAdapter(getApplicationContext(), stocks));
            }
        }, 3000);

    }

    private void cancelSkeletonLoading() {
        skeletonHeader.hide();
        skeletonStocks.hide();
    }

    public void setSkeletonLoading(RecyclerView stockList, CardView header) {
        stocksView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        skeletonHeader = Skeleton.bind(header)
                .load(R.layout.header_skeleton)
                .duration(1200)
                .show();
        skeletonStocks = Skeleton.bind(stockList)
                .load(R.layout.stock_skeleton_item)
                .duration(1200)
                .adapter(new StockAdapter())
                .show();
    }
}