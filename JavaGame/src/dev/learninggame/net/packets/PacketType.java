package dev.learninggame.net.packets;

public enum PacketType {
	INVALID((short)-1), LOGIN((short) 01), DISCONNECT((short) 02), 
	LARGE_OBJECT((short )03), WORLD((short) 04), 
	REQUEST_WORLD((short) 05), PLAYER((short) 06),
	PLANT_BOMB((short) 07);
	
	/* Identificação para o tipo do pacote*/
	private short typeId;	
	
	private PacketType(short typeId) {
		this.typeId = typeId;
	}
	
	public short getId() {
		return typeId;
	}
	
	public static PacketType fromShort(short id) {
		for (PacketType pt : PacketType.values()) {
				if (pt.getId() == id) {
					return pt;
				}
		}
		
		return INVALID;
	}
}
