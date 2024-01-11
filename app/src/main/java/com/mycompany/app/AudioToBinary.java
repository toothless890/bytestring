// provided by chatGPT
package com.mycompany.app;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class AudioToBinary {
    private static final double LOUDNESS_THRESHOLD = 0.04;
    private static final int ROLLING_AVERAGE_WINDOW = 20; // Adjust the window size based on your requirements

    public static byte[] recordAudio(int sampleRate, int durationInSeconds) {
        try {
            AudioFormat format = new AudioFormat(sampleRate, 8, 1, true, false);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Line not supported");
                System.exit(0);
            }

            TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();

            Queue<Double> rollingAverageQueue = new ArrayDeque<>();
            double rollingAverage = 0;

            // Wait until loudness exceeds the threshold
            while (rollingAverage < LOUDNESS_THRESHOLD) {
                byte[] buffer = new byte[1024];
                line.read(buffer, 0, buffer.length);
                double loudness = calculateLoudness(buffer);
                rollingAverage = updateRollingAverage(rollingAverageQueue, loudness);
            }
            System.out.println("start");
            // Now record the audio until loudness falls below the threshold
            int numSamples = sampleRate * durationInSeconds;
            byte[] recordedAudio = new byte[numSamples];
            int bytesRead;
            int totalBytesRead = 0;

            while (rollingAverage >= LOUDNESS_THRESHOLD && totalBytesRead < numSamples) {
                byte[] buffer = new byte[1024];
                bytesRead = line.read(buffer, 0, buffer.length);
                if (bytesRead == -1) {
                    break; // Stop if there is an issue with reading audio data
                }
                System.arraycopy(buffer, 0, recordedAudio, totalBytesRead, bytesRead);
                totalBytesRead += bytesRead;
                double loudness = calculateLoudness(buffer);
                rollingAverage = updateRollingAverage(rollingAverageQueue, loudness);
            }
            System.out.println("end");
            line.stop();
            line.close();

            return Arrays.copyOf(recordedAudio, totalBytesRead);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            return null;
        }
    }


    private static double calculateLoudness(byte[] buffer) {
        // Calculate the loudness of the audio signal (e.g., RMS or peak amplitude)
        // Implement your loudness calculation here
        // For simplicity, let's assume the loudness is the peak amplitude
        double maxAmplitude = 0;
        for (byte b : buffer) {
            double amplitude = Math.abs((double) b / Byte.MAX_VALUE);
            maxAmplitude = Math.max(maxAmplitude, amplitude);
        }
        return maxAmplitude;
    }

    private static double updateRollingAverage(Queue<Double> rollingAverageQueue, double loudness) {
        rollingAverageQueue.add(loudness);
        if (rollingAverageQueue.size() > ROLLING_AVERAGE_WINDOW) {
            rollingAverageQueue.poll();
        }
        double sum = 0;
        for (Double value : rollingAverageQueue) {
            sum += value;
        }
        return sum / rollingAverageQueue.size();
    }


    public static String demodulateAFSK(byte[] audioSignal, int sampleRate, int baudRate) {
        double frequencyMark = 1200;
        double frequencySpace = 2500;
        int samplesPerBit = sampleRate / baudRate;

        StringBuilder binaryData = new StringBuilder();

        for (int i = 0; i < audioSignal.length / samplesPerBit; i++) {
            double energyMark = 0;
            double energySpace = 0;

            for (int j = 0; j < samplesPerBit; j++) {
                double angleMark = 2.0 * Math.PI * j / (double) sampleRate * frequencyMark;
                double angleSpace = 2.0 * Math.PI * j / (double) sampleRate * frequencySpace;

                energyMark += audioSignal[i * samplesPerBit + j] * Math.sin(angleMark);
                energySpace += audioSignal[i * samplesPerBit + j] * Math.sin(angleSpace);
            }

            binaryData.append(energyMark > energySpace ? '0' : '1');
        }
        // System.out.println(binaryData.toString());
        return binaryData.toString();
    }

    public static byte[] readWavFile(String path) throws FileNotFoundException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(path));

        int read;
        byte[] buff = new byte[1024];
        try {
            while ((read = in.read(buff)) > 0) {
                out.write(buff, 0, read);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            out.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        byte[] audioBytes = out.toByteArray();
        return audioBytes;
    }

}