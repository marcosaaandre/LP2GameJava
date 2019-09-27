package dev.learninggame.net.packets;

import java.io.IOException;
import java.util.Arrays;

import dev.learninggame.entities.Bomb;
import dev.learninggame.entities.creatures.Player;
import dev.learninggame.net.utils.Utils;

/**
 * Envia a bomba plantada para o server.
 *
 */
public class Packet07PlantBomb extends GamePacket {
	private static final int headerSize = 2;
	private Bomb bomb;
	
	public Packet07PlantBomb(byte[] packet) {
		super(PacketType.PLANT_BOMB);
		this.packet = packet;
		
		try {
			bomb = (Bomb) Utils.objectFromBytes(Arrays.copyOfRange(packet, getHeaderSize(), packet.length));
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Packet07PlantBomb(Bomb bomb) {
		super(PacketType.PLANT_BOMB);
		
		this.bomb = bomb;
		
		try {
			packet = Utils.concatenate(packet, Utils.objectToBytes(bomb));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Bomb getBomb() {
		return bomb;
	}

	@Override
	public int getHeaderSize() {
		return headerSize;
	}

}
