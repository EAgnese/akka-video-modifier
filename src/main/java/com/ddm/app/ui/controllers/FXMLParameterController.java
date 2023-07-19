package com.ddm.app.ui.controllers;

import akka.actor.typed.ActorSystem;
import com.ddm.app.businesslogic.actors.Guardian;
import com.ddm.app.businesslogic.configuration.SystemConfiguration;
import com.ddm.app.businesslogic.singletons.InputConfigurationSingleton;
import com.ddm.app.businesslogic.singletons.SystemConfigurationSingleton;
import com.ddm.app.businesslogic.utils.GUIMaster;
import com.ddm.app.ui.elements.ColorItem;
import com.ddm.app.ui.elements.ColorItemCell;
import com.ddm.app.ui.interfaces.JFXProgress;
import com.ddm.app.ui.singletons.JFXProgressSingleton;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class FXMLParameterController implements Initializable {

    private final BooleanProperty oneColorSelected = new SimpleBooleanProperty(false);

    private List<ColorItem> colorItems;

    @FXML
    private ListView<ColorItem> colorList;

    @FXML
    private TextField ip;
    @FXML
    private TextField port;
    @FXML
    private TextField directoryPath;

    @FXML
    private AnchorPane anchorId;

    @FXML
    private RadioButton rdButton1;

    @FXML
    private RadioButton rdButton2;

    @FXML
    private RadioButton rdButton3;

    @FXML
    private Label test;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.colorList.visibleProperty().bind(oneColorSelected);
        this.colorItems = createColorItems();

        this.rdButton3.selectedProperty().set(true);

        this.colorList.getItems().addAll(colorItems);
        this.colorList.setCellFactory(listView -> new ColorItemCell());
    }

    @FXML
    private void handleButtonAction(){

        final DirectoryChooser dirChooser = new DirectoryChooser();
        Stage stage = (Stage) anchorId.getScene().getWindow();
        File file = dirChooser.showDialog(stage);

        if (file != null){
            directoryPath.setText(file.getAbsolutePath());
        }
    }

    @FXML
    private void confirmPath(){
        GUIMaster guiMaster = new GUIMaster();
        guiMaster.setNumWorkers(4);

        if (!this.ip.getText().isEmpty()) {
            guiMaster.setHost(this.ip.getText());
        }

        if (!this.port.getText().isEmpty()) {
            guiMaster.setPort(Integer.parseInt(this.port.getText()));
        }

        if (!this.directoryPath.getText().isEmpty()) {
            guiMaster.setInputPath(this.directoryPath.getText());
        }

        if (this.rdButton1.isSelected()){
            guiMaster.setCartoon(true);
        }

        if (this.rdButton2.isSelected()) {
            guiMaster.setColors(this.getSelectedColors().stream()
                    .map(String::toUpperCase)
                    .collect(Collectors.toList()));
        }

        SystemConfigurationSingleton.get().update(guiMaster);
        InputConfigurationSingleton.get().update(guiMaster);
        SystemConfiguration config = SystemConfigurationSingleton.get();

        final ActorSystem<Guardian.Message> guardian = ActorSystem.create(Guardian.create(), config.getActorSystemName(), config.toAkkaConfig());
        guardian.tell(new Guardian.StartMessage());


        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/frames/modifications-progress.fxml"
                    )
            );
            Stage newStage = new Stage();
            newStage.setScene(new Scene(loader.load()));

            JFXProgress progress = JFXProgressSingleton.get();
            System.out.println(loader.getController().toString());
            progress.setProgressController(loader.getController());
            JFXProgressSingleton.set(progress);

            newStage.setTitle("progress");

            newStage.show();

            Stage stage = (Stage) anchorId.getScene().getWindow();
            stage.close();

        }catch (IOException e){
            e.printStackTrace();
        }

    }

    @FXML
    private void getVisualModification() {

        String selectedText = "";
        boolean oneColorSelected = this.rdButton2.isSelected();

        if (this.rdButton1.isSelected()) {
            selectedText = "Give a cartoon effect to your video(s)";
        } else if (oneColorSelected) {
            selectedText = "Erase all colors of your video(s), except the one you select below";
        } else if (this.rdButton3.isSelected()) {
            selectedText = "No visual modification";
        }

        this.test.setText(selectedText);
        this.oneColorSelected.set(oneColorSelected);

    }

    private List<ColorItem> createColorItems() {
        List<ColorItem> items = new ArrayList<>();
        items.add(new ColorItem("Red"));
        items.add(new ColorItem("Orange"));
        items.add(new ColorItem("Yellow"));
        items.add(new ColorItem("Green"));
        items.add(new ColorItem("Cyan"));
        items.add(new ColorItem("Blue"));
        items.add(new ColorItem("Purple"));
        items.add(new ColorItem("Pink"));

        return items;
    }

    public List<String> getSelectedColors() {
        List<String> selectedColors = new ArrayList<>();

        for (ColorItem item : this.colorItems) {
            if (item.isSelected()) {
                selectedColors.add(item.getName());
            }
        }


        return selectedColors;
    }
}
