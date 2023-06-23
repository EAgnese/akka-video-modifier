package com.ddm.app.configuration;


import lombok.Data;

import java.io.File;

@Data
public class InputConfiguration {

    private String inputPath = "data" + File.separator + "videos";

    public void update(CommandMaster commandMaster) {
        this.inputPath = commandMaster.inputPath;
    }

    public File[] getInputFiles() {
        return new File(this.inputPath).listFiles();
    }

}

