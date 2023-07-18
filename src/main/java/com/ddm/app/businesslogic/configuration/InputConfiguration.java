package com.ddm.app.businesslogic.configuration;

import com.ddm.app.businesslogic.utils.ConfigMaster;
import lombok.Data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Data
public class InputConfiguration {

    private String inputPath = "data" + File.separator + "videos";

    private boolean cartoon = false;
    private List<String> colors = new ArrayList<>();

    public void update(ConfigMaster configMaster) {
        this.inputPath = configMaster.getInputPath();
        this.cartoon = configMaster.isCartoon();
        this.colors = configMaster.getColors();
    }

    public File[] getInputFiles() {
        return new File(this.inputPath).listFiles();
    }

}

