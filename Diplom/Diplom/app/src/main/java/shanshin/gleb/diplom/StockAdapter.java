package shanshin.gleb.diplom;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.ViewHolder> {
    ArrayList<Stock> stocks;
    LayoutInflater inflater;
    StockAdapter() {
    }
    StockAdapter(Context ctx, ArrayList<Stock> stocks) {
        this.inflater = LayoutInflater.from(ctx);
        this.stocks = stocks;
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
        viewHolder.name.setText(stock.name);
        viewHolder.count.setText(stock.count + " шт.");
        viewHolder.price.setText(stock.price + " руб.");
        viewHolder.delta.setTextColor(stock.delta < 0 ? inflater.getContext().getResources().getColor(R.color.errorColor) :
                inflater.getContext().getResources().getColor(R.color.colorPrimary));
        viewHolder.line.setBackground(stock.delta < 0 ? inflater.getContext().getResources().getDrawable(R.color.errorColor) :
                inflater.getContext().getResources().getDrawable(R.color.colorPrimary));
        viewHolder.delta.setText((stock.delta < 0 ? "↓" : "↑") + stock.delta + " руб(" + stock.delta / stock.price + "%)");
    }

    @Override
    public int getItemCount() {
        return stocks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name, count, price, delta;
        final View line;

        ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.stock_name);
            count = v.findViewById(R.id.stock_count);
            price = v.findViewById(R.id.stock_price);
            delta = v.findViewById(R.id.stock_delta);
            line = v.findViewById(R.id.line);
        }
    }

}





