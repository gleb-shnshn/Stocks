package shanshin.gleb.diplom;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.paging.PagedListAdapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;

import java.util.ArrayList;

import shanshin.gleb.diplom.model.UniversalStock;

public class StockAdapter extends PagedListAdapter<UniversalStock, StockAdapter.ViewHolder> {
    private StockContainer stockContainer;
    private LayoutInflater inflater;
    private ArrayList<UniversalStock> stocks = new ArrayList<>();
    private int downColor, upColor, greyColor;
    private Drawable upDrawable, downDrawable;
    private Integer activityCode = null;

    StockAdapter() {
        super(DIFF_CALLBACK);
    }

    StockAdapter(Context ctx, ArrayList<UniversalStock> stocks, Integer activityCode) {
        super(DIFF_CALLBACK);
        this.stocks = stocks;
        this.stockContainer = (StockContainer) ctx;
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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final View view = this.inflater.inflate(R.layout.stock_item, viewGroup, false);
        return new StockAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        UniversalStock stock = activityCode == null ? stocks.get(i) : getItem(i);
        viewHolder.stock = stock;
        Uri iconUri = Uri.parse(App.getInstance().getString(R.string.server_url) + stock.iconUrl);
        GlideToVectorYou
                .init()
                .with((AppCompatActivity) inflater.getContext())
                .setPlaceHolder(R.drawable.white_circle, R.drawable.white_circle)
                .load(iconUri, viewHolder.icon);

        if (stock.nameField.length() > 10) {
            viewHolder.name.setText(stock.nameField.substring(0, 9) + "..");
        } else {
            viewHolder.name.setText(stock.nameField);
        }

        viewHolder.count.setText(stock.countField);

        viewHolder.price.setText(stock.priceField);
        viewHolder.priceEnd.setText(stock.priceEndField);

        viewHolder.delta.setText(stock.deltaField);
        if (activityCode != null && activityCode == SearchActivity.TRANSACTION_HISTORY) {
            viewHolder.line.setBackground(stock.redOrGreen ? downDrawable : upDrawable);
            viewHolder.delta.setTextColor(greyColor);
        } else {
            viewHolder.line.setVisibility(View.GONE);
            viewHolder.delta.setTextColor(stock.redOrGreen ? downColor : upColor);
        }


    }

    @Override
    public int getItemCount() {
        return activityCode == null ? stocks.size() : super.getItemCount();
    }

    public void setStocks(ArrayList<UniversalStock> newStocks) {
        if (newStocks != null) {
            this.stocks = newStocks;
            notifyDataSetChanged();
        }
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
                    stockContainer.onStockClick(stock);
                }
            });
            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    stockContainer.onStockLongClick(stock);
                    return true;
                }
            });

        }
    }

    private final static DiffUtil.ItemCallback<UniversalStock> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<UniversalStock>() {
                @Override
                public boolean areItemsTheSame(UniversalStock oldStock, UniversalStock newStock) {
                    return oldStock.id == newStock.id;
                }

                @Override
                public boolean areContentsTheSame(UniversalStock oldStock, UniversalStock newStock) {
                    return oldStock.equals(newStock);
                }
            };

}
