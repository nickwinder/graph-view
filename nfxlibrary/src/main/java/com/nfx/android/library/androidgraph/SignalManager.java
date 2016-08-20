package com.nfx.android.library.androidgraph;

import android.graphics.Canvas;
import android.util.Log;

import com.nfx.android.library.androidgraph.AxisScale.AxisParameters;
import com.nfx.android.library.graphbufferinput.InputListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/**
 * NFX Development
 * Created by nick on 31/10/15.
 */
public class SignalManager {
    private static final String TAG = SignalManager.class.getName();
    /**
     * parent object
     */
    private final GraphView mGraphView;

    /**
     * Array of drawers to display signals
     */
    private final Map<Integer, Signal> mSignalDrawers = new HashMap<>();
    /**
     * An object holding the signals to display
     */

    private final Map<Integer, SignalBuffer> mSignalBuffers = new
            ConcurrentHashMap<>();
    /**
     * Handles the drawing of a unlimited amount of Markers
     **/
    private List<Marker> mMarkers = new Vector<>();
    /**
     * Current drawable area
     */
    private DrawableArea mDrawableArea = new DrawableArea(0, 0, 0, 0);
    /**
     * Constructor
     *
     * @param graphView needed to set the axis zoom levels
     */
    SignalManager(GraphView graphView) {
        mGraphView = graphView;
    }

    public List<Marker> getMarkers() {
        return mMarkers;
    }

    /**
     * Use to add another signal into the collection. If the Id is not unique it will remove the
     * signal with the given id and display a warning
     *
     * @param sizeOfBuffer size of the buffer to create
     * @param axisParameters scale of buffer x axis
     * @param color color of signal
     */
    public InputListener addSignalBuffer(int id, int sizeOfBuffer, AxisParameters axisParameters,
                                         int color) {
        SignalBuffer signalBuffer = new SignalBuffer(sizeOfBuffer, axisParameters);
        SignalBufferInterface signalBufferInterface = new SignalBufferInterface(signalBuffer);

        synchronized(this) {
            if(mSignalBuffers.put(id, signalBuffer) != null) {
                Log.w(TAG, "signal id exists, overwriting");
            }
        }

        Signal signal = new Signal(mGraphView.getGraphParameters(),
                signalBufferInterface, mGraphView.getXZoomDisplay());
        signal.surfaceChanged(mDrawableArea);
        signal.setColour(color);

        synchronized(this) {
            mSignalDrawers.put(id, signal);
        }

        return signalBufferInterface;
    }

    void addMarker(int colour, int id, Marker.MarkerUpdateInterface markerUpdateInterface) {
        Marker marker = new Marker(id, mGraphView.getGraphSignalInputInterface(),
                mSignalDrawers.get(id).getSignalBufferInterface(),
                markerUpdateInterface);

        marker.surfaceChanged(mDrawableArea);
        marker.setColour(colour);

        mMarkers.add(marker);
    }

    /**
     * Remove signal with given id from collection
     *
     * @param id id of the signal to remove
     */
    public void removedSignalBuffer(int id) {
        synchronized(this) {
            mSignalBuffers.remove(id);
            mSignalDrawers.remove(id);
        }
        for(int i = 0; i < mMarkers.size(); i++) {
            if(id == mMarkers.get(i).getSignalId()) {
                mMarkers.remove(i);
            }
        }
    }

    /**
     * Call when the surface view changes it's dimensions the objects have to called in the correct
     * order to ensure they take up the correct space
     *
     * @param drawableArea the available area to draw
     */
    public void surfaceChanged(DrawableArea drawableArea) {
        mDrawableArea = drawableArea;
        for(Signal signal : mSignalDrawers.values()) {
            signal.surfaceChanged(drawableArea);
        }
        for(Marker marker : mMarkers) {
            marker.surfaceChanged(drawableArea);
        }
    }

    /**
     * Call with the canvas to draw on
     *
     * @param canvas canvas to draw the objects onto
     */
    public void doDraw(Canvas canvas) {
        synchronized(this) {
            for(Signal signal : mSignalDrawers.values()) {
                signal.doDraw(canvas);
            }
            for(Marker marker : mMarkers) {
                marker.doDraw(canvas);
            }
        }
    }

    /**
     * Remove signal drawers when stopped
     */
    public void removeSignalDrawers() {
        synchronized(this) {
            mSignalDrawers.clear();
        }
    }

    public Map<Integer, Signal> getSignalDrawers() {
        return mSignalDrawers;
    }

    public Map<Integer, SignalBuffer> getSignalBuffers() {
        return mSignalBuffers;
    }
}