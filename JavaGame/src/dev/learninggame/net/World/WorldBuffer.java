package dev.learninggame.net.World;

import java.util.TreeMap;

import dev.learninggame.net.packets.Packet04World;
import dev.learninggame.net.utils.LargeObjectBuffer;
import dev.learninggame.worlds.World;

/**
 * 
 * Guarda os packet04world para retornar um world desserializado
 *
 */

/*
 * map com varios (max 3?) worlds
 * map de map
 * 
 * quando o client chamar o world mais novo, remover o mais antigo
 */

public class WorldBuffer  {
	TreeMap<Long, LargeObjectBuffer> worlds;
	long currentWorld;	/* id do último world enviado */
	
	public WorldBuffer() {
		worlds = new TreeMap<Long, LargeObjectBuffer>();
		currentWorld = -1;
	}
	
	public void addPacket(Packet04World packet) {
		Long worldId = packet.getId();
		
		if (!worlds.containsKey(worldId)) {
			worlds.put(worldId, new LargeObjectBuffer());
		}
		
		LargeObjectBuffer buffer = worlds.get(worldId);
		buffer.addPacket(packet);
		return;
	}
	
	/**
	 * Mudar pra jogar exceção ao invés de retornar null
	 * @return	null se não há nenhum world novo no buffer
	 */
	public World getLatestWorld() {
		long lastKey = worlds.lastKey();
		
		if (worlds.size() > 2) {
			worlds.remove(worlds.firstKey());
		}
		if (lastKey < currentWorld) {
			return null;
		}
		
		currentWorld = lastKey;
		return (World) worlds.get(worlds.lastKey()).getObject();
	}
	
	/**
	 * 
	 * @return Identificador para o world que está sendo armazenado
	 */
	//public boolean getWorldId;
}
