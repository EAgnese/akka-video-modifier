package com.ddm.app.ui.frames;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class MainFrame extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/frames/parameters-frames.fxml")));
        stage.setTitle("Akka Video Modifier");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
