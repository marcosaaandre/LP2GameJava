package dev.learninggame.states;

import java.awt.Graphics;

import dev.learninggame.Handler;
import dev.learninggame.gfx.Assets;
import dev.learninggame.ui.ClickListener;
import dev.learninggame.ui.UIImageButton;
import dev.learninggame.ui.UIManager;

public class MenuState extends State {
	
	private UIManager uiManager;
	
	public MenuState(Handler handler) {
		super(handler);
		uiManager = new UIManager(handler);
		handler.getMouseManager().setUIManager(uiManager);
		
		
		uiManager.addObject(new UIImageButton(250, 200, 128, 128, Assets.btn_play, new ClickListener() {
			@Override
			public void onClick() {
				handler.getMouseManager().setUIManager(null);
				State.setState(handler.getGame().getGameState());
				
			}
		}));
		
	}
	
	@Override
	public void tick() {
		uiManager.tick();
		
	}

	@Override
	public void render(Graphics g) {
		uiManager.render(g);
	}
	
	
}
