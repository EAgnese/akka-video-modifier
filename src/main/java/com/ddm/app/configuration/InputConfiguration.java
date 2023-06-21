package com.ddm.app.configuration;


import lombok.Data;

import java.io.File;
import java.io.IOException;

//Configuration for the input (the video) of the application

@Data
public class InputConfiguration {

    private String inputPath = "data" + File.separator + "videos";

    public void update(CommandMaster commandMaster) {
        this.inputPath = commandMaster.inputPath;
    }

    public File[] getInputFiles() {
        return new File(this.inputPath).listFiles();
    }

    public void cuttingVideo(File inputFile) throws IOException {
        //TODO : call the python script
    }


}

