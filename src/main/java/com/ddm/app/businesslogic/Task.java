package com.ddm.app.businesslogic;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class Task {
    byte[] img;
    String imgName;
    String subtitles;
    boolean cartoon;
    int videoId;
    String videoName;
    List<String> colors;
}
