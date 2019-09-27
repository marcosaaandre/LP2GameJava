package dev.learninggame.net.utils;

import java.io.IOException;
import java.util.TreeMap;

import dev.learninggame.net.packets.Packet03LargeObject;

/**
 * Classe usada para armazenar pacotes Packet03LargeObject a fim de 
 * 		retornar o objeto serializado
 * 
 *
 */

public class LargeObjectBuffer {
	TreeMap<Short, Packet03LargeObject> buffer;
	short totalParts = -1;

	public LargeObjectBuffer() {
		buffer = new TreeMap<Short, Packet03LargeObject>();
	}
	
	public void addPacket(Packet03LargeObject packet) {
		buffer.put(Short.valueOf(packet.getCurrentPart()), packet);
		
		if (totalParts == -1) {
			totalParts = packet.getTotalParts();
		}
	}
	
	/* Se o buffer já contém todas as partes do objeto */
	public boolean isFull() {
		return buffer.size() == totalParts;
	}
	
	/**
	 * 
	 * @return o objeto dentro do buffer, null se o buffer ainda não estiver cheio
	 * 
	 * (deveria dar exceção ao inves de return null)
	 */
	public Object getObject() {
		
		if (!isFull()) {
			return null;
		}
		
		byte[] serialized = new byte[Packet03LargeObject.bytesPerPart * totalParts]; // Objeto serializado
		Object desserialized = null;
		
		/* Tirar o cabeçalho de cada parte 
		 * e inserir os dados em serialized
		 */
		for (short part = 0; part < totalParts; part++) {
			byte[] data = buffer.get(part).getData();
			
			for (int i = part*Packet03LargeObject.bytesPerPart; 
					i < (part+1) * Packet03LargeObject.bytesPerPart;) {
				for (int j = 0; j < Packet03LargeObject.bytesPerPart;) {
					serialized[i++] = data[j++];
				}
			}
		}
		
		try {
			desserialized = Utils.objectFromBytes(serialized);
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (desserialized == null) {
		}
		
		return desserialized;
	}
}
