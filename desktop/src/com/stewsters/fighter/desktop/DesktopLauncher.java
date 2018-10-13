package com.stewsters.fighter.desktop;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.stewsters.fighter.FighterGame;

public class DesktopLauncher {
    public static void main(String[] arg) {

        Graphics.Monitor primary = Lwjgl3ApplicationConfiguration.getPrimaryMonitor();
        Graphics.DisplayMode desktopMode = Lwjgl3ApplicationConfiguration.getDisplayMode(primary);

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.useVsync(true);
        config.setFullscreenMode(desktopMode);
        new Lwjgl3Application(new FighterGame(), config);
    }
}
