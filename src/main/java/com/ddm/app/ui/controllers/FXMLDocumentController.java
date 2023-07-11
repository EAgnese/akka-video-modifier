package com.ddm.app.ui.controllers;

import com.ddm.app.App;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class FXMLDocumentController implements Initializable {

    public static class ColorItem {


        private final String name;
        private final BooleanProperty selected;
        public ColorItem(String name) {
            this.name = name;
            this.selected = new SimpleBooleanProperty(false);
        }
        public String getName() {
            return this.name;
        }

        public boolean isSelected() {
            return this.selected.get();
        }
        public BooleanProperty getSelectedProperty(){
            return this.selected;
        }

    }

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
        colorList.setCellFactory(listView -> new ColorItemCell());
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
        List<String> argsList = new ArrayList<>();
        argsList.add("master");
        argsList.add("-w");
        argsList.add("4");

        if (!this.ip.getText().isEmpty()) {
            argsList.add("-h");
            argsList.add(this.ip.getText());
        }

        if (!this.port.getText().isEmpty()) {
            argsList.add("-p");
            argsList.add(this.port.getText());
        }

        if (!this.directoryPath.getText().isEmpty()) {
            argsList.add("-ip");
            argsList.add(this.directoryPath.getText());
        }

        if (this.rdButton1.isSelected()){
            argsList.add("-c");
        }

        if (this.rdButton2.isSelected()) {
            argsList.add("-o");
            for (String color : getSelectedColors()){
                argsList.add(color.toUpperCase());
            }
        }

        String[] args = argsList.toArray(new String[0]);
//        for(String arg : args){
//            System.out.println(arg);
//        }
        App.main(args);


        Stage stage = (Stage) anchorId.getScene().getWindow();
        stage.close();
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

    private static class ColorItemCell extends ListCell<ColorItem> {

        private final HBox content;
        private final Label nameLabel;
        private final CheckBox checkBox;

        public ColorItemCell() {
            this.content = new HBox();
            this.nameLabel = new Label();
            this.checkBox = new CheckBox();



            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.SOMETIMES);

            HBox.setHgrow(this.content, Priority.ALWAYS);
            content.setAlignment(Pos.CENTER_RIGHT);

            this.content.getChildren().addAll(this.nameLabel, spacer, this.checkBox);
            this.content.setSpacing(10);
        }

        @Override
        protected void updateItem(ColorItem item, boolean empty) {
            super.updateItem(item, empty);

            if (item == null || empty) {
                setGraphic(null);
            } else {
                this.nameLabel.setText(item.getName());

                item.getSelectedProperty().bind(this.checkBox.selectedProperty());

                setGraphic(this.content);
            }
        }
    }

}
