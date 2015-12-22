package com.nfx.android.library.androidgraph;

import android.content.Context;
import android.graphics.Canvas;

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
    private final Background mBackground;
    /**
     * An object to draw a board around the graph
     */
    private final Boarder mBoarder;
    /**
     * Handles the drawing of all grid lines
     */
    private final GridLines mYGridLines;
    private final GridLines mXGridLines;
    /**
     * Handles the drawing of all text on axis
     */
    private BoarderText mBoarderText;
    /**
     * Set dependant which constructor is called
     */
    private boolean mShowAxisText = false;


    /**
     * Constructor for Background Manager, all drawable objects are created here. Call this
     * constructor if you want the axis text to be shown
     *
     * @param context       application context
     * @param minimumXValue minimum value graph represents for x
     * @param maximumXValue maximum value graph represents for x
     * @param minimumYValue minimum value graph represents for y
     * @param maximumYValue maximum value graph represents for y
     */
    public BackgroundManager(Context context, float minimumXValue, float maximumXValue,
                             float minimumYValue, float maximumYValue) {

        this();
        mShowAxisText = true;
        mBoarderText = new BoarderText(context, minimumXValue, maximumXValue, minimumYValue,
                maximumYValue);
        mXGridLines.showAxisText(context, minimumXValue, maximumXValue);
        mYGridLines.showAxisText(context, minimumYValue, maximumYValue);
    }

    /**
     * Constructor for Background Manager, all drawable objects are created here
     */
    public BackgroundManager() {
        mBackground = new Background();
        mBoarder = new Boarder();

        mXGridLines = new LinXGridLines();
        mYGridLines = new LinYGridLines();
    }

    /**
     * Call when the surface view changes it's dimensions the objects have to called in the correct
     * order to ensure they take up the correct space
     *
     * @param drawableArea the available area to draw
     */
    public void surfaceChanged(DrawableArea drawableArea) {
        mBackground.surfaceChanged(drawableArea);

        if (mShowAxisText) {
            mBoarderText.surfaceChanged(drawableArea);
            // we have to call Y here first to shift the x text into the right location
            mYGridLines.notifyAxisTextOfSurfaceChange(drawableArea);
            mXGridLines.notifyAxisTextOfSurfaceChange(drawableArea);
        }

        mBoarder.surfaceChanged(drawableArea);

        mXGridLines.surfaceChanged(drawableArea);
        mYGridLines.surfaceChanged(drawableArea);
    }

    /**
     * Call with the canvas to draw on
     * @param canvas canvas to draw the objects onto
     */
    public void doDraw(Canvas canvas) {
        mBackground.doDraw(canvas);
        mBoarder.doDraw(canvas);

        mXGridLines.doDraw(canvas);
        mYGridLines.doDraw(canvas);

        if (mShowAxisText) {
            mBoarderText.doDraw(canvas);
        }
    }

    public GridLines getXGridLines() {
        return mXGridLines;
    }

    public GridLines getYGridLines() {
        return mYGridLines;
    }

    /**
     * This tells the graph that there are signals to display, each signal gets its own drawer,
     * At the current time the last signal in the list will control the zoom levels. This is because
     * we are trying to control a single axis zoom from multiple signals. TODO
     *
     * @param signalBuffers pass the object of signals to display on the graph
     */
    public void setSignalBuffers(SignalBuffers signalBuffers) {
        for (SignalBuffer signalBuffer : signalBuffers.getSignalBuffer().values()) {

            mXGridLines.setZoomDisplay(signalBuffer.getXZoomDisplay());
            mYGridLines.setZoomDisplay(signalBuffer.getYZoomDisplay());

            mBoarderText.setZoomDisplay(signalBuffer.getXZoomDisplay(),
                    signalBuffer.getYZoomDisplay());
        }
    }
}
