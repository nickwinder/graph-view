package com.nfx.android.library.androidgraph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * NFX Development
 * Created by nick on 27/10/15.
 *
 * Extend from this class to create a drawable line with view zooming capabilities
 */
public abstract class GridLines extends DrawableObject {

    /**
     * Indicates that the grid line is less than the viewable area
     */
    static final float LESS_THAN_VIEWABLE_AREA = -1;
    /**
     * Indicates that the grid line is greater than the viewable area
     */
    static final float GREATER_THAN_VIEWABLE_AREA = -2;
    /**
     * When a using asks for a grid line which is not present in this object
     */
    static final float GRID_LINE_OUT_OF_RANGE = -3;
    /**
     * Color of the grid lines
     */
    private static final int INITIAL_LINE_COLOR = Color.GRAY;
    /**
     * Use an even number to ensure all grid line strokes look the same
     */
    private static final float INITIAL_LINE_STROKE_WIDTH = 4f;
    /**
     * Paint for the grid lines
     */
    final Paint mPaint = new Paint();
    /**
     * This allows us to know the axis at runtime
     */
    private final AxisOrientation mAxisOrientation;
    /**
     * Minor GridLines
     */
    private final Map<Integer, GridLines> mChildGridLines = new ConcurrentHashMap<>();
    /**
     * Number of grid lines to display in the area
     */
    int mNumberOfGridLines = 6;
    /**
     * Describes the viewable part of the grid
     */
    ZoomDisplay mZoomDisplay;

    /**
     * This is a zoom that is never changed over the runtime of the app. Useful for setting limits
     */
    ZoomDisplay mFixedZoomDisplay;
    /**
     * The axis text to be displayed if needed
     */
    AxisText mAxisText;
    /**
     * Graph dimension size, This is needed for minor grid lines to calculate where to display in
     * cases of zoom
     */
    float mGridLinesSize;
    float mGridLinesOffset;
    /**
     * scale for child grid lines
     */
    GraphManager.Scale mChildGridLineScale;
    /**
     * Base Context
     */
    private Context mContext;

    /**
     * Constructor of GridLines
     *
     * @param axisOrientation either the x or y axis
     */
    GridLines(AxisOrientation axisOrientation) {
        mAxisOrientation = axisOrientation;
        mFixedZoomDisplay = new ZoomDisplay(1f, 0f);
        // Set a default zoom Display
        mZoomDisplay = new ZoomDisplay(1f, 0f);
        mPaint.setColor(INITIAL_LINE_COLOR);
        mPaint.setStrokeWidth(INITIAL_LINE_STROKE_WIDTH);

        setGridLinesSize(1f);
    }

    @Override
    public void doDraw(Canvas canvas) {
        if (mAxisText != null) {
            mAxisText.doDraw(canvas);
        }
        Iterator<GridLines> iterator = mChildGridLines.values().iterator();
        while(iterator.hasNext()) {
            iterator.next().doDraw(canvas);
        }
    }

    public void showAxisText(Context context, float minimumValue, float maximumValue) {
        mContext = context;
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
     * Set the number of Grid lines for this object
     *
     * @param numberOfGridLines amount of gridlines
     */
    public void setNumberOfGridLines(int numberOfGridLines) {
        mNumberOfGridLines = numberOfGridLines;
    }

    /**
     * Change the stroke width of the lines at runtime
     *
     * @param strokeWidth new stroke width value
     */
    private void setGridStrokeWidth(int strokeWidth) {
        mPaint.setStrokeWidth(strokeWidth);

    }

    /**
     * Change the color of the lines at runtime
     *
     * @param color new color value
     */
    private void setColor(int color) {
        mPaint.setColor(color);
    }

    float getGridLineDrawableWidth() {
        // -1 as we want the first grid line to be at 0 and the last at the width of the graph
        return mGridLinesSize / (float) (mNumberOfGridLines - 1);
    }

    abstract float intersect(int gridLine);

    /**
     * Gives the value of where a grid line will interest x on the screen
     *
     * @param gridLine        grid line to find, base 0
     * @return the x Intersect
     *          -3 if the grid line is out of range
     *          -1 if Less than viewable area
     *          -2 if greater than viewable area
     */
    public float intersectZoomCompensated(int gridLine) {
        float intersect = intersect(gridLine);
        if(intersect == GRID_LINE_OUT_OF_RANGE) {
            return GRID_LINE_OUT_OF_RANGE;
        }

        if(intersect < mZoomDisplay.getDisplayOffsetPercentage()) {
            return LESS_THAN_VIEWABLE_AREA;
        } else if(intersect > mZoomDisplay.getFarSideOffsetPercentage()) {
            return GREATER_THAN_VIEWABLE_AREA;
        } else {
            return (intersect - mZoomDisplay.getDisplayOffsetPercentage()) /
                    mZoomDisplay.getZoomLevelPercentage();
        }
    }

    /**
     * The grid lines are a underlay and is considered a underlay there we do not change the
     * drawable area.
     *
     * @param currentDrawableArea will reflect the new drawable area pass in current drawableArea
     */
    @Override
    public void calculateRemainingDrawableArea(DrawableArea currentDrawableArea) {
    }

    /**
     * Used for minor grid lines to gain a reference to the graph size. Must call after surface
     * changed if used
     *
     * @param gridLinesSize size of the full graph viewable area
     */
    void setGridLinesSize(float gridLinesSize) {
        mGridLinesSize = gridLinesSize;
    }

    void setGridLinesOffset(float graphOffset) {
        mGridLinesOffset = graphOffset;
    }

    public void setChildGridLineScale(GraphManager.Scale scale) {
        mChildGridLineScale = scale;
    }

    /**
     * As axis text has to attain the drawable area before the grid lines to ensure grid lines do
     * not over lap the text. We have to provide another function. this function has to be called
     * for each grid lines before {@code surfaceChanged} of grid lines
     *
     * @param drawableArea the drawable area available
     */
    public void notifyAxisTextOfSurfaceChange(DrawableArea drawableArea) {
        if (mAxisText != null) {
            mAxisText.surfaceChanged(drawableArea);
        }
        for (GridLines gridLines : mChildGridLines.values()) {
            gridLines.notifyAxisTextOfSurfaceChange(drawableArea);
        }
    }

    /**
     * The surface size has changed update the current object to resize drawing
     *
     * @param drawableArea new surface size
     */
    @Override
    public void surfaceChanged(DrawableArea drawableArea) {
        super.surfaceChanged(drawableArea);
        for (Map.Entry<Integer, GridLines> gridLines : mChildGridLines.entrySet()) {
            gridLines.getValue().surfaceChanged(drawableArea);
            minorGridLineSurfaceChanged(gridLines.getValue(), gridLines.getKey());
        }

        if(mAxisText != null) {
            mAxisText.calculateGridLineValues();
        }
    }

    private ZoomDisplay getZoomDisplay() {
        return mZoomDisplay;
    }

    /**
     * Set the zoomDisplay for the grid lines should morph to
     *
     * @param zoomDisplay zoomDisplay to set
     */
    public void setZoomDisplay(ZoomDisplay zoomDisplay) {
        mZoomDisplay = zoomDisplay;

        zoomDisplay.setTheListener(new ZoomDisplay.ZoomChangedListener() {
            @Override
            public void zoomChanged() {
                Map<Integer, Boolean> minorXGridLinesToDisplay =
                        adequateSpaceForMinorGridLines();

                for (Map.Entry<Integer, Boolean> majorGridLine : minorXGridLinesToDisplay
                        .entrySet()) {
                    if (majorGridLine.getValue()) {
                        addMinorGridLine(majorGridLine.getKey());
                    } else {
                        mChildGridLines.remove(majorGridLine.getKey());
                    }
                }
            }
        });
    }

    /**
     * Reports if there is adequate space to fit minor grid lines between current grid lines
     *
     * @return a key pair that gives a boolean to show if there is enough space between the grid
     * lines
     */
    private Map<Integer, Boolean> adequateSpaceForMinorGridLines() {
        Map<Integer, Boolean> adequateSpaceList = new HashMap<>();
        final double zoomSpacing =
                (getGridLineDrawableWidth() / mZoomDisplay.getZoomLevelPercentage())
                        * getDimensionLength();
        final float mPlaceMinorGridLinesSize = 500f;

        for (int i = 0; i < getNumberOfGridLines() - 1; ++i) {
            // If the grid lines spacing is greater than this number minor grid lines are added
            if (zoomSpacing > mPlaceMinorGridLinesSize) {
                double lowerIntersect = intersectZoomCompensated(i);
                double upperIntersect = intersectZoomCompensated(i + 1);
                if (lowerIntersect != upperIntersect) {
                    adequateSpaceList.put(i, true);
                } else {
                    adequateSpaceList.put(i, false);
                }
            } else {
                adequateSpaceList.put(i, false);
            }
        }

        return adequateSpaceList;
    }

    /**
     * Add a child minor Grid Line to this grid line
     *
     * @param majorGridLine the grid line number to insert the minor grid line after
     */
    private void addMinorGridLine(int majorGridLine) {
        if (!mChildGridLines.containsKey(majorGridLine)) {
            GridLines minorGridLine;
            if (mAxisOrientation == AxisOrientation.xAxis) {
                if(mChildGridLineScale == GraphManager.Scale.linear) {
                    minorGridLine = new LinXGridLines();
                } else {
                    minorGridLine = new LogXGridLines(mAxisText.getAxisValueSpan(),
                            mAxisText.getMaximumAxisValue() / (float) Math.pow(10,
                                    (getNumberOfGridLines() - 2 - majorGridLine)));
                }
            } else {
                if(mChildGridLineScale == GraphManager.Scale.linear) {
                    minorGridLine = new LinYGridLines();
                } else {
                    minorGridLine = new LogYGridLines(mAxisText.getAxisValueSpan(),
                            (int) mAxisText.getMaximumAxisValue() / (float) Math.pow(10,
                                    (getNumberOfGridLines() - 2 - majorGridLine)));
                }
            }
            minorGridLine.setGridStrokeWidth(2);
            minorGridLine.setColor(Color.DKGRAY);
            minorGridLine.setNumberOfGridLines(11);
            mChildGridLines.put(majorGridLine, minorGridLine);

            if (mAxisText != null) {
                minorGridLine.showAxisText(mContext, mAxisText.getMinimumAxisValue(),
                        mAxisText.getMaximumAxisValue());
            }

            minorGridLineSurfaceChanged(minorGridLine, majorGridLine);
            minorGridLine.setZoomDisplay(getZoomDisplay());
        }
    }

    /**
     * this tells the children grid lines where the major grid lines would sit at 100% zoom level in
     * the new surface dimensions
     *
     * @param gridLine      The child grid line
     * @param majorGridLine major grid line the child is sitting on
     */
    private void minorGridLineSurfaceChanged(GridLines gridLine, int majorGridLine) {
        DrawableArea parentDrawableArea = getDrawableArea();
        gridLine.surfaceChanged(parentDrawableArea);

        float left = intersect(majorGridLine);
        float right = intersect(majorGridLine + 1);

        gridLine.surfaceChanged(parentDrawableArea);

        gridLine.setGridLinesSize(right - left);
        gridLine.setGridLinesOffset(left);

        if (mAxisText != null) {
            // We want out children Axis to have the same drawable area as our own.
            gridLine.getAxisText().getDrawableArea().setDrawableArea(mAxisText.getDrawableArea());
        }
    }

    /**
     * Used to report back the height for the yAxis or width on the xAxis
     *
     * @return dimension length
     */
    abstract float getDimensionLength();

    public AxisText getAxisText() {
        return mAxisText;
    }

    public ZoomDisplay getFixedZoomDisplay() {
        return mFixedZoomDisplay;
    }

    enum AxisOrientation {
        xAxis,
        yAxis
    }
}
