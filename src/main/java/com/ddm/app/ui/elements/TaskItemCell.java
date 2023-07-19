package com.ddm.app.ui.elements;

import javafx.beans.property.DoubleProperty;
import javafx.scene.control.ListCell;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.beans.binding.Bindings;

public class TaskItemCell extends ListCell<TaskItem> {

    private final HBox content;
    private final Text percentage;
    private final ProgressBar progressBar;

    public TaskItemCell() {
        this.content = new HBox();
        this.percentage = new Text();
        this.progressBar = new ProgressBar();


        this.content.getChildren().addAll(this.progressBar, this.percentage);
        this.content.setSpacing(10);
    }

    @Override
    protected void updateItem(TaskItem item, boolean empty) {
        super.updateItem(item, empty);

        if (item == null || empty) {
            setGraphic(null);
        } else {
            DoubleProperty progress = item.getProgressProperty();

            this.progressBar.progressProperty().bind(progress);

            this.percentage.textProperty().bind(Bindings.createStringBinding(
                    () -> String.format("%.2f%%", progress.get() * 100), progress));

            setGraphic(this.content);
        }
    }
}
