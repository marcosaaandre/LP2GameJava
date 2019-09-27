package dev.learninggame.entities;

import java.awt.Graphics;
import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Iterator;

import dev.learninggame.Handler;
import dev.learninggame.entities.creatures.Player;
import dev.learninggame.entities.creatures.PlayerGirl;

public class EntityManager implements Serializable {
	
	private transient Handler handler;
	private CopyOnWriteArrayList<Entity> entities;
	
	public EntityManager(Handler handler) {
		this.handler = handler;
		entities = new CopyOnWriteArrayList<Entity>();
	}
	
	public void tick() {
		for (Entity e: entities) {
			e.tick();
			
			if (!e.isActive()) {
				entities.remove(e);
			}
		}
	}
	
	public void render(Graphics g) {
		for(Entity e : entities)
			e.render(g);
	}
	
	public void addEntity(Entity e) {
		entities.add(e);
	}
	
	//GETTERS AND SETTERS
	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}
	
	public CopyOnWriteArrayList<Player> getPlayers() {
		CopyOnWriteArrayList<Player> players = new CopyOnWriteArrayList<Player>();
		for (Entity e : getEntities()) {
			if (e instanceof Player) {
				players.add((Player) e);
			}
		}
		return players;
	}
	
	public CopyOnWriteArrayList<Entity> getEntities() {
		return entities;
	}

	public CopyOnWriteArrayList<Bomb> getBombs(){
		CopyOnWriteArrayList<Bomb> bombs = new CopyOnWriteArrayList<Bomb>();
		for (Entity e : getEntities()) {
			if (e instanceof Bomb) {
				bombs.add((Bomb) e);
			}
		}
		return bombs;
	}
	
	public CopyOnWriteArrayList<Fire> getFires(){
		CopyOnWriteArrayList<Fire> fires = new CopyOnWriteArrayList<Fire>();
		for (Entity e : getEntities()) {
			if (e instanceof Fire) {
				fires.add((Fire) e);
			}
		}
		return fires;
	}
	
	public CopyOnWriteArrayList<Brick> getBricks(){
		CopyOnWriteArrayList<Brick> bricks = new CopyOnWriteArrayList<Brick>();
		for (Entity e : getEntities()) {
			if (e instanceof Brick) {
				bricks.add((Brick) e);
			}
		}
		return bricks;
	}
	
    public void setEntities(CopyOnWriteArrayList<Entity> entities) {
		this.entities = entities;
	}
	
	
	public void removeEntity(Entity entity) {
		entities.remove(entity);
	}
	
	/**
	 * Atualiza o handler para todos os objetos
	 * @param handler2
	 */
	public void setHandlerForAll(Handler handler) {
		for (Entity e : entities) {
			e.setHandler(handler);
		}
	}
	
}
