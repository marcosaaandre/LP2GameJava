package dev.learninggame.net.packets;
import java.io.IOException;

import dev.learninggame.net.Client;
import dev.learninggame.net.utils.Utils;

public class Packet01Login extends GamePacket {
	
	private Client client;
	private static final int headerSize = 2;

	public Packet01Login(Client client) {
		super(PacketType.LOGIN);
		this.client = client;
		
		try {
			packet = Utils.concatenate(packet, Utils.objectToBytes(client));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Packet01Login(byte[] packet) {
		super(PacketType.LOGIN);
		this.packet = packet;
		
		try {
			client = (Client) Utils.objectFromBytes(getData());
		} catch (ClassNotFoundException | IOException e) {				
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public int getHeaderSize() {
		return headerSize;
	}
	
	public Client getClient() {
		return client;
	}
	
	@Override
	public byte[] getPacket() {
		return packet;
	}

}
