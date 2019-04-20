package shanshin.gleb.diplom;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import shanshin.gleb.diplom.model.Stock;
import shanshin.gleb.diplom.model.TransactionStock;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.ViewHolder> {
    private List<Stock> stocks;
    private List<TransactionStock> transactionStocks;
    private StockContatiner stockContatiner;
    private LayoutInflater inflater;
    private int downColor, upColor, greyColor;
    private Drawable upDrawable, downDrawable;
    private Integer activityCode = null;
    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.ENGLISH);
    private DateFormat dfNew = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

    StockAdapter() {
        stocks = new ArrayList<>();
    }

    public void initData(Context ctx, Integer activityCode) {
        this.stockContatiner = (StockContatiner) ctx;
        this.inflater = LayoutInflater.from(ctx);

        this.upColor = inflater.getContext().getResources().getColor(R.color.colorPrimary);
        this.downColor = inflater.getContext().getResources().getColor(R.color.errorColor);

        this.greyColor = inflater.getContext().getResources().getColor(R.color.grey);

        this.upDrawable = inflater.getContext().getResources().getDrawable(R.color.colorPrimary);
        this.downDrawable = inflater.getContext().getResources().getDrawable(R.color.errorColor);

        this.activityCode = activityCode;

    }

    StockAdapter(Context ctx, List<Stock> stocks, ArrayList<TransactionStock> transactionStocks, Integer activityCode) {
        this.stocks = stocks;
        this.transactionStocks = transactionStocks;
        initData(ctx, activityCode);
        notifyDataSetChanged();
    }

    @Override
    public StockAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final View view = this.inflater.inflate(R.layout.stock_item, viewGroup, false);
        return new StockAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        if (activityCode == null || activityCode == StockSearchActivity.SEARCH_STOCKS) {
            bindUsualViewHolder(viewHolder, i);
        } else {
            bindTransactionViewHolder(viewHolder, i);

        }
    }

    private void bindTransactionViewHolder(ViewHolder viewHolder, int i) {
        TransactionStock transactionStock = transactionStocks.get(i);
        bindGeneralViews(viewHolder, transactionStock.stock);
        viewHolder.line.setVisibility(View.GONE);
        viewHolder.plusOrMinus.setText(transactionStock.type.equals("sell") ? "−" : "+");
        viewHolder.plusOrMinus.setTextColor(transactionStock.type.equals("sell") ? downColor : upColor);
        viewHolder.count.setText(transactionStock.stock.code + " • " + transactionStock.amount + " шт.");
        viewHolder.price.setText(String.format("%.2f", transactionStock.totalPrice) + " руб.");
        viewHolder.delta.setText(formatDate(transactionStock.date));
        viewHolder.delta.setTextColor(greyColor);
    }

    private String formatDate(String date) {
        if (date.contains("T")) {
            try {
                return dfNew.format(df.parse(date));
            } catch (ParseException e) {
                Log.d("tagged", e.toString());
            }
            return "";
        } else {
            return date;
        }

    }

    private void bindGeneralViews(ViewHolder viewHolder, Stock stock) {
        if (stock.name.length() > 10) {
            viewHolder.name.setText(stock.name.substring(0, 9) + "..");
        } else {
            viewHolder.name.setText(stock.name);
        }
        Glide
                .with(inflater.getContext())
                .load(App.getInstance().getString(R.string.server_url) + stock.iconUrl.substring(1))
                .centerCrop()
                .placeholder(R.drawable.white_circle)
                .into(viewHolder.icon);
    }

    private void bindUsualViewHolder(ViewHolder viewHolder, int i) {
        Stock stock = stocks.get(i);
        bindGeneralViews(viewHolder, stock);
        viewHolder.stock = stock;
        if (activityCode == null)
            viewHolder.count.setText(stock.count + " шт.");
        else
            viewHolder.count.setText(stock.code);
        viewHolder.price.setText(String.format("%.2f", stock.price) + " руб.");
        viewHolder.delta.setTextColor(stock.priceDelta < 0 ? downColor : upColor);
        viewHolder.plusOrMinus.setVisibility(View.GONE);
        viewHolder.line.setBackground(stock.priceDelta < 0 ? downDrawable : upDrawable);
        viewHolder.delta.setText((stock.priceDelta < 0 ? "↓" : "↑") + stock.priceDelta + " руб(" + String.format("%.4f", stock.priceDelta / stock.price) + "%)");
    }


    public void setStocks(ArrayList<Stock> newStocks) {
        stocks = newStocks;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (activityCode == null || activityCode == StockSearchActivity.SEARCH_STOCKS)
            return stocks.size();
        else
            return transactionStocks.size();
    }

    public List<Stock> getStocks() {
        return stocks;
    }

    public List<TransactionStock> getHistoryStocks() {
        return transactionStocks;
    }

    public void setHistoryStocks(ArrayList<TransactionStock> items) {
        transactionStocks = items;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name, count, price, delta, plusOrMinus;
        final View line;
        final ImageView icon;
        Stock stock;

        ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.stock_name);
            count = v.findViewById(R.id.stock_count);
            price = v.findViewById(R.id.stock_price);
            delta = v.findViewById(R.id.stock_delta);
            plusOrMinus = v.findViewById(R.id.plusOrMinus);
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
