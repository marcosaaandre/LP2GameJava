package dev.learninggame.entities;

import java.awt.Graphics;

import dev.learninggame.Handler;
import dev.learninggame.gfx.Animation;
import dev.learninggame.gfx.Assets;
import dev.learninggame.tiles.Tile;

public class Bomb extends Entity{
	
	private Animation animBomb;
	
	public Bomb(Handler handler, float x, float y) {
		super(handler, x, y, Tile.TILEWIDTH, Tile.TILEHEIGHT);
		
		//Posicao da bomba
		bounds.x = Tile.TILEWIDTH*getCurrentTileX();
		bounds.y = Tile.TILEHEIGHT*getCurrentTileY();
		//Largura e comprimento da hitbox
		bounds.width = Tile.TILEWIDTH;
		bounds.height = Tile.TILEHEIGHT;
		
		animBomb = new Animation(300, Assets.putBomb);
		
	}
	
	private int getCurrentTileX() {
		for(int i = 0; i < (handler.getHeight()/Tile.TILEHEIGHT) + (Tile.TILEHEIGHT*2); i++) {
			if(x <= Tile.TILEHEIGHT*i) {
				return i;
			}
		}
		return 0;
	}
	
	private int getCurrentTileY() {
		for(int i = 0; i < handler.getHeight()/Tile.TILEHEIGHT + (Tile.TILEWIDTH*2); i++) {
			if(y <= Tile.TILEHEIGHT*i) {
				return i;
			}
		}
		return 0;
	}
	
	@Override
	public void tick() {
		animBomb.tick();
	}

	@Override
	public void render(Graphics g) {
		g.drawImage(animBomb.getCurrentFrame(), Tile.TILEWIDTH*getCurrentTileX(), 
				Tile.TILEHEIGHT*getCurrentTileY(), Tile.TILEWIDTH, Tile.TILEHEIGHT, null);
	}

	@Override
	protected void die() {
		
	}

}