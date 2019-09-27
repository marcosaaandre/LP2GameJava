package dev.learninggame.net.packets;

import java.io.IOException;
import java.util.Arrays;

import dev.learninggame.entities.creatures.Player;
import dev.learninggame.net.utils.Utils;

/**
 * Envia o player
 *
 */
public class Packet06Player extends GamePacket {
	private static final int headerSize = 2;
	private Player player;
	
	public Packet06Player(byte[] packet) {
		super(PacketType.PLAYER);
		this.packet = packet;

		try {
			player = (Player) Utils.objectFromBytes(Arrays.copyOfRange(packet, getHeaderSize(), packet.length));
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Packet06Player(Player player) {
		super(PacketType.PLAYER);
		
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
