package shanshin.gleb.diplom;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import shanshin.gleb.diplom.model.UniversalStock;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.ViewHolder> {
    private List<UniversalStock> stocks;
    private StockContatiner stockContatiner;
    private LayoutInflater inflater;
    private int downColor, upColor, greyColor;
    private Drawable upDrawable, downDrawable;
    private Integer activityCode = null;

    StockAdapter() {
        stocks = new ArrayList<>();
    }

    StockAdapter(Context ctx, ArrayList<UniversalStock> stocks, Integer activityCode) {
        this.stocks = stocks;

        this.stockContatiner = (StockContatiner) ctx;
        this.inflater = LayoutInflater.from(ctx);

        this.upColor = inflater.getContext().getResources().getColor(R.color.colorPrimary);
        this.downColor = inflater.getContext().getResources().getColor(R.color.errorColor);
        this.greyColor = inflater.getContext().getResources().getColor(R.color.grey);

        this.upDrawable = inflater.getContext().getResources().getDrawable(R.color.colorPrimary);
        this.downDrawable = inflater.getContext().getResources().getDrawable(R.color.errorColor);

        this.activityCode = activityCode;

        notifyDataSetChanged();
    }

    @Override
    public StockAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final View view = this.inflater.inflate(R.layout.stock_item, viewGroup, false);
        return new StockAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        UniversalStock stock = stocks.get(i);
        viewHolder.stock = stock;

        Glide
                .with(inflater.getContext())
                .load(App.getInstance().getString(R.string.server_url) + stock.iconUrl.substring(1))
                .centerCrop()
                .placeholder(R.drawable.white_circle)
                .into(viewHolder.icon);

        if (stock.nameField.length() > 10) {
            viewHolder.name.setText(stock.nameField.substring(0, 9) + "..");
        } else {
            viewHolder.name.setText(stock.nameField);
        }

        viewHolder.count.setText(stock.countField);

        viewHolder.price.setText(stock.priceField);
        viewHolder.priceEnd.setText(stock.priceEndField);
        viewHolder.price.forceLayout();

        viewHolder.delta.setText(stock.deltaField);
        if (activityCode != null && activityCode == SearchActivity.TRANSACTION_HISTORY) {
            viewHolder.line.setBackground(stock.redOrGreen ? downDrawable : upDrawable);
            viewHolder.delta.setTextColor(greyColor);
        } else {
            viewHolder.line.setVisibility(View.GONE);
            viewHolder.delta.setTextColor(stock.redOrGreen ? downColor : upColor);
        }


    }

    public void setStocks(ArrayList<UniversalStock> newStocks) {
        stocks = newStocks;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return stocks.size();
    }

    public List<UniversalStock> getStocks() {
        return stocks;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name, count, price, priceEnd, delta;
        final View line;
        final ImageView icon;
        UniversalStock stock;

        ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.stock_name);
            count = v.findViewById(R.id.stock_count);
            price = v.findViewById(R.id.stock_price);
            priceEnd = v.findViewById(R.id.stock_price_end);
            delta = v.findViewById(R.id.stock_delta);
            line = v.findViewById(R.id.line);
            icon = v.findViewById(R.id.icon);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    stockContatiner.stockClicked(stock);
                }
            });
        }
    }

}
