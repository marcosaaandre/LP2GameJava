package dev.learninggame.net.World;

import java.io.IOException;
import java.util.ArrayList;

import dev.learninggame.net.Server;
import dev.learninggame.net.packets.Packet03LargeObject;
import dev.learninggame.net.packets.Packet04World;
import dev.learninggame.net.utils.Utils;
import dev.learninggame.worlds.World;
/**
 * Classe utilizada para serializar um world a fim de poder enviá-lo pela rede.
 * 
 *
 */
public class NetWorld implements Runnable {
	
	/* Contador para o número de instâncias dessa classe */
	private static long count; 
    private static final Object countLock = new Object();
	
	/* Identificador para este World */ 
	private long worldId;
	
	private boolean isReady;
	
	private World world;
	private ArrayList<byte[]> serializedWorld;
	private ArrayList<Packet04World> packets;
	
	public NetWorld(World world) {
		this.world = world;
		isReady = false;
		
		synchronized (countLock) {
			worldId = count;
			count++;
		}
	}
	
	public boolean isReady() {
		return isReady;
	}
	
	public long getId() {
		return worldId;
	}
	
	public World getWorld() {
		return world;
	}

	@Override
	public void run() {
		serializeWorld();
		buildPackets();
		
		isReady = true;
	}
	
	private void serializeWorld() {
		try {
			serializedWorld = Utils.sliceBytes(Utils.objectToBytes(world), Packet03LargeObject.bytesPerPart);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void buildPackets() {
		packets = new ArrayList<Packet04World>();
		
		for (int part = 0; part < serializedWorld.size(); part++) {
			Packet04World packet = new Packet04World(
					serializedWorld.get(part), worldId, (short)serializedWorld.size(), (short)part);
			packets.add(packet);
		}
	}
	
	public ArrayList<Packet04World> getPackets() {
		return packets;
	}
}
