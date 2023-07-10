package com.ddm.app.businesslogic.configuration;


import lombok.Data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Data
public class InputConfiguration {

    private String inputPath = "data" + File.separator + "videos";

    private boolean cartoon = false;
    private List<String> colors = new ArrayList<>();

    public void update(CommandMaster commandMaster) {
        this.inputPath = commandMaster.inputPath;
        this.cartoon = commandMaster.cartoon;
        this.colors = commandMaster.colors;
    }

    public File[] getInputFiles() {
        return new File(this.inputPath).listFiles();
    }

}

