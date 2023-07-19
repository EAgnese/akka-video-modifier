package com.ddm.app.ui.elements;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class ColorItem {


    private final String name;
    private final BooleanProperty selected;
    public ColorItem(String name) {
        this.name = name;
        this.selected = new SimpleBooleanProperty(false);
    }
    public String getName() {
        return this.name;
    }

    public boolean isSelected() {
        return this.selected.get();
    }
    public BooleanProperty getSelectedProperty(){
        return this.selected;
    }

}