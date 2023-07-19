package com.ddm.app.ui.interfaces;

import java.util.List;

public interface ProgressInterface {


    void setNbrImages(List<Integer> nbrImages);

    void updateProgress(int videoID, double progress);

}
