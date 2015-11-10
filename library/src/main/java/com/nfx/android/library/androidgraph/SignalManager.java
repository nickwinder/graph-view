package com.nfx.android.library.androidgraph;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.Collection;

/**
 * NFX Development
 * Created by nick on 31/10/15.
 */
public class SignalManager {
    /**
     * parent object
     */
    private GraphManager mGraphManager;

    /**
     * Array of drawers to display signals
     */
    private Collection<Signal> mSignalDrawers = new ArrayList<>();
    /**
     * An object holding the signals to display
     */
    private SignalBuffers mSignalBuffers;

    /**
     * Constructor
     *
     * @param graphManager needed to set the axis zoom levels
     */
    SignalManager(GraphManager graphManager) {
        mGraphManager = graphManager;
    }

    /**
     * This tells the graph that there are signals to display, each signal gets its own drawer,
     * At the current time the last signal in the list will control the zoom levels. This is because
     * we are trying to control a single axis zoom from multiple signals. TODO
     *
     * @param signalBuffers pass the object of signals to display on the graph
     */
    public void setSignalBuffers(SignalBuffers signalBuffers) {
        mSignalBuffers = signalBuffers;
        for (SignalBuffer signalBuffer : mSignalBuffers.getSignalBuffer().values()) {
            mSignalDrawers.add(new Signal(signalBuffer));

            mGraphManager.getBackgroundManager().getXMajorGridLines().setZoomDisplay(
                    signalBuffer.getXZoomDisplay());
            mGraphManager.getBackgroundManager().getYMajorGridLines().setZoomDisplay(
                    signalBuffer.getYZoomDisplay());
        }

    }

    /**
     * Call when the surface view changes it's dimensions the objects have to called in the correct
     * order to ensure they take up the correct space
     *
     * @param drawableArea the available area to draw
     */
    public void surfaceChanged(DrawableArea drawableArea) {
        for (Signal signal : mSignalDrawers) {
            signal.surfaceChanged(drawableArea);
        }
    }

    /**
     * Call with the canvas to draw on
     *
     * @param canvas canvas to draw the objects onto
     */
    public void doDraw(Canvas canvas) {
        if (mSignalBuffers != null) {
            for (Signal signal : mSignalDrawers) {
                signal.doDraw(canvas);
            }
        }
    }
}