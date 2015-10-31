package com.nfx.android.library.androidgraph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.Collection;

/**
 * NFX Development
 * Created by nick on 28/10/15.
 *
 * The background manager holders many drawable objects which are considered background objects
 * It makes batch calls to DoDraw functions of all it's members and individual sizing options
 * are possible by overriding surfaceChanged
 */
public class BackgroundManager {
    private static final String TAG = "BackgroundManager";
    /**
     * An object which draws onto the canvas
     **/
    private Background mBackground;
    /**
     * An object to draw a board around the graph
     */
    private Boarder mBoarder;
    /**
     * Handles the drawing of all grid lines
     */
    private GridLines mYMajorGridLines;
    private GridLines mXMajorGridLines;
    private Collection<GridLines> mGridLinesMinor = new ArrayList<>();
    /**
     * Handles the drawing of all text on axis
     */
    private Collection<AxisText> mXAxisText = new ArrayList<>();
    private Collection<AxisText> mYAxisText = new ArrayList<>();
    /**
     * Set dependant which constuctor is called
     */
    private boolean mShowAxisText = false;
    /**
     * These object will be passed into drawable objects that need to be resized based on zoom level
     */
    private ZoomDisplay mZoomDisplayX;
    private ZoomDisplay mZoomDisplayY;

    /**
     * Constructor for Background Manager, all drawable objects are created here. Call this
     * constructor if you want the axis text to be shown
     *
     * @param context       application context
     * @param zoomDisplayX  reference to graphManagers zoomDisplay for x
     * @param zoomDisplayY  reference to graphManagers zoomDisplay for y
     * @param minimumXValue minimum value graph represents for x
     * @param maximumXValue maximum value graph represents for x
     * @param minimumYValue minimum value graph represents for y
     * @param maximumYValue maximum value graph represents for y
     */
    public BackgroundManager(Context context, ZoomDisplay zoomDisplayX, ZoomDisplay zoomDisplayY,
                             float minimumXValue, float maximumXValue, float minimumYValue,
                             float maximumYValue) {
        this(context, zoomDisplayX, zoomDisplayY);

        mXAxisText.add(new XAxisText(context, mXMajorGridLines, minimumXValue, maximumXValue));
        mYAxisText.add(new YAxisText(context, mYMajorGridLines, minimumYValue, maximumYValue));
        mShowAxisText = true;
    }

    /**
     * Constructor for Background Manager, all drawable objects are created here
     * @param context application context
     * @param zoomDisplayX reference to graphManagers zoomDisplay for x
     * @param zoomDisplayY reference to graphManagers zoomDisplay for y
     */
    public BackgroundManager(Context context, ZoomDisplay zoomDisplayX, ZoomDisplay zoomDisplayY) {

        mZoomDisplayX = zoomDisplayX;
        mZoomDisplayY = zoomDisplayY;

        zoomDisplayX.setTheListener(new ZoomDisplay.ZoomChangedListener() {
            @Override
            public void zoomChanged() {
//                for (GridLines gridLines : mGridLinesMajor) {
//                    if (gridLines.getAxisOrientation() == GridLines.AxisOrientation.xAxis) {
//                        gridLines.updateZoomLevel();
//                    }
//                }
//                for (GridLines gridLines : mGridLinesMinor) {
//                    if (gridLines.getAxisOrientation() == GridLines.AxisOrientation.xAxis) {
//                        gridLines.updateZoomLevel();
//                    }
//                }
            }
        });
        zoomDisplayY.setTheListener(new ZoomDisplay.ZoomChangedListener() {
            @Override
            public void zoomChanged() {
//                for (GridLines gridLines : mGridLinesMajor) {
//                    if (gridLines.getAxisOrientation() == GridLines.AxisOrientation.yAxis) {
//                        gridLines.updateZoomLevel();
//                    }
//                }
//                for (GridLines gridLines : mGridLinesMinor) {
//                    if (gridLines.getAxisOrientation() == GridLines.AxisOrientation.yAxis) {
//                        gridLines.updateZoomLevel();
//                    }
//                }
            }
        });

        mBackground = new Background();
        mBoarder = new Boarder();

        mXMajorGridLines = new LogXGridLines(mZoomDisplayX);
        mYMajorGridLines = new LinYGridLines(mZoomDisplayY);

        mGridLinesMinor.add(new LogXGridLines(mZoomDisplayX));

        for (GridLines gridLines : mGridLinesMinor) {
            gridLines.setGridStrokeWidth(2);
            gridLines.setColor(Color.DKGRAY);
        }
    }

    /**
     * Call when the surface view changes it's dimensions the objects have to called in the correct
     * order to ensure they take up the correct space
     *
     * @param drawableArea the avaiable area to draw
     */
    public void surfaceChanged(DrawableArea drawableArea) {
        mBackground.surfaceChanged(drawableArea);

        if (mShowAxisText) {
            for (AxisText axisText : mYAxisText) {
                axisText.surfaceChanged(drawableArea);
            }
            for (AxisText axisText : mXAxisText) {
                axisText.surfaceChanged(drawableArea);
            }
        }

        mBoarder.surfaceChanged(drawableArea);

        // When the boarder is used we have to shift the axis text as it would no longer be inline
        // with the grid lines
        if (mShowAxisText) {
            for (AxisText axisText : mYAxisText) {
                axisText.getDrawableArea().setDrawableArea(axisText.getDrawableArea().getLeft(),
                        axisText.getDrawableArea().getTop() + mBoarder.getStrokeWidth(),
                        axisText.getDrawableArea().getWidth(),
                        axisText.getDrawableArea().getHeight() - mBoarder.getStrokeWidth());
            }
            for (AxisText axisText : mXAxisText) {
                axisText.getDrawableArea().setDrawableArea(
                        axisText.getDrawableArea().getLeft() + mBoarder.getStrokeWidth(),
                        axisText.getDrawableArea().getTop(),
                        axisText.getDrawableArea().getWidth() - mBoarder.getStrokeWidth(),
                        axisText.getDrawableArea().getHeight());
            }
        }

        mXMajorGridLines.surfaceChanged(drawableArea);
        mYMajorGridLines.surfaceChanged(drawableArea);

        for (GridLines gridLinesMinor : mGridLinesMinor) {
            if (gridLinesMinor.getAxisOrientation() == GridLines.AxisOrientation.xAxis) {
                int minorGridLineWidth = (int) mXMajorGridLines.intersect(0);
                gridLinesMinor.getDrawableArea().setDrawableArea(drawableArea.getLeft(),
                        drawableArea.getTop(), minorGridLineWidth, drawableArea.getHeight());
            }
        }
    }

    /**
     * Call with the canvas to draw on
     * @param canvas canvas to draw the objects onto
     */
    public void doDraw(Canvas canvas) {
        mBackground.doDraw(canvas);
        mBoarder.doDraw(canvas);

        mXMajorGridLines.doDraw(canvas);
        mYMajorGridLines.doDraw(canvas);

        for (GridLines gridLines : mGridLinesMinor) {
            gridLines.doDraw(canvas);
        }

        if (mShowAxisText) {
            for (AxisText axisText : mXAxisText) {
                axisText.doDraw(canvas);
            }
            for (AxisText axisText : mYAxisText) {
                axisText.doDraw(canvas);
            }
        }
    }
}
