package dev.learninggame.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import dev.learninggame.Game;
import dev.learninggame.Handler;
import dev.learninggame.entities.Bomb;
import dev.learninggame.entities.EntityManager;
import dev.learninggame.entities.creatures.Player;
import dev.learninggame.net.World.NetWorld;
import dev.learninggame.net.packets.GamePacket;
import dev.learninggame.net.packets.Packet01Login;
import dev.learninggame.net.packets.Packet04World;
import dev.learninggame.net.packets.Packet05RequestWorld;
import dev.learninggame.net.packets.Packet06Player;
import dev.learninggame.net.packets.Packet07PlantBomb;
import dev.learninggame.net.packets.PacketType;
import dev.learninggame.net.utils.Utils;
import dev.learninggame.worlds.World;

public class Server implements Runnable {
	public static final int PORT = 1337;
	
	/* Delay em milisegundos entre cada atualização 
	 * 40: 25/sec
	 */
	public static final int TICK_RATE = 40;	
	
	private DatagramSocket socket;
	private Handler handler;
	private World world;
	private HashSet<Client> connectedClients;
	
	private NetWorld currentWorld;
	private NetWorld nextWorld;
	private ScheduledExecutorService worldManager;
	
	public Server(Game game) {
		handler = new Handler(game);
		world = new World(this.handler, "res/worlds/world1.txt");
		handler.setWorld(world);
		world.setHandler(this.handler);
		
		connectedClients = new HashSet<Client>();
		currentWorld = null;
		nextWorld = null;
		
		try {
			this.socket = new DatagramSocket(Server.PORT);	// listening
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		worldManager = Executors.newSingleThreadScheduledExecutor();
		
		/* Atualiza o world a cada TICK_RATE e manda pros clients */
		worldManager.scheduleWithFixedDelay(new Runnable() {
		    	@Override
		    	public void run() {
		    		try {
		    			tickWorld(); 
		    		} catch (Exception e) {
		    			e.printStackTrace();
		    		}
		    		nextWorld = new NetWorld(world);
		    		nextWorld.run();
		    		currentWorld = nextWorld;
		    		
			    	sendCurrentWorldToClients();
		    	}
		    }, 0, Server.TICK_RATE, TimeUnit.MILLISECONDS); 
	}
	
	public void run() {
		while (true) {	
			byte[] receivedData = new byte[1024];
			DatagramPacket packet = new DatagramPacket(receivedData, receivedData.length);
						
			try {
				socket.receive(packet);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			parsePacket(packet);
			
			// receivedData = Arrays.copyOfRange(receivedData, 0, packet.getLength());		
		}
	}
	
	/**
	 * Verifica se o thread que está executando o método está
	 *     executando o servidor
	 * 
	 * @param threadId - id to thread que está chamando o método
	 * @return true se o thread que está chamando o método é o servidor
	 */
	
	/**
	 * Manda o byte[] data para o client especificado.
	 * 
	 * @param data
	 * @param clientIP
	 * @param clientPort
	 */
	public void sendData(byte[] data, InetAddress clientIP, int clientPort) {
		DatagramPacket packet = new DatagramPacket(data, data.length, clientIP, clientPort);
		
		try {
			socket.send(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Manda o byte[] data para todos os clients conectados.
	 * 
	 * @param data
	 */
	public void sendDataToAllClients(byte[] data) {
		synchronized (connectedClients) {
			for (Client client : connectedClients) {
				sendData(data, client.getIp(), client.getPort());
			}
		}
	}
	
	
	
	/**
	 * Analisa o pacote e o jogo de acordo com o pacote recebido.
	 *
	 * @param packet - pacote a ser analisado
	 */
	public void parsePacket(DatagramPacket packet) {
		byte[] data = packet.getData();
		short typeId = Utils.bytesToShort(Arrays.copyOfRange(data, 0, 2));
		PacketType packetType = PacketType.fromShort(typeId);
		InetAddress sourceIp = packet.getAddress();
		int sourcePort = packet.getPort();
		
		/* Converte o pacote UDP para um GamePacket e chama o método parsePacket
		 * tendo como argumento esse GamePacket.
		 */
		
		switch (packetType) {
		case LOGIN:
			addConnection(new Packet01Login(data), sourceIp, sourcePort);
			break;
		case REQUEST_WORLD:
			sendWorld(sourceIp, sourcePort);
			break;
		case PLAYER:
			updatePlayer(new Packet06Player(data));
			break;
		case PLANT_BOMB:
			Client client = findClient(sourceIp, sourcePort);
			if (client != null) {
				plantBomb(new Packet07PlantBomb(data), client.getPlayer());
			}
			break;
		default:
			break;
		}
	}
	
	
	private void addConnection(Packet01Login loginPacket, InetAddress sourceIp, int sourcePort) {
		Client client = loginPacket.getClient();
		client.setIp(sourceIp);
		client.setPort(sourcePort);		
		if (!connectedClients.contains(client)) {
			connectedClients.add(client);
			System.out.println(client.getUsername() + " has connected");
		}
		
		// handler.getWorld().getEntityManager().addEntity(client.getPlayer());
		
		/* Comunica o login para todos os clients */
		sendDataToAllClients(loginPacket.getPacket());
		
		/* Manda o world para o client que acabou de logar */
		sendWorld(sourceIp, sourcePort);
		
		givePlayer(client);
	}
	
	/**
	 * Dá um player para o client
	 * @param client
	 */
	private void givePlayer(Client client) {
		Player player = new Player(client.getUsername(), null, world.getSpawnXBoy(), world.getSpawnYBoy());
		synchronized (world) {
			world.getEntityManager().addEntity(player);
		}
		client.setPlayer(player);
		
//		if (!world.getEntityManager().getEntities().isEmpty()) {
//			System.out.println("player adicionado");
//		}
	}
	
	private void sendWorld(InetAddress clientIp, int clientPort) {
		if (currentWorld == null) {
			return;
		}
		
		//System.out.println("sendWorld");
		
		ArrayList<Packet04World> packets = currentWorld.getPackets();
		
		for(GamePacket p : packets) {
			sendData(p.getPacket(), clientIp, clientPort);
		}
		
	}
	
	private void sendCurrentWorldToClients() {
		synchronized (connectedClients) {
			for (Client c : connectedClients) {
				sendWorld(c.getIp(), c.getPort());
			}
		}
	}
	
	/**
	 * Atualiza o estado do player que moveu
	 */
	public void updatePlayer(Packet06Player packet) {
		Player player = packet.getPlayer();
		String username = player.getUsername();
		
		synchronized (connectedClients) {
			for (Client c : connectedClients) {
				if (c.getUsername().equals(username)) {
					c.setPlayer(player);
				}
			}
		}
		
		EntityManager entityManager = world.getEntityManager();
		synchronized (world) {
			CopyOnWriteArrayList<Player> players = entityManager.getPlayers();
			for (Player p : players) {
				if (p.equals(player)) {
					//System.out.println("player atualizado");
					entityManager.removeEntity(p);
					entityManager.addEntity(player);
				}
			}
		}
	}
	
	/**
	 * 
	 * @return client com o correspondente ip e porta
	 * 		null se nao achou
	 */
	private Client findClient(InetAddress clientIp, int clientPort) {
		synchronized (connectedClients) {
			for (Client c : connectedClients) {
				if (c.getPort() == clientPort && c.getIp().equals(clientIp)) {
					return c;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Planta uma bomba no world
	 * @param player - Player que está plantando a bomba
	 */
	public void plantBomb(Packet07PlantBomb packet, Player player) {
		Bomb bomb = packet.getBomb();
		bomb.updateFrames();
		bomb.setHandler(handler);

		synchronized (world) {
			world.getEntityManager().addEntity(bomb);
		}
	}
	
	private void tickWorld() {
		synchronized (world) {
			world.tick();
		}
	}
}
