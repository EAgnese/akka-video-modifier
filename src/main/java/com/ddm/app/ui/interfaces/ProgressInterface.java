package com.ddm.app.ui.interfaces;

import java.util.List;

public interface ProgressInterface {


    void initProgress(List<Integer> nbrImages);

    void updateProgress(int videoID, double progress);

}
