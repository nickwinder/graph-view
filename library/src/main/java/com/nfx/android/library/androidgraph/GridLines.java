package com.nfx.android.library.androidgraph;

import android.graphics.Color;

/**
 * NFX Development
 * Created by nick on 27/10/15.
 *
 * Extend from this class to create a drawable line with view zooming capabilities
 */
public abstract class GridLines extends DrawableObject {

    /**
     * Number of grid lines to display in the area
     */
    protected int mNumberOfGridLines = 2;
    /**
     * Color of the grid lines
     */
    protected int mGridColor = Color.GRAY;
    /**
     * Use an even number to ensure all grid line strokes look the same
     */
    protected float mGridStrokeWidth = 4f;
    /**
     * Describes the viewable part of the grid
     */
    protected ZoomDisplay mZoomDisplay;
    /**
     * This allows us to know the axis at runtime
     */
    private AxisOrientation mAxisOrientation;

    /**
     * Constructor of GridLines
     *
     * @param axisOrientation either the x or y axis
     */
    public GridLines(AxisOrientation axisOrientation) {
        mAxisOrientation = axisOrientation;
        // Set a default zoom Display
        mZoomDisplay = new ZoomDisplay(1f, 0f);
    }

    /**
     * Gets the axis object is references
     *
     * @return a enum value for current axis
     */
    public AxisOrientation getAxisOrientation() {
        return mAxisOrientation;
    }

    /**
     * Number of grid lines at 100% zoom
     *
     * @return current number of grid lines
     */
    public int getNumberOfGridLines() {
        return mNumberOfGridLines;
    }

    /**
     * Change the stroke width of the lines at runtime
     *
     * @param strokeWidth new stroke width value
     */
    public void setGridStrokeWidth(int strokeWidth) {
        mGridStrokeWidth = strokeWidth;
    }

    /**
     * Change the color of the lines at runtime
     *
     * @param color new color value
     */
    public void setColor(int color) {
        mGridColor = color;
    }

    /**
     * To be implemented when axis orientation is know
     *
     * @param gridLine grid line to find out the intersecting value
     * @return intersecting point
     */
    public abstract float intersect(int gridLine);

    /**
     * To be implemented when scale is know LOG/LIN
     *
     * @param gridLine grid line to find out the intersecting value
     * @param dimensionLength length of width or height
     * @return intersecting point
     */
    protected abstract float intersect(int gridLine, int dimensionLength);

    /**
     * The grid lines are a underlay and is considered a underlay there we do not change the
     * drawable area.
     *
     * @param currentDrawableArea will reflect the new drawable area pass in current drawableArea
     */
    @Override
    public void calculateRemainingDrawableArea(DrawableArea currentDrawableArea) {
    }

    public void setZoomDisplay(ZoomDisplay zoomDisplay) {
        mZoomDisplay = zoomDisplay;
    }

    enum AxisOrientation {
        xAxis,
        yAxis
    }
}
