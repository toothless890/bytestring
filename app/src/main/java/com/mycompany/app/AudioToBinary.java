// provided by chatGPT
package com.mycompany.app;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class AudioToBinary {


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

            int numSamples = sampleRate * durationInSeconds;
            byte[] recordedAudio = new byte[numSamples];
            line.read(recordedAudio, 0, numSamples);

            line.stop();
            line.close();

            return recordedAudio;
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String demodulateAFSK(byte[] audioSignal, int sampleRate, int baudRate) {
        double frequencyMark = 1200;
        double frequencySpace = 2200;
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