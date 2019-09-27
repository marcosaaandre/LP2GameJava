package dev.learninggame.gfx;

import java.awt.image.BufferedImage;
import java.io.Serializable;

public class Animation implements Serializable {
	private int speed, index;
	private long lastTime, timer;
	private transient BufferedImage[] frames;
	
	public Animation(int speed, BufferedImage[] frames) {
		this.speed = speed; //Tempo maximo entre um frame e outro da animacao
		this.frames = frames; //Conjunto de imagens do sprite sheet com os frames
		index = 0;
		timer = 0;
		lastTime = System.currentTimeMillis();
	}
	
	public void tick() {
		timer += System.currentTimeMillis() - lastTime;
		lastTime = System.currentTimeMillis();
		
		if (timer > speed) {
			index++;
			timer = 0;
			if(index >= frames.length) {
				index = 0;
			}
		}
	}
	
	public BufferedImage getCurrentFrame() {
		return frames[index];
	}

	public void setFrames(BufferedImage[] frames) {
		this.frames = frames;
	}
	
}
