package org.foxesworld.newgame.engine.ui;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

public class HUDController implements ScreenController {
    private Nifty nifty;
    private Screen screen;
    //private Label hudTextLabel;

    @Override
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
        //hudTextLabel = screen.findNiftyControl("#hudText", Label.class);
    }

    @Override
    public void onStartScreen() {
        // Ничего не делаем при старте экрана
    }

    @Override
    public void onEndScreen() {
        // Ничего не делаем при закрытии экрана
    }
}