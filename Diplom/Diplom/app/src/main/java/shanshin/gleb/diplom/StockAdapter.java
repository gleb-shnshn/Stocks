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
        if (activityCode == null || activityCode == SearchActivity.SEARCH_STOCKS) {
            bindUsualViewHolder(viewHolder, i);
        } else {
            bindTransactionViewHolder(viewHolder, i);
        }
    }

    private void bindTransactionViewHolder(ViewHolder viewHolder, int i) {
        TransactionStock transactionStock = transactionStocks.get(i);
        bindGeneralViews(viewHolder, transactionStock.stock);
        viewHolder.line.setBackground(transactionStock.type.equals("sell") ? downDrawable : upDrawable);
        viewHolder.count.setText(transactionStock.stock.code + " • " + transactionStock.amount + getString(R.string.pcs));
        setPriceAndPriceEndValue(viewHolder, transactionStock.totalPrice);
        viewHolder.delta.setText(App.getInstance().getUtils().formatDate(transactionStock.date));
        viewHolder.delta.setTextColor(greyColor);
    }

    private void setPriceAndPriceEndValue(ViewHolder viewHolder, float value) {
        String priceFull = formatFloat(2, value);
        viewHolder.price.setText(priceFull.substring(0, priceFull.length() - 3));
        viewHolder.priceEnd.setText(priceFull.substring(priceFull.length() - 3) + getString(R.string.currency));
        viewHolder.price.forceLayout();
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

        setPriceAndPriceEndValue(viewHolder, stock.price);

        viewHolder.delta.setTextColor(stock.priceDelta < 0 ? downColor : upColor);
        viewHolder.line.setVisibility(View.GONE);

        String deltaPercents = formatFloat(4, stock.priceDelta / stock.price);
        String arrow = stock.priceDelta < 0 ? getString(R.string.arrowDown) : getString(R.string.arrowUp);
        viewHolder.delta.setText(arrow + stock.priceDelta + getString(R.string.currency) + String.format("(%s%%)", deltaPercents));
    }

    private String formatFloat(int length, float value) {
        return String.format(Locale.ENGLISH, "%." + length + "f", value);
    }

    private String getString(int resId) {
        return App.getInstance().getString(resId);
    }

    public void setStocks(ArrayList<Stock> newStocks) {
        stocks = newStocks;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (activityCode == null || activityCode == SearchActivity.SEARCH_STOCKS)
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
        final TextView name, count, price, priceEnd, delta;
        final View line;
        final ImageView icon;
        Stock stock;

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
