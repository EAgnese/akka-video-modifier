package com.ddm.app.ui.controllers;

import com.ddm.app.App;
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
        private final CheckBox checkBox;

        public ColorItem(String name) {
            this.name = name;
            this.checkBox = new CheckBox();
        }
        public String getName() {
            return name;
        }
        public boolean isSelected() {
            return checkBox.isSelected();
        }
    }



    @FXML
    private ListView<ColorItem> colorList;

    private List<ColorItem> colorItems;

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
        this.colorList.setVisible(this.rdButton2.isSelected());
        this.colorItems = createColorItems();

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
        String[] args = {"master", "-w", "0",};
        App.main(args);

        Stage stage = (Stage) anchorId.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void getVisualModification() {

        if (this.rdButton1.isSelected()){
            this.test.setText(this.rdButton1.getText());
        } else if (this.rdButton2.isSelected()) {
            this.test.setText(this.rdButton2.getText());
        }else if (this.rdButton3.isSelected()) {
            this.test.setText(this.rdButton3.getText());
        }

        this.colorList.setVisible(this.rdButton2.isSelected());

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

    public List<ColorItem> getSelectedColors() {
        List<ColorItem> selectedColors = new ArrayList<>();

        for (ColorItem item : this.colorItems) {
            if (item.isSelected()) {
                selectedColors.add(item);
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
                this.checkBox.setSelected(item.isSelected());

                setGraphic(this.content);
            }
        }
    }

}
