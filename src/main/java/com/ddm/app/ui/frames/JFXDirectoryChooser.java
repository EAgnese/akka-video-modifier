package com.ddm.app.ui.frames;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JFXDirectoryChooser extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/frames/videos-selector.fxml"));
        Parent root = loader.load();
        stage.setTitle("Akka Video Modifier");

        stage.setMinWidth(575);
        stage.setMinHeight(550);

        stage.setScene(new Scene(root));
        stage.show();
    }
}
