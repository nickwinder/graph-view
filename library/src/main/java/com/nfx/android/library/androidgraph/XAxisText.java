package com.nfx.android.library.androidgraph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * NFX Development
 * Created by nick on 29/10/15.
 *
 * Used to draw text onto a canvas to display the values of grid lines in the Y axis
 */
public class XAxisText extends AxisText {
    /**
     * Constructor
     *
     * @param context   application context is used for dimension reasons
     * @param gridLines grid lines axis is related to
     */
    XAxisText(Context context, GridLines gridLines) {
        super(context, gridLines);
    }

    /**
     * draw the axis text on to canvas
     *
     * @param canvas a canvas to draw onto
     */
    @Override
    public void doDraw(Canvas canvas) {
        for (int i = 0; i < mGridLines.getNumberOfGridLines(); ++i) {
            Rect bounds = new Rect();
            String textString = String.valueOf(i);
            mTextPaint.getTextBounds(textString, 0, textString.length(), bounds);
            int x = getDrawableArea().getLeft() + (int) mGridLines.intersect(i);

            // Remember the text is drawn on the baseline
            canvas.drawText(textString, x, getDrawableArea().getTop() +
                    (int) Math.abs(mTextPaint.ascent()), mTextPaint);
        }
    }

    /**
     * The surface size has changed update the current object to resize drawing
     * This will align the xAxis to the bottom for now
     *
     * @param drawableArea new surface size
     */
    public void surfaceChanged(DrawableArea drawableArea) {
        String textString = "0";
        Rect bounds = new Rect();
        mTextPaint.getTextBounds(textString, 0, textString.length(), bounds);
        getDrawableArea().setDrawableArea(drawableArea.getLeft(),
                drawableArea.getHeight() - (int) getRealTextHeight(),
                drawableArea.getWidth(), (int) getRealTextHeight());
        calculateRemainingDrawableArea(drawableArea);
    }

    /**
     * This will change the drawable area passed in to reflect the new drawable area after the
     * Axis object is finished with it
     *
     * @param currentDrawableArea will reflect the new drawable area pass in current drawableArea
     */
    @Override
    public void calculateRemainingDrawableArea(DrawableArea currentDrawableArea) {
        int xOffset = currentDrawableArea.getLeft();
        int yOffset = currentDrawableArea.getTop();
        int width = currentDrawableArea.getWidth();
        int height = currentDrawableArea.getHeight();

        // If it is equal to zero we assume it is top aligned
        if (getDrawableArea().getTop() == 0) {
            yOffset += getDrawableArea().getHeight();
        }

        height -= getDrawableArea().getHeight();

        currentDrawableArea.setDrawableArea(xOffset, yOffset, width, height);
    }
}