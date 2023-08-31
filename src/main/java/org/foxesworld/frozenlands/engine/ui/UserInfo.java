package org.foxesworld.frozenlands.engine.ui;

import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.component.IconComponent;
import com.simsilica.lemur.style.ElementId;
import org.foxesworld.frozenlands.engine.player.PlayerInterface;

import java.awt.*;

public class UserInfo {

    private Node guiNode;
    private Camera cam;

    private Font customFont;
    public ComponentManager componentManager;

    public UserInfo(PlayerInterface playerInterface) {
        this.guiNode = playerInterface.getGuiNode();
        this.cam = playerInterface.getFpsCam();
    }

    public void showWindow() {

        String[][] icons = new String[][]{
                {"posX", "icons/posX.png"},
                {"posY", "icons/posY.png"},
                {"posZ", "icons/posZ.png"},
                {"Health", ""}
        };

        // We'll wrap the text in a window to make sure the layout is working
        Container window = new Container();
        componentManager = new ComponentManager();
        float x = cam.getWidth() - window.getPreferredSize().x;
        float y = cam.getHeight() - window.getPreferredSize().y;
        window.setLocalTranslation(x, y, 0);
        Label pos = new Label(" Position", new ElementId("window.title.label"));
        window.addChild(pos);

        Container buttonSets = window.addChild(new Container());
        pos.setIcon(new IconComponent("icons/pos.png"));

        for (String[] iconDef : icons) {
            IconComponent icon;
            String posLetter = iconDef[0];
            Label posLabel = componentManager.addLabel("", posLetter, buttonSets);
            if (!iconDef[1].equals("")) {
                icon = new IconComponent(iconDef[1]);
                posLabel.setIcon(icon);
            }
        }

        float windowX = cam.getWidth() - window.getPreferredSize().x - 15;
        float windowY = cam.getHeight() - pos.getPreferredSize().y - 15;
        window.setLocalTranslation(windowX, windowY, 1);
        guiNode.attachChild(window);
    }
}
