package org.foxesworld.newgame.engine.ui;

import com.simsilica.lemur.Label;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.core.GuiControl;
import com.simsilica.lemur.style.ElementId;

import com.jme3.scene.Spatial;


public class ComponentManager {

    private Panel container;

    // Конструктор, принимающий контейнер, в котором хранятся компоненты
    public ComponentManager(Panel container) {
        this.container = container;
    }

    // Метод для получения Label по строковому значению ElementId
    public Label getLabelByElementId(String elementIdText) {
        ElementId elementId = new ElementId(elementIdText);
        return findLabelByElementId(container, elementId);
    }

    // Приватный метод для поиска Label по ElementId
    private Label findLabelByElementId(Panel container, ElementId elementId) {
        for (Spatial child : container.getChildren()) {
            if (child instanceof Label && child.getControl(GuiControl.class).getComponent(elementId.toString()).equals(elementId)) {
                return (Label) child;
            } else if (child instanceof Panel) {
                Label foundLabel = findLabelByElementId((Panel) child, elementId);
                if (foundLabel != null) {
                    return foundLabel;
                }
            }
        }
        return null;
    }
}
