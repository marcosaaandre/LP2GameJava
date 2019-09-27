package dev.learninggame.net.packets;

import java.io.IOException;
import java.util.Arrays;

import dev.learninggame.entities.creatures.Player;
import dev.learninggame.net.utils.Utils;

/**
 * Packet usado para mandar o nome do jogador vencedor
 * para que os clients deem game-over
 *
 */
public class Packet08GameOver extends GamePacket {
	public static final int headerSize = 2;
	
	String winner;

	public Packet08GameOver(byte[] packet) {
		super(PacketType.GAME_OVER);
		this.packet = packet;
		
		try {
			winner = (String) Utils.objectFromBytes(Arrays.copyOfRange(packet, getHeaderSize(), packet.length));
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public Packet08GameOver(String winnerName) {
		super(PacketType.GAME_OVER);
		this.winner = winnerName;
		
		try {
			packet = Utils.concatenate(packet, Utils.objectToBytes(winner));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getHeaderSize() {
		return headerSize;
	}
	
	public String getWinner() {
		return winner;
	}

}
