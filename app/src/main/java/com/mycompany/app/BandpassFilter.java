package com.mycompany.app;

import org.jtransforms.fft.DoubleFFT_1D;
import org.apache.commons.math3.analysis.function.Sinc;
import org.apache.commons.math3.complex.Complex;

public class BandpassFilter {

    public static byte[] applyBandpassFilter(byte[] rawData, double lowFreq, double highFreq, double sampleRate) {
        // Convert byte array to double array for signal processing
        double[] signal = new double[rawData.length];
        for (int i = 0; i < rawData.length; i++) {
            signal[i] = (double) rawData[i];
        }

        // Apply FFT using JTransforms
        DoubleFFT_1D fft = new DoubleFFT_1D(signal.length);
        fft.realForward(signal);

        // Create a bandpass filter in the frequency domain using a Butterworth filter
        double[] filter = createButterworthBandpassFilter(signal.length, lowFreq, highFreq, sampleRate, 5);
        // Apply the filter to the transformed signal
        double[] filteredSignal = new double[signal.length];
        for (int i = 0; i < signal.length / 2; i++) {
            int index = i * 2;
            double magnitude = Math.hypot(signal[index], signal[index + 1]);

            // Check if the frequency is within the desired range
            double freq = i * sampleRate / signal.length;
            if (freq >= lowFreq && freq <= highFreq) {
                double filteredMagnitude = magnitude * filter[i];
                double phase = Math.atan2(signal[index + 1], signal[index]);
                filteredSignal[index] = filteredMagnitude * Math.cos(phase);
                filteredSignal[index + 1] = filteredMagnitude * Math.sin(phase);
            } else {
                // If the frequency is outside the desired range, set the magnitude to zero
                filteredSignal[index] = 0.0;
                filteredSignal[index + 1] = 0.0;
            }
        }

        // Apply inverse FFT using JTransforms to the filtered signal
        fft.realInverse(filteredSignal, true);

        // Convert the filtered signal back to byte array
        byte[] filteredData = new byte[filteredSignal.length];
        for (int i = 0; i < filteredSignal.length; i++) {
            filteredData[i] = (byte) filteredSignal[i];
        }

        return filteredData;
    }

    private static double[] createButterworthBandpassFilter(int length, double lowFreq, double highFreq,
            double sampleRate, int order) {
        double[] filter = new double[length / 2];
        Sinc sinc = new Sinc();

        for (int i = 0; i < filter.length; i++) {
            double freq = i * sampleRate / length;
            filter[i] = butterworthFilter(freq, lowFreq, highFreq, order)
                    * sinc.value((freq - lowFreq) / (highFreq - lowFreq));

        }

        return filter;
    }

    private static double butterworthFilter(double freq, double lowFreq, double highFreq, int order) {
        double omega = 2.0 * Math.PI * freq / (highFreq - lowFreq);
        double x = Math.sin(0.5 * omega);
        double y = 1.0 / Math.cos(0.5 * omega);

        double alpha = x / y;
        double beta = (0.5 - alpha) / (0.5 + alpha);

        return 1.0 / (1.0 + Math.pow(beta, order));
    }

    // public static void main(String[] args) {
    // // Example usage
    // try {
    // // Replace with your actual audio data and parameters
    // byte[] rawData = /* Your raw audio data */;
    // double lowFreq = 1000; // Replace with your desired lower frequency
    // double highFreq = 4000; // Replace with your desired higher frequency
    // double sampleRate = 44100; // Replace with your actual sample rate

    // byte[] filteredData = applyBandpassFilter(rawData, lowFreq, highFreq,
    // sampleRate);

    // // Now, filteredData contains the byte array of the audio after applying the
    // bandpass filter
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }
}
