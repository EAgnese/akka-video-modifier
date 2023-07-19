package com.ddm.app.ui.elements;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
public class TaskItem {
    private final String name;
    private final DoubleProperty progress = new SimpleDoubleProperty();

    public TaskItem(String name, double progress) {
        this.name = name;
        this.progress.set(progress);
    }

    public String getName() {
        return this.name;
    }

    public double getProgress() {
        return this.progress.get();
    }

    public void setProgress(double progress){
        this.progress.set(progress);
    }

    public DoubleProperty getProgressProperty() {
        return this.progress;
    }
}