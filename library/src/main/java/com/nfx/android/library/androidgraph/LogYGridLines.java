package com.nfx.android.library.androidgraph;

import android.content.Context;
import android.graphics.Canvas;

/**
 * NFX Development
 * Created by nick on 27/10/15.
 *
 * Draws logarithmic style grid lines in the y plane. Call doDraw with the canvas in which to
 * draw on
 */
public class LogYGridLines extends LogGridLines {

    /**
     * Constructor
     */
    public LogYGridLines(float gridLineMinimumValue, float gridLineMaximumValue,
                         float axisValueSpan) {
        super(AxisOrientation.yAxis, gridLineMinimumValue, gridLineMaximumValue, axisValueSpan);
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
            float yIntersect = intersectZoomCompensated(i) * getDrawableArea().getHeight();
            if(yIntersect >= 0 && yIntersect < getDimensionLength()) {
                canvas.drawLine(getDrawableArea().getLeft(), getDrawableArea().getTop() +
                        yIntersect,
                        getDrawableArea().getRight(), getDrawableArea().getTop() + yIntersect,
                        mPaint);

            }
        }
    }

    @Override
    public void showAxisText(Context context, float minimumValue, float maximumValue) {
        super.showAxisText(context, minimumValue, maximumValue);
        mAxisText = new YAxisText(context, this, minimumValue, maximumValue);
    }

    /**
     * The surface size has changed update the current object to resize drawing
     *
     * @param drawableArea new surface size
     */
    public void surfaceChanged(DrawableArea drawableArea) {
        super.surfaceChanged(drawableArea);
    }

    @Override
    float getDimensionLength() {
        return getDrawableArea().getHeight();
    }
}
