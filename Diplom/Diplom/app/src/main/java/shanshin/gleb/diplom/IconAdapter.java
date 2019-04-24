package shanshin.gleb.diplom;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Handler;

import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYouListener;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class IconAdapter extends RecyclerView.Adapter<IconAdapter.ViewHolder> {
    List<String> iconUrls;
    AuthActivity context;
    CircleImageView currentChosen;

    public IconAdapter(List<String> iconUrls, AuthActivity context) {
        this.iconUrls = iconUrls;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.icon_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        String imageUrl = App.getInstance().getString(R.string.server_url) + App.getInstance().getString(R.string.icon_url) + iconUrls.get(position);
        holder.iconUrl = iconUrls.get(position);
        GlideToVectorYou
                .init()
                .with(context)
                .withListener(new GlideToVectorYouListener() {
                    @Override
                    public void onLoadFailed() {

                    }

                    @Override
                    public void onResourceReady() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (position == 0) {
                                    updateCurrentChosen(holder.imageView, holder.iconUrl);
                                }
                            }
                        }, 300);
                    }
                })
                .load(Uri.parse(imageUrl), holder.imageView);
    }

    @Override
    public int getItemCount() {
        return iconUrls.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imageView;
        String iconUrl;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.icon);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateCurrentChosen(imageView, iconUrl);
                }
            });
        }
    }

    public void updateCurrentChosen(CircleImageView imageView, String iconUrl) {
        if (currentChosen != null)
            currentChosen.setBorderWidth(0);
        imageView.setBorderWidth(5);
        currentChosen = imageView;
        context.setMainIcon(imageView.getDrawable(), iconUrl);
    }
}
