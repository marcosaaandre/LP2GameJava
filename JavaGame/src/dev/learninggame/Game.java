package dev.learninggame;

import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

import dev.learninggame.display.Display;
import dev.learninggame.entities.creatures.Player;
import dev.learninggame.gfx.Assets;
import dev.learninggame.gfx.Som;
import dev.learninggame.input.KeyManager;
import dev.learninggame.input.MouseManager;
import dev.learninggame.net.Client;
import dev.learninggame.net.Server;
import dev.learninggame.states.GameState;
import dev.learninggame.states.MenuState;
import dev.learninggame.states.State;

/* Classe de controle dos graficos do jogo*/
public class Game implements Runnable {
	/* 17: 60 ticks / sec */
	public static final int TICK_RATE = 17;

	private Display display;
	private int width, height;
	public String title;

	private BufferStrategy bs;
	private Graphics g;
	
	//States
	private State gameState;
	private State menuState;
	
	//Input
	private KeyManager keyManager;
	private MouseManager mouseManager;
	
	//sons
	// private Som sound, explosion;
	
	//Handlers
	private Handler handler;
	
	// Sockets
	private Client client;
	private Server server;
	
	// Thread
	private boolean running;
	private ScheduledExecutorService executor;
	
	public Game(String title, int width, int height){ 
		this.width = width;
		this.height = height;
		this.title = title;
		keyManager = new KeyManager();
		mouseManager = new MouseManager();
		executor = Executors.newSingleThreadScheduledExecutor();
	}
	
	private void init(){
		//Inicia o JFrame do Display
		display = new Display(title, width, height);
		display.getFrame().addKeyListener(keyManager);
		display.getFrame().addMouseListener(mouseManager);
		display.getFrame().addMouseMotionListener(mouseManager);
		display.getCanvas().addMouseListener(mouseManager);
		display.getCanvas().addMouseMotionListener(mouseManager);
		Assets.init();
		
//		sound = new Som("/sounds/stage.mp3");
//		sound.play();
		
		handler = new Handler(this);
		gameState = new GameState(handler);
		menuState = new MenuState(handler);
		State.setState(menuState);
	}
	
	private void tick(){
		keyManager.tick();
		
		if(State.getState() != null)
			State.getState().tick();
	}
	
	private void render(){
		bs = display.getCanvas().getBufferStrategy();
		if(bs == null){
			display.getCanvas().createBufferStrategy(3);
			return;
		}
		g = bs.getDrawGraphics();
		//Clear Screen
		g.clearRect(0, 0, width, height);
		//Draw Here!
		
		if(State.getState() != null)
			State.getState().render(g);
		
		//End Drawing!
		bs.show();
		g.dispose();
	}
	
	public void run(){
		try {
			tick();
			render();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public KeyManager getKeyManager(){
		return keyManager;
	}
	
	public MouseManager getMouseManager() {
		return mouseManager;
	}

	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
	
	public Client getClient() {
		return client;
	}
	
	public Server getServer() {
		return server;
	}
	
	public State getGameState() {
		return gameState;
	}
	
	public synchronized void start() {
		if (running) {
			return;
		}
		
		running = true;
		init();
		executor.scheduleWithFixedDelay(this, 0, Game.TICK_RATE, TimeUnit.MILLISECONDS);
		
		// Iniciando os sockets
		if (JOptionPane.showConfirmDialog(null, "Você quer ser o servidor?") == 0) {
			server = new Server(this);
			Thread tServer = new Thread(server);
			tServer.start();
		}
				
		String username = JOptionPane.showInputDialog("Digite seu nick: ");
		Client client = new Client(handler, username, "localhost");
		new Thread(client).start();
	}
	
}
