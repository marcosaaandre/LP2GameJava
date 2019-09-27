package dev.learninggame.net;

import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JOptionPane;

import dev.learninggame.Handler;
import dev.learninggame.entities.Bomb;
import dev.learninggame.entities.EntityManager;
import dev.learninggame.entities.creatures.Player;
import dev.learninggame.net.World.WorldBuffer;
import dev.learninggame.net.packets.GamePacket;
import dev.learninggame.net.packets.Packet01Login;
import dev.learninggame.net.packets.Packet04World;
import dev.learninggame.net.packets.Packet08GameOver;
import dev.learninggame.net.packets.PacketType;
import dev.learninggame.net.utils.Utils;
import dev.learninggame.worlds.World;

public class Client implements Runnable, Serializable {
	
	/* Identificação para os tipos de players */
	public static final int PLAYER_BOY = 1; 
	public static final int PLAYER_GIRL = 2;
	
	/* Identificação para este player */
	private int playerType;
	
	/* Se ja perdeu o jogo */
	private boolean hasLost;

	private InetAddress serverIp;
	private InetAddress ip;
	private int port;
	private transient DatagramSocket socket;
	
	private transient Player player;
	private transient Handler handler;
	private String username;
	
	private transient WorldBuffer worldBuffer;

	/* Clients que estão conectados no jogo */
	private transient HashSet<Client> connectedPlayers = new HashSet<Client>();

	
	public Client(Handler handler, int playerType, String username, String serverIp) {
		this.username = username;
		this.playerType = playerType;
		this.handler = handler;
		this.worldBuffer = new WorldBuffer();
		
		try {
			socket = new DatagramSocket();
			this.serverIp = InetAddress.getByName(serverIp);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override 
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		
		if (!(object instanceof Client)) {
			return false;
		}
		Client otherClient = (Client) object;
		
		return this.getUsername().trim().equalsIgnoreCase(
				otherClient.getUsername().trim());
	}
	
	@Override
	public int hashCode() {
		return getUsername().trim().toLowerCase().hashCode();
	}
	
//	public Client(String username, Player player, String ip, String serverIp) {
//		this(username, player, serverIp);
//		try {
//			this.ip = InetAddress.getByName(ip);
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	public String getUsername()	{
		return username;	
	}
	
	public InetAddress getIp() {
		return ip;
	}
	
	public void setIp(InetAddress newIp) {
		ip = newIp;
	}
	
	public int getPort() {
		return port;
	}
	
	public void setPort(int newPort) {
		port = newPort;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public int getPlayerType() {
		return playerType;
	}
	
	public void setHasLost(boolean hasLost) {
		this.hasLost = hasLost;
	}
	
	public boolean hasLost() {
		return hasLost;
	}
	
	public void run() {
		login();
		
		while (true) {
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			
			try {
				socket.receive(packet);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			parsePacket(packet);
		}
	}
	
	public void sendData(byte[] data) {
		DatagramPacket packet = new DatagramPacket(data, data.length, serverIp, Server.PORT);
		
		try {
			socket.send(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendPacket(GamePacket packet) {
		sendData(packet.getPacket());
	}
	
	private void parsePacket(DatagramPacket packet) {
		byte[] data = packet.getData();
		short typeId = Utils.bytesToShort(Arrays.copyOfRange(data, 0, 2));
		PacketType packetType = PacketType.fromShort(typeId);
		
		switch (packetType) {
		case LOGIN:
			addConnection(new Packet01Login(data));
			break;
		case DISCONNECT:
			break;
		case LARGE_OBJECT:
			break;
		case WORLD:
			fetchWorld(new Packet04World(data));
			break;
		case GAME_OVER:
			endGame(new Packet08GameOver(data));
			break;
		case INVALID:
			break;
		default:
			break;
		}
	}
	
	/**
	 * Manda o pacote pro worldBuffer
	 * atualiza o world se necessário
	 * @return
	 */
	private void fetchWorld(Packet04World packet) {		
		worldBuffer.addPacket(packet);
		
		World world = worldBuffer.getLatestWorld();
		if (world != null) {
			updateWorld(world);
			
//			synchronized (handler.getWorld()) {
//				handler.setWorld(world);
//				world.setHandler(handler);
//				parseWorld();
//			}
			
//			if (getUsername().equals("client")) {
//				System.out.println("world atualizado " + getUsername());
//			}
		}
	}
	
	private void updateWorld(World newWorld) {
		EntityManager entityManager = newWorld.getEntityManager();
		entityManager.setHandlerForAll(handler);
		newWorld.setHandler(handler);
		
		CopyOnWriteArrayList<Player> players = entityManager.getPlayers();
		for (Player player : players) {
			player.updateFrames();
			if (player.getUsername().equals(getUsername())) {
				player.setClient(this);
			}
			// System.out.println("NenhumPlayer: " + (nenhumPlayer ? "true" : "false"));
		}
		CopyOnWriteArrayList<Bomb> bombs = entityManager.getBombs();
		for (Bomb bomb : bombs) {
			bomb.updateFrames();
			//System.out.println("bomb");
		}
		handler.setWorld(newWorld);
		
		
	}

	private void addConnection(Packet01Login loginPacket) {
		Client newClient = loginPacket.getClient();
		if (!connectedPlayers.contains(newClient)) {
			connectedPlayers.add(newClient);
			System.out.println(newClient.getUsername() + " has joined the game");
		}
	}
	
	private void login() {
		sendPacket(new Packet01Login(this));
	}
	
	private void endGame(Packet08GameOver packet) {
		String winner = packet.getWinner();
		
		JOptionPane.showMessageDialog(null, "Fim de jogo: "
				+ winner + " Venceu!!!");
	}
}
