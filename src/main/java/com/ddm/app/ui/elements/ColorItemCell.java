package com.ddm.app.ui.elements;

import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class ColorItemCell extends ListCell<ColorItem> {

    private final HBox content;
    private final Label nameLabel;
    private final CheckBox checkBox;

    public ColorItemCell() {
        this.content = new HBox();
        this.nameLabel = new Label();
        this.checkBox = new CheckBox();

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.SOMETIMES);

        HBox.setHgrow(this.content, Priority.ALWAYS);
        content.setAlignment(Pos.CENTER_RIGHT);

        this.content.getChildren().addAll(this.nameLabel, spacer, this.checkBox);
        this.content.setSpacing(10);
    }

    @Override
    protected void updateItem(ColorItem item, boolean empty) {
        super.updateItem(item, empty);

        if (item == null || empty) {
            setGraphic(null);
        } else {
            this.nameLabel.setText(item.getName());

            item.getSelectedProperty().bind(this.checkBox.selectedProperty());

            setGraphic(this.content);
        }
    }
}