package com.mycompany.app;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Hello world!
 *
 */
public class App {
    // turn a file into a string representation of all it's bytes. 
    public static String getRawData(String filename) {
        Path path = Paths.get(filename);
        // Path path = Paths.get("test.txt");
        File file = new File(path);
        String rawData = File.byteArr2String(file.getHeader()) + 11111111 + File.byteArr2String(file.getData());
        // System.out.println(rawData);
        return rawData;
        
    }

    public static void writeFile(String rawData) {
        byte[] bytes = File.string2ByteArr(rawData);
        String name = "";
        int dataStart = 0;
        byte testCase = File.string2ByteArr("11111111")[0];
        for (int x = 0; x < bytes.length; x++) {
            if (bytes[x] == testCase) {
                name = new String(bytes, 0, x, StandardCharsets.UTF_8);
                dataStart = x + 1; // +1 for header marker
                break;
            }
        }
        byte[] data = Arrays.copyOfRange(bytes, dataStart, bytes.length);
        try {
            Files.createFile(Paths.get("2" + name));
            Files.write(Paths.get("2" + name), data);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws FileNotFoundException {
        long startTime = System.nanoTime();
        String data = getRawData("t.txt");

        long readTime = System.nanoTime();
        long totalTime = readTime - startTime;
        // System.err.println(data);

        int sampleRate = 44100; // Sample rate in Hz
        int baudRate = 1200; // Baud rate in bits per second

        byte[] audioSignal = Audio.convertBinaryToAFSK(data, sampleRate, baudRate);
        // Audio.playAudioSignal(audioSignal, sampleRate);

        // int sampleRate = 44100;
        // int baudRate = 1200;
        int durationInSeconds = 15; // Adjust this based on the expected duration of your recorded signal

        // byte[] recordedAudio = AudioToBinary.recordAudio(sampleRate, durationInSeconds);
        System.out.println("Recording complete!");
        // byte[] testAudio = AudioToBinary.readWavFile("test.wav");

        // String binaryData = AudioToBinary.demodulateAFSK(recordedAudio, sampleRate, baudRate);
        
        String binaryData = AudioToBinary.demodulateAFSK(audioSignal, sampleRate, baudRate);
        // System.out.println("Decoded Binary Data: " + binaryData);
        writeFile(binaryData);

        // writeFile(data);

 
    }
}
