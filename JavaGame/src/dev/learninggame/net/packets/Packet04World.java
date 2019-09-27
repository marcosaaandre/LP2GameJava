package dev.learninggame.net.packets;

import java.nio.ByteBuffer;
import java.util.Arrays;

import dev.learninggame.net.utils.Utils;

/**
 * Formato do pacote:
 *     bytes 0 e 1: short PacketType tipo do pacote
 *     bytes 
 *     bytes 2 e 3: short totalParts (em quantas partes foi dividido). 
 *     bytes 4 e 5: short currerntPart (número da parte atual, a contar a partir de zero)
 *         (cada parte tem bytesPerPart de tamanho)
 *     bytes 6 a 13: long worldId (Contador indicando quantos worlds já foram enviados pelo servidor
 *     				e identificando este world)
 *     a partir do byte 14: parte do world serializado
 * 
 * @author andre
 *
 */

public class Packet04World extends Packet03LargeObject {
	
	private long worldId;

	private static final int headerSize = 14;

	public Packet04World(byte[] packet) {
		super(packet, PacketType.WORLD);
		worldId = Utils.bytesToLong(Arrays.copyOfRange(packet, 6, 14));
		//updatePacketType();
	}
	
	public Packet04World(byte[] data, long worldId, short parts, short currentPart) {
		super(data, PacketType.WORLD, parts, currentPart);
		this.worldId = worldId;
		
		/* Inserindo o worldId no array de bytes */
		byte[] newPacket = Utils.concatenate(Arrays.copyOfRange(packet, 0, super.getHeaderSize()), 
				Utils.longToBytes(worldId), data);
		
		packet = newPacket;
	}
	
	public int getHeaderSize() {
		return headerSize;
	}

	public long getId() {
		return worldId;
	}

//	public void updatePacketType() {
//		packetType = PacketType.WORLD;
//		
//		/* atualizar 2 primeiros bytes */
//		byte[] aux = Utils.shortToBytes(getTypeId());
//		packet[0] = aux[0];
//		packet[1] = aux[1];
//	}
}
