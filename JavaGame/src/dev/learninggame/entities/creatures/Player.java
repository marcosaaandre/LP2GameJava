package dev.learninggame.entities.creatures;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.Serializable;

import dev.learninggame.Handler;
import dev.learninggame.entities.Bomb;
import dev.learninggame.entities.Entity;
import dev.learninggame.entities.Fire;
import dev.learninggame.gfx.Animation;
import dev.learninggame.gfx.Assets;
import dev.learninggame.net.Client;
import dev.learninggame.net.Server;
import dev.learninggame.net.packets.Packet06Player;
import dev.learninggame.net.packets.Packet07PlantBomb;

public class Player extends Creature implements Serializable, Runnable{
	
	//Atributos
	protected int maxBombs;
	private int nOfBombs;
	
	//Animations
	protected Animation animUp;
	protected Animation animDown;
	protected Animation animLeft;
	protected Animation animRight;
	protected long tempoInicio;
	protected long tempoFinal;
	protected boolean solid;
	
	//Multiplayer
	private String username;
	private transient Client client;

	@Override
	public void run() {
		System.out.println("player iniciado");
	}
	
	public Player(String username, Handler handler, float x, float y) {	
		super(handler, x, y, Creature.DEFAULT_CREATURE_WIDTH, Creature.DEFAULT_CREATURE_HEIGHT);
		this.username = username;
		maxBombs = 9999;
		
		//ajuste da posicao da hitbox
		bounds.x = 34;
		bounds.y = 38;
		//largura e comprimento da hitbox
		bounds.width = 30;
		bounds.height = 35;
		
		//Animations
		animUp = new Animation(180, null);
		animDown = new Animation(180, null);
		animLeft = new Animation(150, null);
		animRight = new Animation(150, null);
		updateFrames();
		
		//Contagem do tempo
		tempoInicio = 0;
		tempoFinal = System.currentTimeMillis();
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setClient(Client client) {
		this.client = client;
	}
	
	@Override 
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		
		if (!(object instanceof Player)) {
			return false;
		}
		Player otherPlayer = (Player) object;
		
		if (getUsername() == null) {
			return false;
		}
		
		return this.getUsername().trim().equalsIgnoreCase(
				otherPlayer.getUsername().trim());
	}
	
	@Override
	public int hashCode() {
		return getUsername().trim().toLowerCase().hashCode();
	}
	
//	public void setHandler(Handler handler) {
//		this.handler = handler;
//	}

	public void updateFrames() {
		//Animations
		animUp.setFrames(Assets.player_up);
		animDown.setFrames(Assets.player_down);
		animLeft.setFrames(Assets.player_left);
		animRight.setFrames(Assets.player_right);
		
	}

	protected void setSolidity(boolean b) {
		this.solid = b;
	}

	@Override
	public void tick() {
		/* Não é o player do client */
		if (client == null) {
			return;
		}
		
		//Animations
		animUp.tick();
		animDown.tick();
		animRight.tick();
		animLeft.tick();
		
		//Tempo
		tempoFinal = System.currentTimeMillis();
		
		//Attack
		checkHurts();
		
		//Movement
		getInput();
		move();
	}
	
	@Override
	public void move() {
		super.move();
		if (client != null && client.getUsername().equals(username)) {
			Packet06Player packet = new Packet06Player(this);
			client.sendPacket(packet);
		}
	}
	
	//Bomb Attacks
	private void checkHurts() {		
		if(tempoFinal - currentHurt > 1000 && handler.getWorld().hasFire(getCurrentTileX(x), getCurrentTileY(y))) {
			hurt(25);
			currentHurt = tempoFinal;
		}
	}
	
	/* Cuida de terminar o jogo quando o player morrer */
	@Override
	public void die() {		
		Server server = handler.getGame().getServer();
		if (client != null && server != null) {
			server.setLoser(getUsername());
		}
	}
	
	public boolean isSolid() {
		return solid;
	}
	
	protected void getInput() {
		xMove = 0; //variaveis declaradas na classe Creature
		yMove = 0;
		
		if(handler.getKeyManager().up)
			yMove = -speed;
		if(handler.getKeyManager().down)
			yMove = speed;
		if(handler.getKeyManager().left)
			xMove = -speed;
		if(handler.getKeyManager().right)
			xMove = speed;
		if(handler.getKeyManager().bombBoy) {
			if(tempoFinal - tempoInicio > 250) {
				installBomb();
				tempoInicio = tempoFinal;
			}
		}
			
	
	}
	
	/*
	 * @author Nathan Rodrigo
	 * Planta bomba no mapa
	 */
	public void installBomb() {
		if(!handler.getWorld().hasBomb(getCurrentTileX(x), getCurrentTileY(y+15)) 
				&& nOfBombs < maxBombs) {
			Bomb bomba = new Bomb(handler, (int)x, (int)y);
			//handler.getWorld().getEntityManager().addEntity(bomba);
			client.sendPacket(new Packet07PlantBomb(bomba));
			nOfBombs++;
		}
	}
	
	public void fireHurt() {
		
	}
	
	@Override
	public void render(Graphics g) {

		g.drawImage(getCurrentAnimation(), (int)(x), (int)(y), width, height, null);
		
		/*g.setColor(Color.yellow); // Testar hit box
		g.fillRect((int) (x + bounds.x), (int)(y + bounds.y), bounds.width, bounds.height);*/
	}

	protected BufferedImage getCurrentAnimation() {
		if(xMove > 0)
			return animRight.getCurrentFrame();
		if(xMove < 0)
			return animLeft.getCurrentFrame();
		if(yMove > 0)
			return animDown.getCurrentFrame();
		if(yMove < 0)
			return animUp.getCurrentFrame();
		return Assets.player;
	} 

	public int getnOfBombs() {
		return nOfBombs;
	}

	public void addnOfBombs() {
		this.nOfBombs--;
	}
	
	public int getMaxBombs() {
		return maxBombs;
	}

}
