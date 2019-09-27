package dev.learninggame.net.packets;

import java.util.Arrays;

import dev.learninggame.net.utils.Utils;

public abstract class GamePacket {
	
	/* Identificação para o tipo do pacote*/
	protected PacketType packetType;
	
	/* Representação do pacote em formato byte[] para ser enviado pela rede */
	protected byte[] packet;		
	
	public GamePacket(PacketType packetType) {
		this.packetType = packetType;
		packet = Utils.shortToBytes(getTypeId());
	}
	
	// public abstract void writeData(Client client);
	// public abstract void writeData(Server server);
	public PacketType getPacketType() {
		return packetType;
	}
	
	public short getTypeId() {
		return packetType.getId();
	}
	
	/* Retorna o pacote em seu formato byte[] */
	public byte[] getPacket() {
		return packet;
	}
	
	/* Retorna o conteúdo do pacote em bytes (sem o cabeçalho) */
	// public abstract byte[] getData();
	
	/**
	 * 
	 * @return Tamanho do cabeçalho do pacote
	 */
	public abstract int getHeaderSize();
	
	/**
	 * 
	 * @return Conteúdo do pacote sem o cabeçalho
	 */
	public byte[] getData() {
		return Arrays.copyOfRange(packet, getHeaderSize(), packet.length);
	}
}
