package dev.learninggame.net.packets;

public enum PacketType {
	INVALID((short)-1), LOGIN((short) 1), DISCONNECT((short) 2), 
	LARGE_OBJECT((short ) 3), WORLD((short) 4), 
	REQUEST_WORLD((short) 5), PLAYER((short) 6),
	PLANT_BOMB((short) 7), GAME_OVER((short) 8);
	
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
