package dev.learninggame.net.packets;

public class Packet05RequestWorld extends GamePacket {

	/*
	 * Pacote simples para o client requisitar o objeto world do server
	 */
	
	private static final int headerSize = 2;
	public Packet05RequestWorld() {
		super(PacketType.REQUEST_WORLD);
	}

	@Override
	public int getHeaderSize() {
		return headerSize;
	}

}
