package com.ddm.app.ui.interfaces;

import com.ddm.app.ui.controllers.FXMLProgressController;

import java.util.List;

public class JFXProgress implements ProgressInterface {

    FXMLProgressController progressController;

    public JFXProgress(){
    }

    public void setProgressController(FXMLProgressController progressController) {
        this.progressController = progressController;
    }

    @Override
    public void setNbrImages(List<Integer> nbrImages) {
        this.progressController.setNbrImages(nbrImages);
    }

    @Override
    public void updateProgress(int videoID, double progress) {
        this.progressController.updateProgress(videoID,progress);
    }
}
