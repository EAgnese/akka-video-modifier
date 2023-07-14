package com.ddm.app.ui.controllers;

import com.ddm.app.ui.interfaces.ProgressInterface;
import com.ddm.app.ui.utils.TaskItem;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class FXMLProgressController implements Initializable, ProgressInterface {

    private List<TaskItem> taskItems;

    @FXML
    private ProgressBar mainProgressBar;

    @FXML
    private Text mainPercentage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    @Override
    public void init(int size) {
        taskItems = FXCollections.observableArrayList();

        this.taskItems.clear(); // Effacer les tâches existantes

        for (int i = 0; i < size; i++) {
            TaskItem taskItem = new TaskItem("Tâche " + (i + 1), 0.0);
            taskItems.add(taskItem);
        }
    }

    @Override
    public void updateProgress(int videoId, double progress) {

        this.taskItems.get(videoId).setProgress(progress);

        double totalProgress = 0.0;

        for (TaskItem taskItem : this.taskItems) {
            totalProgress += taskItem.getProgress();
        }

        double overallProgress = totalProgress / this.taskItems.size();
        this.mainProgressBar.setProgress(overallProgress);
        this.mainPercentage.setText(String.format("Avancement total : %.2f%%", overallProgress * 100));
    }

}
