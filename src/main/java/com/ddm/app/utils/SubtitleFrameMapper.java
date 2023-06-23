package com.ddm.app.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class SubtitleFrameMapper {
    private final int fps;
    private final String filePath;

    public SubtitleFrameMapper(int fps, String filePath) {
        this.fps = fps;
        this.filePath = filePath;
    }

    public Map<Integer, String> mapFramesToSubtitles() {
        Map<Integer, String> frameSubtitleMap = new HashMap<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(this.filePath));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ", 2);

                if (parts.length >= 2) {
                    String timecodeRange = parts[0];
                    String subtitle = parts[1];

                    String[] timecodeParts = timecodeRange.split("-");
                    String startTimecode = timecodeParts[0];
                    String endTimecode = timecodeParts[1];

                    int startFrame = convertTimecodeToFrame(startTimecode, this.fps);
                    int endFrame = convertTimecodeToFrame(endTimecode, this.fps);

                    for (int frameNumber = startFrame; frameNumber <= endFrame; frameNumber++) {
                        frameSubtitleMap.put(frameNumber, subtitle);
                    }
                }
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return frameSubtitleMap;
    }

    private int convertTimecodeToFrame(String timecode, int fps) {
        String[] timeParts = timecode.split(",");

        int frame = (timeParts.length > 1) ? Integer.parseInt(timeParts[1]) : 0;
        if (frame >= fps) {
            frame = fps - 1;
        }
        String[] timeValues = timeParts[0].split(":");

        int seconds = (timeValues.length > 0) ? Integer.parseInt(timeValues[timeValues.length - 1]) : 0;

        int minutes = (timeValues.length > 1) ? Integer.parseInt(timeValues[timeValues.length - 2]) : 0;

        int hours = (timeValues.length > 2) ? Integer.parseInt(timeValues[timeValues.length - 3]) : 0;

        Duration duration = Duration.ofHours(hours).plusMinutes(minutes).plusSeconds(seconds);
        long totalSeconds = duration.getSeconds();

        return (int) (totalSeconds * fps + frame);
    }

    public static void main(String[] args) {
        int fps = 24;
        int duration = 3*24+18;
        String filePath = "subtitles.txt";

        SubtitleFrameMapper frameMapper = new SubtitleFrameMapper(fps, filePath);
        Map<Integer, String> frameSubtitleMap = frameMapper.mapFramesToSubtitles();

        for (int frameNumber = 0;frameNumber <= duration;frameNumber++) {
            String subtitle = frameSubtitleMap.get(frameNumber);

            System.out.println("Frame " + frameNumber + ": " + ((subtitle == null) ? "":subtitle));
        }
    }
}