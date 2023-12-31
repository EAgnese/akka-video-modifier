package com.ddm.app.businesslogic.utils;

public enum PythonScripts {
    AUDIO_EXTRACTION("audio_extraction.py"),
    CARTOON("cartoon.py"),
    BLACK_AND_WHITE_COLORED("no_color_except.py"),
    SUBTITLES("subtitles.py"),
    VIDEO_EXPORT("video_export.py"),
    IMAGES_EXTRACTION("video_images_extraction.py"),
    VIDEOS_FPS("videos_fps.py");

    public final String label;

    PythonScripts(String label) {
        String folder = "python/";
        this.label =  folder+label;
    }
}
