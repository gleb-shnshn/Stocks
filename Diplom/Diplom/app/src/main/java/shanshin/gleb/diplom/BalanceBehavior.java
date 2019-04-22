package shanshin.gleb.diplom;

import android.content.Context;
import android.content.res.TypedArray;

import com.google.android.material.appbar.AppBarLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class BalanceBehavior extends CoordinatorLayout.Behavior<TextView> {
    private boolean colored = false;

    private int startXPosition;
    private int startYPosition;

    private Toolbar toolbar;
    private Drawable transparent, grey;

    private int startToolbarHeight;

    private boolean initialised = false;

    private float amountOfToolbarToMove;
    private float amountToMoveXPosition;
    private float amountToMoveYPosition;

    private float finalToolbarHeight, finalXPosition, finalYPosition, finalWidth;

    public BalanceBehavior(
            final Context context,
            final AttributeSet attrs) {

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BalanceBehavior);
            finalXPosition = a.getDimension(R.styleable.BalanceBehavior_finalXPosition, 0);
            finalYPosition = a.getDimension(R.styleable.BalanceBehavior_finalYPosition, 0);
            finalWidth = a.getDimension(R.styleable.BalanceBehavior_finalWidth, 0);
            finalToolbarHeight = a.getDimension(R.styleable.BalanceBehavior_finalToolbarHeight, 0);
            grey = App.getInstance().getDrawable(R.color.grey);
            transparent = App.getInstance().getDrawable(R.color.transparent);
            a.recycle();
        }
    }

    @Override
    public boolean layoutDependsOn(
            final CoordinatorLayout parent,
            final TextView child,
            final View dependency) {
        return dependency instanceof AppBarLayout;
    }


    @Override
    public boolean onDependentViewChanged(
            final CoordinatorLayout parent,
            final TextView child,
            final View dependency) {
        if (child.getWidth() == 0)
            return true;
        initProperties(child, dependency);

        float currentToolbarHeight = startToolbarHeight + dependency.getY();

        currentToolbarHeight = currentToolbarHeight < finalToolbarHeight ? finalToolbarHeight : currentToolbarHeight;
        final float amountAlreadyMoved = startToolbarHeight - currentToolbarHeight;
        final float progress = 100 * amountAlreadyMoved / amountOfToolbarToMove;
        if (progress > 90 && !colored) {
            toolbar.setBackground(grey);
            colored = true;
        } else if (progress < 90 && colored) {
            toolbar.setBackground(transparent);
            colored = false;
        }

        final float distanceXToSubtract = progress * amountToMoveXPosition / 100;
        final float distanceYToSubtract = progress * amountToMoveYPosition / 100;
        float newXPosition = startXPosition - distanceXToSubtract;
        child.setX(newXPosition);
        child.setY(startYPosition - distanceYToSubtract);

        return true;
    }

    private void initProperties(
            final TextView child,
            final View dependency) {
        if (!initialised) {
            toolbar = ((StockCaseActivity) child.getContext()).getToolbar();
            startXPosition = (int) child.getX();
            startYPosition = (int) child.getY();
            startToolbarHeight = dependency.getHeight();

            initChildProperties(child);
            initialised = true;
        }
    }

    private void initChildProperties(TextView child) {
        finalWidth = child.getMeasuredWidth();
        finalXPosition = child.getRootView().getWidth() - finalWidth;
        amountOfToolbarToMove = startToolbarHeight - finalToolbarHeight;
        amountToMoveXPosition = startXPosition - finalXPosition;
        amountToMoveYPosition = startYPosition - finalYPosition;
    }

}