package com.nfx.android.library.androidgraph;

import android.content.Context;
import android.graphics.Canvas;

/**
 * NFX Development
 * Created by nick on 27/10/15.
 *
 * Draws linear style grid lines in the x plane. Call doDraw with the canvas in which to draw on
 */
public class LinXGridLines extends LinGridLines {
    /**
     * Constructor
     */
    public LinXGridLines() {
        super(AxisOrientation.xAxis);
    }

    /**
     * Draws all lines which are viewable on screen
     *
     * @param canvas a canvas to draw onto
     */
    @Override
    public void doDraw(Canvas canvas) {
        super.doDraw(canvas);

        for (int i = 0; i < mNumberOfGridLines; ++i) {
            float xIntersect = intersectZoomCompensated(i) * getDrawableArea().getWidth();
            if(xIntersect >= 0 && xIntersect < getDimensionLength()) {
                canvas.drawLine(getDrawableArea().getLeft() + xIntersect, getDrawableArea()
                        .getTop(),
                        getDrawableArea().getLeft() + xIntersect, getDrawableArea().getBottom(),
                        mPaint);
            }
        }
    }

    @Override
    public void showAxisText(Context context, float minimumValue, float maximumValue) {
        super.showAxisText(context, minimumValue, maximumValue);
        if(mChildGridLineScale == GraphManager.Scale.logarithmic) {
            mAxisText = new LogXAxisText(context, this, minimumValue, maximumValue);
        } else {
            mAxisText = new XAxisText(context, this, minimumValue, maximumValue);
        }
    }

    /**
     * The surface size has changed update the current object to resize drawing
     *
     * @param drawableArea new surface size
     */
    public void surfaceChanged(DrawableArea drawableArea) {
        super.surfaceChanged(drawableArea);
        setGridLinesOffset(0);
    }

    @Override
    float getDimensionLength() {
        return getDrawableArea().getWidth();
    }
}