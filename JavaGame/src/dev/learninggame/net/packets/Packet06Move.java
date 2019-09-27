package dev.learninggame.net.packets;

import java.io.IOException;
import java.util.Arrays;

import dev.learninggame.entities.creatures.Player;
import dev.learninggame.net.utils.Utils;

/**
 * Envia o player
 *
 */
public class Packet06Move extends GamePacket {
	private static final int headerSize = 2;
	private Player player;
	
	public Packet06Move(byte[] packet) {
		super(PacketType.MOVE);
		this.packet = packet;

		try {
			player = (Player) Utils.objectFromBytes(Arrays.copyOfRange(packet, getHeaderSize(), packet.length));
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Packet06Move(Player player) {
		super(PacketType.MOVE);
		
		try {
			packet = Utils.concatenate(packet, Utils.objectToBytes(player));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Player getPlayer() {
		return player;
	}

	@Override
	public int getHeaderSize() {
		return headerSize;
	}

}
