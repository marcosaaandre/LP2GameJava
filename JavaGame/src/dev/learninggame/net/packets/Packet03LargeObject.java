package dev.learninggame.net.packets;

import java.util.Arrays;

import dev.learninggame.net.utils.Utils;

/**
 * Formato do pacote:
 *     bytes 0 e 1: short PacketType tipo do pacote
 *     bytes 
 *     bytes 2 e 3: short totalParts (em quantas partes foi dividido). 
 *     bytes 4 e 5: short currerntPart (número da parte atual, a contar a partir de zero)
 *         (cada parte tem bytesPerPart de tamanho)
 *     a partir do byte 6: parte do objeto serializado
 * 
 * @author andre
 *
 */

public abstract class Packet03LargeObject extends GamePacket {

	public static final short bytesPerPart = 1000;
	private static final int headerSize = 6;
	short totalParts;
	short currentPart;
	
	public Packet03LargeObject(byte[] packet, PacketType packetType) {
		
		super(packetType);
		totalParts = Utils.bytesToShort(Arrays.copyOfRange(packet, 2, 4));
		currentPart = Utils.bytesToShort(Arrays.copyOfRange(packet, 4, 6));
		this.packet = packet;
	}
	
	public Packet03LargeObject(byte[] data, PacketType packetType, short totalParts, short currentPart) {
		super(packetType);
		
		this.totalParts = totalParts;
		this.currentPart = currentPart;
		
		packet = Utils.concatenate(Utils.shortToBytes(getTypeId()), 
				Utils.shortToBytes(totalParts),
				Utils.shortToBytes(currentPart),
				data);
	}

	@Override
	public int getHeaderSize() {
		return headerSize;
	}
	
	public short getCurrentPart() {
		return currentPart;
	}
	
	public short getTotalParts() {
		return totalParts;
	}
	
}
