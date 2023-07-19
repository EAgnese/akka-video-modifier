package com.ddm.app.ui.controllers;

import com.ddm.app.ui.elements.TaskItem;
import com.ddm.app.ui.elements.TaskItemCell;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class FXMLProgressController implements Initializable {

    private List<TaskItem> taskItems;

    @FXML
    private ProgressBar mainProgressBar;

    @FXML
    private Text mainPercentage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println(this.mainProgressBar);
    }


    public void setNbrImages(List<Integer> nbrImages) {
        taskItems = FXCollections.observableArrayList();

        this.taskItems.clear(); // Effacer les tâches existantes

        for (int i = 0; i < nbrImages.size(); i++) {
            TaskItem taskItem = new TaskItem("Video " + (i + 1), 0.0);
            this.taskItems.add(taskItem);
        }
    }

    public void updateProgress(int videoId, double progress) {

        TaskItem modifiedTask = this.taskItems.get(videoId);
        int images = this.nbrImages.get(videoId);
        modifiedTask.setProgress(progress/images);

        double totalProgress = 0.0;

        for (TaskItem taskItem : this.taskItems) {
            totalProgress += taskItem.getProgress();
        }

        double overallProgress = totalProgress / this.taskItems.size();
        this.mainProgressBar.setProgress(overallProgress);
        this.mainPercentage.setText(String.format("%.2f%%", overallProgress * 100));
    }

}
