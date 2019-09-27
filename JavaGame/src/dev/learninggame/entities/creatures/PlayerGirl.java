package dev.learninggame.entities.creatures;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.Serializable;

import dev.learninggame.Handler;
import dev.learninggame.entities.Bomb;
import dev.learninggame.gfx.Animation;
import dev.learninggame.gfx.Assets;

public class PlayerGirl extends Player implements Serializable {
	
	public PlayerGirl(String username, Handler handler, float x, float y) {	
		super(username, handler, x, y);
				
		animDown = new Animation(100, null);
		animUp = new Animation(100, null);
		animRight = new Animation(100, null);
		animLeft = new Animation(100, null);

	}
	
	@Override
	public void updateFrames() {
		//Animations
		animUp.setFrames(Assets.playerg_up);
		animDown.setFrames(Assets.playerg_down);
		animLeft.setFrames(Assets.playerg_left);
		animRight.setFrames(Assets.playerg_right);
		
	}

	@Override
	protected BufferedImage getCurrentAnimation() {
		if(xMove > 0)
			return animRight.getCurrentFrame();
		if(xMove < 0)
			return animLeft.getCurrentFrame();
		if(yMove > 0)
			return animDown.getCurrentFrame();
		if(yMove < 0)
			return animUp.getCurrentFrame();
		return Assets.woman;
	} 
	
	@Override
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
			if(tempoFinal - tempoInicio > 200) {
				installBomb();
				tempoInicio = tempoFinal;
			}
		}
	}
		
}
