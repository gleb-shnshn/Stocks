package shanshin.gleb.diplom;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import shanshin.gleb.diplom.model.Stock;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.ViewHolder> {
    private List<Stock> stocks;
    private StockContatiner stockContatiner;
    private LayoutInflater inflater;
    private int downColor, upColor;
    private Drawable upDrawable, downDrawable;

    StockAdapter() {
        stocks = new ArrayList<>();
    }

    StockAdapter(Context ctx, List<Stock> stocks) {
        this.stockContatiner = (StockContatiner) ctx;
        this.inflater = LayoutInflater.from(ctx);
        this.stocks = stocks;
        this.stocks.addAll(stocks);
        this.stocks.addAll(stocks);

        this.upColor = inflater.getContext().getResources().getColor(R.color.colorPrimary);
        this.downColor = inflater.getContext().getResources().getColor(R.color.errorColor);

        this.upDrawable = inflater.getContext().getResources().getDrawable(R.color.colorPrimary);
        this.downDrawable = inflater.getContext().getResources().getDrawable(R.color.errorColor);

        notifyDataSetChanged();
    }

    @Override
    public StockAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final View view = this.inflater.inflate(R.layout.stock_item, viewGroup, false);
        return new StockAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StockAdapter.ViewHolder viewHolder, int i) {
        Stock stock = stocks.get(i);
        if (stock.name.length() > 10) {
            stock.name = stock.name.substring(0, 9) + "..";
        }
        viewHolder.stock = stock;
        viewHolder.name.setText(stock.name);
        viewHolder.count.setText(stock.count + " шт.");
        viewHolder.price.setText(stock.price + " руб.");
        viewHolder.delta.setTextColor(stock.priceDelta < 0 ? downColor : upColor);
        viewHolder.line.setBackground(stock.priceDelta < 0 ? downDrawable : upDrawable);
        viewHolder.delta.setText((stock.priceDelta < 0 ? "↓" : "↑") + stock.priceDelta + " руб(" + String.format("%.5f", stock.priceDelta / stock.price) + "%)");
    }

    public void setStocks(ArrayList<Stock> newStocks) {
        stocks = newStocks;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return stocks.size();
    }

    public List<Stock> getStocks() {
        return stocks;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name, count, price, delta;
        final View line;
        Stock stock;

        ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.stock_name);
            count = v.findViewById(R.id.stock_count);
            price = v.findViewById(R.id.stock_price);
            delta = v.findViewById(R.id.stock_delta);
            line = v.findViewById(R.id.line);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    stockContatiner.stockClicked(stock);
                }
            });
        }
    }

}





