package com.nfx.android.library.graphbufferinput;

import android.media.AudioFormat;
import android.media.AudioRecord;

import com.nfx.android.library.androidgraph.GraphView;

import org.jtransforms.fft.FloatFFT_1D;

/**
 * NFX Development
 * Created by nick on 30/11/15.
 * <p/>
 * This class takes the microphone input and computes the fft of signal. In addition to this the
 * final buffer is logarithmic
 */
public class MicrophoneFFTInput extends MicrophoneInput {

    // NW pulled from other magnitude scales. This will ensure a signal of +1 to -1 is equal to 0db
    // This fudge factor is added to the output to make a realistically
    // fully-saturated signal come to 0dB.  Without it, the signal would
    // have to be solid samples of -32768 to read zero, which is not
    // realistic.  This really is a fudge, because the best value depends
    // on the input frequency and sampling rate.  We optimise here for
    // a 1kHz signal at 16,000 samples/sec.
    private static final float FUDGE = 0.63610f;
    /**
     * Buffer to pass to the fft class
     */
    protected float[] mFftBuffer;
    /**
     * Last fft buffer to be converted
     */
    protected float[] mMagnitudeBuffer;
    /**
     * Number of historical buffers to store
     */
    private int mNumberOfHistoryBuffers = 4;
    /**
     * Computes the FFT
     */
    private FloatFFT_1D mFftCalculations = null;
    /**
     * Buffer with the finished data in
     */
    private float[] mReturnedMagnitudeBuffer;
    /**
     * Stores a history of the previous buffers
     */
    private float[][] mHistoryMagnitudeBuffers;
    /**
     * Current history buffer to write into
     */
    private int mHistoryIndex = 0;

    /**
     * Constructor to initialise microphone for listening
     *
     * @param graphSignalInputInterface interface to send signal data to
     * @param binSize set the bin size of the fft
     */
    public MicrophoneFFTInput(GraphView.GraphSignalInputInterface
                                      graphSignalInputInterface, int binSize) {
        super(graphSignalInputInterface, binSize);
    }

    @Override
    public void initialise() {
        super.initialise();

        mFftCalculations = new FloatFFT_1D(mInputBlockSize);

        mFftBuffer = new float[mInputBlockSize * 2];
        mMagnitudeBuffer = new float[mInputBlockSize / 2];
        mReturnedMagnitudeBuffer = new float[mInputBlockSize / 2];

        mHistoryMagnitudeBuffers = new float[mNumberOfHistoryBuffers][mInputBlockSize / 2];
    }

    /**
     * This takes the last read buffer and does a FFT calculation on it. It then converts the values
     * into dB. This may take a while so we have to optimise this as much as possible
     *
     * @param buffer Buffer containing the data.
     */
    @Override
    protected void readDone(float[] buffer) {
        applyMagnitudeConversions(buffer);
        applyingFFTAveraging();
        notifyListenersOfBufferChange(mReturnedMagnitudeBuffer);
    }

    protected void applyMagnitudeConversions(float buffer[]) {
        if(mGraphSignalInputInterface != null) {
            applyHanningWindow(buffer);
            System.arraycopy(buffer, 0, mFftBuffer, 0, buffer.length);
            mFftCalculations.realForwardFull(mFftBuffer);

            float real, imaginary;

            int bufferLength = mMagnitudeBuffer.length;

            for(int i = 0; i < bufferLength; ++i) {
                real = mFftBuffer[i * 2];
                imaginary = mFftBuffer[i * 2 + 1];
                final float scale = buffer.length * FUDGE;
                mMagnitudeBuffer[i] = (float) Math.sqrt(real * real + imaginary * imaginary) /
                        scale;

                // Convert the signal into decibels so it is easier to read on screen.
                // 20*log(value) / scaledToAxisMinimum
                // Then flip the buffer to allow simple display on screen. (Screens display top to
                // bottom, graphs show bottom to top)
                mMagnitudeBuffer[i] = 20f * (float) Math.log10(mMagnitudeBuffer[i]);
                mMagnitudeBuffer[i] /= mGraphSignalInputInterface.getGraphParameters().
                        getYAxisParameters().getMinimumValue(); // Scale to negative 140 db
                mMagnitudeBuffer[i] = 1f - mMagnitudeBuffer[i];
            }
        }
    }

    /**
     * takes in a buffer and applies a hanning window to it.
     *
     * @param buffer buffer to apply the hanning window to
     */
    private void applyHanningWindow(float[] buffer) {
        int bufferLength = buffer.length;
        double twoPi = 2.0 * Math.PI;

        for(int n = 1; n < bufferLength; n++) {
            buffer[n] *= 0.5 * (1 - Math.cos((twoPi * n) / (bufferLength - 1)));
        }
    }

    /**
     * Averages the new buffer with the old buffers and stores the results the return buffer
     */
    private void applyingFFTAveraging() {
        // Update the index.
        if(++mHistoryIndex >= mNumberOfHistoryBuffers) {
            mHistoryIndex = 0;
        }

        int bufferLength = mMagnitudeBuffer.length;

        System.arraycopy(
                mMagnitudeBuffer, 0, mHistoryMagnitudeBuffers[mHistoryIndex], 0, bufferLength);

        for(int i = 0; i < bufferLength; ++i) {
            mReturnedMagnitudeBuffer[i] = 0;
            for(int g = 0; g < mNumberOfHistoryBuffers; ++g) {
                mReturnedMagnitudeBuffer[i] += mHistoryMagnitudeBuffers[g][i];
            }
            mReturnedMagnitudeBuffer[i] /= mNumberOfHistoryBuffers;
        }
    }

    public void setNumberOfHistoryBuffers(int sNumberOfHistoryBuffers) {
        this.mNumberOfHistoryBuffers = sNumberOfHistoryBuffers;

        mHistoryMagnitudeBuffers = new float[sNumberOfHistoryBuffers][mInputBlockSize / 2];
    }

    @Override
    public void setInputBlockSize(int inputBlockSize) {
        boolean running = isRunning();

        final int audioBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_FLOAT);

        if(running) {
            stop();
        }

        if(inputBlockSize < audioBufferSize) {
            mInputBlockSize = audioBufferSize;
        } else {
            mInputBlockSize = inputBlockSize;
        }

        // As we need to change the buffer size of the input we have to change reinitialise all the
        // arrays
        notifyListenersOfInputBlockSizeChange(mInputBlockSize / 2);

        initialise();
        if(running) {
            start();
        }
    }

}
