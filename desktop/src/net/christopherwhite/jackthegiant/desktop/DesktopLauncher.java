package net.christopherwhite.jackthegiant.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import helpers.GameInfo;
import net.christopherwhite.jackthegiant.GameMain;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = GameInfo.Width;
		config.height = GameInfo.Height;
		new LwjglApplication(new GameMain(), config);
	}
}
//https://www.udemy.com/course/the-complete-libgdx-game-course/learn/lecture/5133666#overview