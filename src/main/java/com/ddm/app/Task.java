package com.ddm.app;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Task {
    byte[] img;
    String imgName;
    String subtitles;
    boolean cartoon;
    int videoId;
}
