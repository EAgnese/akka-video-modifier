package com.ddm.app.utils;

import com.ddm.app.singletons.SystemConfigurationSingleton;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;

public class VideoFPSReader {
    private static VideoFPSReader instance;
    private final HashMap<String, Integer> videoFPSMap;

    private VideoFPSReader() {
        videoFPSMap = new HashMap<>();
    }

    public static VideoFPSReader getInstance() {
        if (instance == null) {
            instance = new VideoFPSReader();
        }
        return instance;
    }

    public HashMap<String, Integer> getVideoFPS(String videosFilesPath, String jsonFilePath) {
        if (videoFPSMap.isEmpty()) {
            runPythonScript(videosFilesPath, jsonFilePath);
            readJSONFile(jsonFilePath);
        }

        return videoFPSMap;
    }

    private void runPythonScript(String videosFilesPath, String jsonFilePath) {
        String pythoncommand = SystemConfigurationSingleton.get().getPythoncommand();
        String[] cmdFps = {pythoncommand, PythonScripts.VIDEOS_FPS.label, "-p", videosFilesPath, "-o", jsonFilePath};
        PythonScriptRunner.run(cmdFps);
    }

    private void readJSONFile(String jsonFilePath) {
        try {
            // Read the JSON file and populate the HashMap
            String jsonContent = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
            JSONObject jsonObject = new JSONObject(jsonContent);

            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String videoName = keys.next();
                int fps = jsonObject.getInt(videoName);
                videoFPSMap.put(videoName.replace('.', '-'), fps);
            }
        } catch (IOException e) {
            System.out.println("Error reading the JSON file: " + e.getMessage());
        } catch (JSONException e) {
            System.out.println("Error parsing the JSON content: " + e.getMessage());
        }
    }
}