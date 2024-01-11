package com.mycompany.app;
    import javax.sound.sampled.*;
    import java.util.Arrays;

public class AudioToBinary {

    public static void main(String[] args) {
        
    }

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
}
