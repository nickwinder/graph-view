package com.nfx.android.library.androidgraph;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * NFX Development
 * Created by nick on 27/10/15.
 *
 * Draws linear style grid lines in the y plane. Call doDraw with the canvas in which to draw on
 */
public class LinYGridLines extends LinGridLines {
    /**
     * Constructor
     */
    public LinYGridLines() {
        super(AxisOrientation.yAxis);
    }

    /**
     * Draws all lines which are viewable on screen
     *
     * @param canvas a canvas to draw onto
     */
    @Override
    public void doDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(mGridColor);
        paint.setStrokeWidth(mGridStrokeWidth);

        for (int i = 0; i < mNumberOfGridLines; ++i) {
            float yIntersect = intersect(i);
            canvas.drawLine(getDrawableArea().getLeft(), getDrawableArea().getTop() + yIntersect,
                    getDrawableArea().getRight(), getDrawableArea().getTop() + yIntersect, paint);
        }
    }

    /**
     * calls the dimension specific intersect workout
     * @param gridLine grid line to find out the intersecting value
     * @return value where line intersects
     */
    @Override
    public float intersect(int gridLine) {
        return intersect(gridLine, getDrawableArea().getHeight());
    }
}
