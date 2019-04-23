package shanshin.gleb.diplom;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LoadingButton extends FrameLayout {

    private ProgressBar mProgressBar;
    private TextView mTextView;
    private String mText = "";
    private final int DEFAULT_PROGRESS_MARGIN = 8;

    private void init() {
        setClickable(true);
        setElevation(10f);

        LayoutParams progressBarParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        progressBarParams.setMargins(
                DEFAULT_PROGRESS_MARGIN,
                DEFAULT_PROGRESS_MARGIN,
                DEFAULT_PROGRESS_MARGIN,
                DEFAULT_PROGRESS_MARGIN);
        mProgressBar = new ProgressBar(getContext());
        mProgressBar.setLayoutParams(progressBarParams);
        mProgressBar.setVisibility(View.INVISIBLE);

        LayoutParams textViewParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        mTextView = new TextView(getContext());
        mTextView.setLayoutParams(textViewParams);
        mTextView.setText(mText);
        mTextView.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        mTextView.setTypeface(Typeface.DEFAULT_BOLD);
        mTextView.setTextColor(getResources().getColor(R.color.white));

        addView(mProgressBar);
        addView(mTextView);
    }

    public void setText(String text) {
        this.mText = text;
        mTextView.setText(text);

    }

    public void startLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
        mTextView.setVisibility(View.INVISIBLE);
    }

    public void stopLoading() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mTextView.setVisibility(View.VISIBLE);
    }

    public LoadingButton(Context context) {
        super(context);
        init();
    }

    public LoadingButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadingButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
}