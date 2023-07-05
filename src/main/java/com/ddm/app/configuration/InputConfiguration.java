package com.ddm.app.configuration;


import lombok.Data;

import java.io.File;

@Data
public class InputConfiguration {

    private String inputPath = "data" + File.separator + "videos";

    private boolean cartoon = false;
    private String color = null;

    public void update(CommandMaster commandMaster) {
        this.inputPath = commandMaster.inputPath;
        this.cartoon = commandMaster.cartoon;
        this.color = commandMaster.color;
    }

    public File[] getInputFiles() {
        return new File(this.inputPath).listFiles();
    }

}

