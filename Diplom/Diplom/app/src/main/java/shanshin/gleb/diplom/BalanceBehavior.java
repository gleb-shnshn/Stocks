package shanshin.gleb.diplom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class BalanceBehavior extends CoordinatorLayout.Behavior<TextView> {
    private boolean colored = false;
    // calculated from given layout
    private int startXPositionImage;
    private int startYPositionImage;
    private int startHeight;
    private int startToolbarHeight;

    private boolean initialised = false;

    private float amountOfToolbarToMove;
    private float amountOfImageToReduce;
    private float amountToMoveXPosition;
    private float amountToMoveYPosition;

    // user configured params
    private float finalToolbarHeight, finalXPosition, finalYPosition, finalHeight, finalWidth;

    public BalanceBehavior(
            final Context context,
            final AttributeSet attrs) {

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BalanceBehavior);
            finalXPosition = a.getDimension(R.styleable.BalanceBehavior_finalXPosition, 0);
            finalYPosition = a.getDimension(R.styleable.BalanceBehavior_finalYPosition, 0);
            finalHeight = a.getDimension(R.styleable.BalanceBehavior_finalHeight, 0);
            finalWidth = a.getDimension(R.styleable.BalanceBehavior_finalWidth, 0);
            finalToolbarHeight = a.getDimension(R.styleable.BalanceBehavior_finalToolbarHeight, 0);
            a.recycle();
        }
    }

    @Override
    public boolean layoutDependsOn(
            final CoordinatorLayout parent,
            final TextView child,
            final View dependency) {

        return dependency instanceof AppBarLayout; // change if you want another sibling to depend on
    }

    @Override
    public boolean onDependentViewChanged(
            final CoordinatorLayout parent,
            final TextView child,
            final View dependency) {
        // make child (avatar) change in relation to dependency (toolbar) in both size and position, init with properties from layout
        initProperties(child, dependency);

        // calculate progress of movement of dependency
        float currentToolbarHeight = startToolbarHeight + dependency.getY(); // current expanded height of toolbar
        // don't go below configured min height for calculations (it does go passed the toolbar)
        currentToolbarHeight = currentToolbarHeight < finalToolbarHeight ? finalToolbarHeight : currentToolbarHeight;
        final float amountAlreadyMoved = startToolbarHeight - currentToolbarHeight;
        final float progress = 100 * amountAlreadyMoved / amountOfToolbarToMove; // how much % of expand we reached
        if (progress > 90 && !colored) {
            ((StockCaseActivity) parent.getContext()).toolbar.setBackground(parent.getContext().getDrawable(R.color.grey));
            colored = true;
        } else if (progress < 90 && colored) {
            ((StockCaseActivity) parent.getContext()).toolbar.setBackground(parent.getContext().getDrawable(R.color.transparent));
            colored = false;
        }

        // update image position
        final float distanceXToSubtract = progress * amountToMoveXPosition / 100;
        final float distanceYToSubtract = progress * amountToMoveYPosition / 100;
        float newXPosition = startXPositionImage - distanceXToSubtract;
        //newXPosition = newXPosition < endXPosition ? endXPosition : newXPosition; // don't go passed end position
        child.setX(newXPosition);
        child.setY(startYPositionImage - distanceYToSubtract);

        return true;
    }

    private void initProperties(
            final TextView child,
            final View dependency) {
        finalXPosition = child.getRootView().getWidth() - finalWidth * 2.1f / 3;
        if (!initialised) {
            // form initial layout
            startHeight = child.getHeight();
            startXPositionImage = (int) child.getX();
            startYPositionImage = (int) child.getY();
            startToolbarHeight = dependency.getHeight();
            // some calculated fields
            amountOfToolbarToMove = startToolbarHeight - finalToolbarHeight;
            amountOfImageToReduce = startHeight - finalHeight;
            amountToMoveXPosition = startXPositionImage - finalXPosition;
            amountToMoveYPosition = startYPositionImage - finalYPosition;
            initialised = true;
        }
    }

}