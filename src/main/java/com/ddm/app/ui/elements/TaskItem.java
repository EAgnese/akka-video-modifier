package com.ddm.app.ui.elements;

public class TaskItem {
    private final String name;
    private double progress;

    public TaskItem(String name, double progress) {
        this.name = name;
        this.progress = progress;
    }

    public String getName() {
        return this.name;
    }

    public double getProgress() {
        return this.progress;
    }

    public void setProgress(double progress){
        this.progress = progress;
    }
}