package com.mycompany.app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class File {
    private String name;

    File(Path name) {
        this.name = name.toString();

    };

    // read the file (could use a better method in the future, but currently reading the whole file.)
    // ideas include reading chunks at a time and appending it to a temporary file. 
    public byte[] getData() {
        try {
            // consider using a buffered reader to reduce memory use and risk of crashing
            byte[] data = Files.readAllBytes(Paths.get(name));

            return data;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // turn the name of the file, including the extension into a byte array. 
    public byte[] getHeader() {
        return this.name.getBytes();
    }

    // turn a byte array into a decimal representation of that byte array
    public static String byteArr2String(byte[] bytes) {
        // use a stringBuilder to massively speed up runtimes
        StringBuilder dataStringBuilder = new StringBuilder(bytes.length * 8); // Preallocate capacity
    
        for (byte b : bytes) {
            int line = b & 0xFF;
            dataStringBuilder.append(Integer.toBinaryString(line | 0x100).substring(1));
        }
    
        return dataStringBuilder.toString();
    }
    
    // construct a byte array from a string. 
    public static byte[] string2ByteArr(String string) {
        byte[] data = new byte[string.length() / 8];

        for (int x = 0; x < string.length(); x += 8) {
            String shortstring = string.substring(x, x + 8);
            Integer intbyte = Integer.parseInt(shortstring, 2);
            byte bytebyte = intbyte.byteValue();
            data[x / 8] = bytebyte;
        }
        return data;
    }

}
