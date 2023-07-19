package com.ddm.app.ui.controllers;

import com.ddm.app.ui.elements.TaskItem;
import com.ddm.app.ui.elements.TaskItemCell;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class FXMLProgressController implements Initializable {

    private List<TaskItem> taskItems;

    private List<Integer> nbrImages;

    @FXML
    private ProgressBar mainProgressBar;

    @FXML
    private Text mainPercentage;

    @FXML
    private ListView<TaskItem> taskList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.mainPercentage.setText("0%");
    }


    public void setNbrImages(List<Integer> nbrImages) {
        this.nbrImages = nbrImages;
        this.taskItems = FXCollections.observableArrayList();

        this.taskItems.clear(); // Effacer les tâches existantes

        for (int i = 0; i < nbrImages.size(); i++) {
            TaskItem taskItem = new TaskItem("Video " + (i + 1), 0.0);
            this.taskItems.add(taskItem);
        }

        this.taskList.getItems().addAll(this.taskItems);
        this.taskList.setCellFactory(listView -> new TaskItemCell());
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
