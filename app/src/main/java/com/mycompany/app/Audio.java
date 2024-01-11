// provided by CHATGPT

package com.mycompany.app;

    import javax.sound.sampled.*;

public class Audio {



    public static byte[] convertBinaryToAFSK(String binaryData, int sampleRate, int baudRate) {
        double frequencyMark = 1600; // Frequency for binary '0'
        double frequencySpace = 2500; // Frequency for binary '1'

        int samplesPerBit = sampleRate / baudRate;
        int numSamples = binaryData.length() * samplesPerBit;

        byte[] audioSignal = new byte[numSamples];

        for (int i = 0; i < binaryData.length(); i++) {
            char bit = binaryData.charAt(i);

            double frequency;
            if (bit == '0') {
                frequency = frequencyMark;
            } else {
                frequency = frequencySpace;
            }

            for (int j = 0; j < samplesPerBit; j++) {
                double angle = 2.0 * Math.PI * j / (double) sampleRate * frequency;
                audioSignal[i * samplesPerBit + j] = (byte) (Math.sin(angle) * 127);
            }
        }

        return audioSignal;
    }

    public static void playAudioSignal(byte[] audioSignal, int sampleRate) {
        try {
            AudioFormat format = new AudioFormat(sampleRate, 8, 1, true, false);
            SourceDataLine line = AudioSystem.getSourceDataLine(format);
            line.open(format);
            line.start();
            line.write(audioSignal, 0, audioSignal.length);
            line.drain();
            line.close();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    

    
    
}

