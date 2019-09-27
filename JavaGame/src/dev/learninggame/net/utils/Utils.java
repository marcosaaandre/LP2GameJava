package dev.learninggame.net.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public class Utils {
	public static byte[] objectToBytes(Object object) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bos);
		
		out.writeObject(object);
		return bos.toByteArray();
	}
	
	public static Object objectFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInputStream in = new ObjectInputStream(bis);
		
		return in.readObject();
	}
	
	/**
	 * Divide a array de bytes original em várias arrays de bytes 
	 * 		de no máximo maxSize de tamanho.
	 * @param original
	 * @param maxSize
	 * @return ArrayList contendo cada array de bytes
	 */
	public static ArrayList<byte[]> sliceBytes(byte[] original, int maxSize) {
		// int arrays = original.length / maxSize 
		// 		+ (original.length % maxSize == 0 ? 0 : 1);
		
		ArrayList<byte[]> r = new ArrayList<byte[]>();
		for (int i = 0; i < original.length; i += maxSize) {
			r.add(Arrays.copyOfRange(original, i, i + maxSize));
		}
		
		return r;
	}
	
	public static byte[] concatenate(byte[]...arrays) {
		int length = 0;
		for (byte[] array : arrays) {
			length += array.length;
		}
		
		byte[] r = new byte[length];
		
		int rIndex = 0;
		for (byte[] array : arrays) {
			System.arraycopy(array, 0, r, rIndex, array.length);
			rIndex += array.length;
		}
		
		return r;
	}

    public static byte[] shortToBytes(short value) {
    	ByteBuffer buffer = ByteBuffer.allocate(2);
    	buffer.putShort(value);
    	return buffer.array();
    }

	public static short bytesToShort(byte[] bytes) {
    	ByteBuffer buffer = ByteBuffer.allocate(4);
    	buffer.put(bytes);
    	buffer.flip();
    	return buffer.getShort();
	}
	
	public static byte[] intToBytes(int value) {
    	ByteBuffer buffer = ByteBuffer.allocate(4);
    	buffer.putInt(value);
    	return buffer.array();
    }

	public static int bytesToInt(byte[] bytes) {
    	ByteBuffer buffer = ByteBuffer.allocate(4);
    	buffer.put(bytes);
    	buffer.flip();
    	return buffer.getInt();
	}
	
	public static byte[] longToBytes(long value) {
    	ByteBuffer buffer = ByteBuffer.allocate(8);
    	buffer.putLong(value);
    	return buffer.array();
    }

	public static Long bytesToLong(byte[] bytes) {
    	ByteBuffer buffer = ByteBuffer.allocate(8);
    	buffer.put(bytes);
    	buffer.flip();
    	return buffer.getLong();
	}
	
	public static byte[] floatToBytes(Float value) {
    	ByteBuffer buffer = ByteBuffer.allocate(4);
    	buffer.putFloat(value);
    	return buffer.array();
    }

	public static Float bytesToFloat(byte[] bytes) {
    	ByteBuffer buffer = ByteBuffer.allocate(4);
    	buffer.put(bytes);
    	buffer.flip();
    	return buffer.getFloat();
	}
	
	
	
}
