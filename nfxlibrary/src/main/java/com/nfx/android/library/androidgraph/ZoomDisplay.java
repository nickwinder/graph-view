package com.nfx.android.library.androidgraph;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * NFX Development
 * Created by nick on 27/10/15.
 *
 * A helper class which will hold the zoom values of a given view. A listener can get updates
 * when the surface size has changed.
 */
public class ZoomDisplay {
    /**
     * Absolute maximum zoom value
     */
    public static final float MAXIMUM_ZOOM_LEVEL = 1f;
    /**
     * Absolute minimum zoom level
     */
    public static final float MINIMUM_ZOOM_LEVEL = 0f;
    @SuppressWarnings("unused")
    private static final String TAG = "ZoomDisplay";
    /**
     * Subscribe to this listener to get Zoom notification changes
     */
    private final CopyOnWriteArrayList<ZoomChangedListener> mZoomChangedListener = new
            CopyOnWriteArrayList<>();
    /**
     * Maximum zoom level
     */
    private float mMaximumZoomLevel = 1f;
    /**
     * Minimum zoom level
     */
    private float mMinimumZoomLevel = 0f;
    /**
     * percentage of view to display in given area
     */
    private float mZoomLevelPercentage = 1f;
    /**
     * offset in given plane where to display from
     */
    private float mDisplayOffsetPercentage = 0f;


    /**
     * Initial values of the zoom level
     * @param zoomLevelPercentage     a float referenced as 0% = 0 100% = 1 if outside this value a
     *                                warning is logged and 1 will be assigned
     * @param displayOffsetPercentage a float referenced as 0% = 0 100% = 1 if outside this value a
     *                                warning is logged and 0f will be assigned
     **/
    @SuppressWarnings("SameParameterValue")
    ZoomDisplay(float zoomLevelPercentage, float displayOffsetPercentage) {
        if(zoomLevelPercentage < mMaximumZoomLevel && zoomLevelPercentage > mMinimumZoomLevel) {
            mZoomLevelPercentage = zoomLevelPercentage;
        }
        if(displayOffsetPercentage < mMaximumZoomLevel && displayOffsetPercentage >
                mMinimumZoomLevel) {
            // Ensure that the zoom level will be within the bounds of the screen
            if((displayOffsetPercentage + mZoomLevelPercentage) > mMaximumZoomLevel) {
                mDisplayOffsetPercentage = mMaximumZoomLevel - mZoomLevelPercentage;
            } else {
                mDisplayOffsetPercentage = displayOffsetPercentage;
            }
        }
    }

    /**
     * register a listener for zoom changed reports
     * @param listener register listener
     */
    public void setTheListener(ZoomChangedListener listener) {
        mZoomChangedListener.add(listener);
    }

    /**
     * @return the value of mDisplayOffsetPercentage
     */
    public float getDisplayOffsetPercentage() {
        return mDisplayOffsetPercentage;
    }

    /**
     * change the view offset
     *
     * @param displayOffsetPercentage a float referenced as a percentage across the screen 0% = 0
     *                                100% = 1 if outside this value a warning is logged and
     *                                nothing happens
     */
    public void setDisplayOffsetPercentage(float displayOffsetPercentage) {
        if(displayOffsetPercentage < mMinimumZoomLevel) {
            displayOffsetPercentage = mMinimumZoomLevel;
        } else if(displayOffsetPercentage > MAXIMUM_ZOOM_LEVEL) {
            displayOffsetPercentage = MAXIMUM_ZOOM_LEVEL;
        }

        // Ensure that the zoom level will be within the bounds of the screen
        if((displayOffsetPercentage + mZoomLevelPercentage) > mMaximumZoomLevel) {
            mDisplayOffsetPercentage = mMaximumZoomLevel - mZoomLevelPercentage;
        } else {
            mDisplayOffsetPercentage = displayOffsetPercentage;
        }

        if (!mZoomChangedListener.isEmpty()) {
            for (ZoomChangedListener listener : mZoomChangedListener) {
                listener.zoomChanged();
            }
        }
    }

    /**
     * @return the value of mDisplayOffsetPercentage + size currently being displayed
     */
    public float getFarSideOffsetPercentage() {
        return mDisplayOffsetPercentage + mZoomLevelPercentage;
    }

    /**
     * @return the value of mZoomLevelPercentage
     */
    public float getZoomLevelPercentage() {
        return mZoomLevelPercentage;
    }

    /**
     * change the zoom level of the view
     * @param zoomLevelPercentage a float referenced as 0% = 0 100% = 1 if outside this value a
     *                            warning is logged and nothing happens
     */
    public void setZoomLevelPercentage(float zoomLevelPercentage) {
        if(zoomLevelPercentage < MINIMUM_ZOOM_LEVEL) {
            return;
        } else if(zoomLevelPercentage > MAXIMUM_ZOOM_LEVEL) {
            zoomLevelPercentage = MAXIMUM_ZOOM_LEVEL;
        }

        // Ensure that the zoom level will be within the bounds of the screen
        if((zoomLevelPercentage + mDisplayOffsetPercentage) > mMaximumZoomLevel) {
            mDisplayOffsetPercentage = mMaximumZoomLevel - zoomLevelPercentage;
        }
        if((mMaximumZoomLevel - zoomLevelPercentage) < mMinimumZoomLevel) {
            mDisplayOffsetPercentage = mMinimumZoomLevel;
            zoomLevelPercentage = mMaximumZoomLevel - mMinimumZoomLevel;
        }

        mZoomLevelPercentage = zoomLevelPercentage;

        if (!mZoomChangedListener.isEmpty()) {
            for (ZoomChangedListener listener : mZoomChangedListener) {
                listener.zoomChanged();
            }
        }
    }

    public void setZoomLimits(float minimumZoomLevel, float maximumZoomLevel) {
        mMinimumZoomLevel = minimumZoomLevel;
        mMaximumZoomLevel = maximumZoomLevel;
        setZoomLevelPercentage(maximumZoomLevel - minimumZoomLevel);
        setDisplayOffsetPercentage(minimumZoomLevel);
    }

    /**
     * An interface for subscribes to get information about view zoom level changes
     */
    public interface ZoomChangedListener {
        void zoomChanged();
    }
}