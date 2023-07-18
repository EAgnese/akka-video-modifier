package com.ddm.app.ui.frames;

import com.ddm.app.ui.controllers.FXMLProgressController;
import com.ddm.app.ui.interfaces.ProgressInterface;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class JFXProgress implements ProgressInterface {

    FXMLProgressController progressController;

    public JFXProgress(/*FXMLProgressController progressController*/){
        //this.progressController = progressController;
    }

    @Override
    public void initProgress(List<Integer> nbrImages) {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/frames/parameters-frames.fxml"));
        this.progressController = loader.getController();

        this.progressController.setNbrImages(nbrImages);

        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Stage stage = new Stage();
        stage.setTitle("Akka Video Modifier");

        stage.setScene(new Scene(root));
        stage.show();
    }

    @Override
    public void updateProgress(int videoID, double progress) {
        this.progressController.updateProgress(videoID,progress);
    }
}
