package com.nfx.android.graph.androidgraph;

import android.content.Context;
import android.graphics.Canvas;

import com.nfx.android.graph.androidgraph.AxisScale.AxisParameters;

/**
 * NFX Development
 * Created by nick on 27/10/15.
 *
 * Draws linear style grid lines in the x plane. Call doDraw with the canvas in which to draw on
 */
class LinXGridLines extends LinGridLines {
    /**
     * Constructor
     *
     * @param axisParameters        parameters of graph shown
     */
    LinXGridLines(AxisParameters axisParameters) {
        super(AxisOrientation.xAxis, axisParameters);
    }

    /**
     * Draws all lines which are viewable on screen
     *
     * @param canvas a canvas to draw onto
     */
    @Override
    public void doDraw(Canvas canvas) {
        super.doDraw(canvas);

        for(int i = 0; i < numberOfGridLines; ++i) {
            float xIntersect = intersectZoomCompensated(i) * getDrawableArea().getWidth();
            if(xIntersect >= 0 && xIntersect < getDimensionLength()) {
                canvas.drawLine(getDrawableArea().getLeft() + xIntersect, getDrawableArea()
                        .getTop(),
                        getDrawableArea().getLeft() + xIntersect, getDrawableArea().getBottom(),
                        paint);
            }
        }
    }

    @Override
    public void showAxisText(Context context) {
        super.showAxisText(context);
        axisText = new XAxisText(context, this, axisParameters);
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
