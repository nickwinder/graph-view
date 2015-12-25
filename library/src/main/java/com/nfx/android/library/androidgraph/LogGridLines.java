package com.nfx.android.library.androidgraph;

/**
 * NFX Development
 * Created by nick on 28/10/15.
 *
 * To be extend by a axis specific class.
 */
abstract class LogGridLines extends GridLines {
    /**
     * Given span for the axis. This is used to calculate the logarithmic scale
     */
    private final float mAxisSpanValue;
    /**
     * The Decade the grid lines represent
     */
    private final float mDecade;

    /**
     * Constructor which passes straight through
     *]
     * @param axisOrientation either the x or y axis
     */
    LogGridLines(AxisOrientation axisOrientation, float axisSpanValue, float decade) {
        super(axisOrientation);
        mAxisSpanValue = axisSpanValue;
        mDecade = decade;
    }

    @Override
    float intersect(int gridLine) {
        if(gridLine >= mNumberOfGridLines || gridLine < 0) {
            return GRID_LINE_OUT_OF_RANGE;
        }

        float lineLog = GraphManager.log(mDecade * (1f / (float) (getNumberOfGridLines() - 1) *
                (float) gridLine));
        float maxLog = GraphManager.log(mAxisSpanValue);

        return lineLog / maxLog;
    }
}
