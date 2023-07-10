package com.ddm.app.businesslogic;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Result {
    byte[] img;
    String imgName;
    int videoId;
    String videoName;
}