package org.foxesworld.newgame.engine.ui;

import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.component.IconComponent;
import com.simsilica.lemur.style.ElementId;
import org.foxesworld.newgame.engine.player.PlayerInterface;

public class UserInfo {

    private Node guiNode;
    private Camera cam;
    private ComponentManager componentManager;

    public UserInfo(PlayerInterface playerInterface){
        this.guiNode = playerInterface.getGuiNode();
        this.cam = playerInterface.getFpsCam();
    }

    public void showWindow() {

        String[][] icons = new String[][] {
                {"X", "test64.png"},
                {"Y", "test32.png"},
                {"Z", "test16.png"}
        };

        // We'll wrap the text in a window to make sure the layout is working
        Container window = new Container();
        float x = cam.getWidth() - window.getPreferredSize().x;
        float y = cam.getHeight() - window.getPreferredSize().y;
        window.setLocalTranslation(x, y, 0);
        Label pos = new Label(" Position", new ElementId("window.title.label"));
        window.addChild(pos);

        Container buttonSets = window.addChild(new Container());
        pos.setIcon(new IconComponent("pos.png"));
        int row = 0;
        int column;

        column = 0;
        for( String[] iconDef : icons ) {
            IconComponent icon = new IconComponent(iconDef[1]);
            String posLetter = iconDef[0];
            Label posLabel = new Label("0", new ElementId("pos" + posLetter));
            buttonSets.addChild(new Label(posLetter + " - " + posLabel.getText()));
            row++;
        }

        float windowX = cam.getWidth() - window.getPreferredSize().x;
        window.setLocalTranslation(windowX - 15, 750, 1);
        componentManager = new ComponentManager(window);
        test();
        guiNode.attachChild(window);
    }

    private void test(){
        componentManager.getLabelByElementId("posX").setText("GG");
    }
}
