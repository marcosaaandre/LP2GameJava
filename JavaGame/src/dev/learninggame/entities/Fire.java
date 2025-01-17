package dev.learninggame.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import dev.learninggame.Handler;
import dev.learninggame.gfx.Assets;
import dev.learninggame.tiles.Tile;

public class Fire extends Entity{
	
	public static final int MAIN = 0;
	public static final int MID_RIGHT = 1;
	public static final int RIGHT = 2;
	public static final int MID_LEFT = 3;
	public static final int LEFT = 4;
	public static final int MID_BOT = 5;
	public static final int BOT = 6;
	public static final int MID_TOP = 7;
	public static final int TOP = 8;

	
	private static BufferedImage[] fireSheet = Assets.bombFire;
	private int currentAsset;
	//Time to disappear
	private long initialTime;
	private long currentTime;
	private int id;
	
	public Fire(Handler handler, float x, float y, int currentAsset) {
		super(handler, x, y, Tile.TILEWIDTH, Tile.TILEHEIGHT);
		this.currentAsset = currentAsset;
		
		bounds.x = Tile.TILEWIDTH * getCurrentTileX(x);
		bounds.y = Tile.TILEHEIGHT * getCurrentTileY(y);

		bounds.width = Tile.TILEWIDTH;
		bounds.height = Tile.TILEHEIGHT;
		
		//Tempo inicial da bomba
		initialTime = System.currentTimeMillis();
		currentTime = System.currentTimeMillis();
		
	}
	
	@Override
	public void tick() {
		currentTime = System.currentTimeMillis();
		verifyTime();
		verifyBombs(getCurrentTileX(x), getCurrentTileX(y));
	}
	
	/**
	 * @author Nathan Rodrigo
	 * Verifica se o fogo pode se espalhar para as Tiles acima
	 */
	public void verifyOpenYtop() {
		//Cima
		int ty = (int) y - Tile.TILEHEIGHT;
		
		if(!collisionWithTile(getCurrentTileX(x), getCurrentTileY(ty)) && this.currentAsset == MAIN
				&& !verifyBombs(getCurrentTileX(x), getCurrentTileX(ty))
				&& !verifyBricks(getCurrentTileX(x), getCurrentTileX(ty))) {
			handler.getWorld().installFire(x, ty, MID_TOP, id);
		}
		if(!collisionWithTile(getCurrentTileX(x), getCurrentTileY(ty)) && this.currentAsset == MID_TOP
				&& !verifyBombs(getCurrentTileX(x), getCurrentTileX(ty))
				&& !verifyBricks(getCurrentTileX(x), getCurrentTileX(ty))) {
			handler.getWorld().installFire(x, ty, TOP, id);
		}
	}
	
	/**
	 * @author Nathan Rodrigo
	 * Verifica se o fogo pode se espalhar para as Tiles abaixo
	 */
	public void verifyOpenYbot() {
		//Cima
		int ty = (int) y + Tile.TILEHEIGHT;
		
		if(!collisionWithTile(getCurrentTileX(x), getCurrentTileY(ty)) && this.currentAsset == MAIN
				&& !verifyBombs(getCurrentTileX(x), getCurrentTileX(ty))
				&& !verifyBricks(getCurrentTileX(x), getCurrentTileX(ty))) {
			handler.getWorld().installFire(x, ty, MID_BOT, id);
		}
		if(!collisionWithTile(getCurrentTileX(x), getCurrentTileY(ty)) && this.currentAsset == MID_BOT
				&& !verifyBombs(getCurrentTileX(x), getCurrentTileX(ty))
				&& !verifyBricks(getCurrentTileX(x), getCurrentTileX(ty))) {
			handler.getWorld().installFire(x, ty, BOT, id);
		}
	}
	
	/**
	 * @author Nathan Rodrigo
	 * Verifica se o fogo pode se espalhar para as Tiles a direita
	 */
	public void verifyOpenXright() {
		//Direita
		int tx = (int) x + Tile.TILEWIDTH;
		
		if(!collisionWithTile(getCurrentTileX(tx), getCurrentTileY(y)) && this.currentAsset == MAIN
				&& !verifyBombs(getCurrentTileX(tx), getCurrentTileX(y))
				&& !verifyBricks(getCurrentTileX(tx), getCurrentTileX(y))) {
			handler.getWorld().installFire(tx, y, MID_RIGHT, id);
		}
		if(!collisionWithTile(getCurrentTileX(tx), getCurrentTileY(y)) && this.currentAsset == MID_RIGHT
				&& !verifyBombs(getCurrentTileX(tx), getCurrentTileX(y))
				&& !verifyBricks(getCurrentTileX(tx), getCurrentTileX(y))) {
			handler.getWorld().installFire(tx, y, RIGHT, id);
		}
	}
	
	/**
	 * @author Nathan Rodrigo
	 * Verifica se o fogo pode se espalhar para as Tiles a esquerda
	 */
	public void verifyOpenXleft() {
		//Esquerda
		int tx = (int) x - Tile.TILEWIDTH;
		
		if(!collisionWithTile(getCurrentTileX(tx), getCurrentTileY(y)) && this.currentAsset == MAIN
				&& !verifyBombs(getCurrentTileX(tx), getCurrentTileX(y))
				&& !verifyBricks(getCurrentTileX(tx), getCurrentTileX(y))) {
			handler.getWorld().installFire(tx, y, MID_LEFT, id);
		}
		if(!collisionWithTile(getCurrentTileX(tx), getCurrentTileY(y)) && this.currentAsset == MID_LEFT
				&& !verifyBombs(getCurrentTileX(tx), getCurrentTileX(y))
				&& !verifyBricks(getCurrentTileX(tx), getCurrentTileX(y))) {
			handler.getWorld().installFire(tx, y, LEFT, id);
		}
	}
	
	/**
	 * Metodo para verificar se ha bombas no caminho do fogo
	 * @author Nathan Rodrigo
	 * @param currentXFire coordenada X da textura do fogo
	 * @param currentYFire coordenada Y da textura do fogo
	 * @return true se houver alguma bomba no caminho
	 */
	public boolean verifyBombs(int currentXFire, int currentYFire) {
		if(handler.getWorld().hasBomb(currentXFire, currentYFire)) {
			handler.getWorld().getBomb(currentXFire, currentYFire).explodeThisBomb();
			return true;
		}
		return false;
	}
	
	/**
	 * Metodo para verificar se ha tijolos no caminho do fogo
	 * @author Nathan Rodrigo
	 * @param currentXFire coordenada X da textura do fogo
	 * @param currentYFire coordenada Y da textura do fogo
	 * @return true se houver algum tijolo no caminho
	 */
	public boolean verifyBricks(int currentXFire, int currentYFire) {
		if(handler.getWorld().hasBrick(currentXFire, currentYFire)) {
			handler.getWorld().getBrick(currentXFire, currentYFire).setActive(false);
			return true;
		}
		return false;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * @author Nathan Rodrigo
	 * @return tempo de vida do fogo
	 */
	public long getTimeToDisappear() {
		return currentTime - initialTime;
	}
	
	/**
	 * @author Nathan Rodrigo
	 * Funcao para verificar se o tempo do fogo deve esgotar
	 * @throws FileNotFoundException 
	 */
	public void verifyTime() {
		if(getTimeToDisappear() > 2000) {
			handler.getWorld().getEntityManager().removeEntity(this);
			
		}
	}
	
	private boolean collisionWithTile(int nextTileX, int nextTileY) {
		return handler.getWorld().getTile(nextTileX, nextTileY).isSolid();
	}
	
	@Override
	public void render(Graphics g) {
		g.drawImage(fireSheet[this.currentAsset], Tile.TILEWIDTH*getCurrentTileX(x), 
				Tile.TILEHEIGHT*getCurrentTileY(y), Tile.TILEWIDTH, Tile.TILEHEIGHT, null);
		
		//Retangulo para testar hitbox do fogo
		/*g.setColor(Color.red); // Testar hit box
		g.fillRect((int) (bounds.x), (int)(bounds.y), bounds.width, bounds.height);*/
		
	}
	
	@Override
	protected void die() {
		
	}

}
