package com.ddm.app.businesslogic.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PythonScriptRunner {
    public static List<String> run(String[] cmd){
        List<String> result = new ArrayList<>();

        try {

            // Lancer le processus externe pour exécuter le script Python
            Process process = Runtime.getRuntime().exec(cmd);

            // Récupérer le flux de sortie du processus
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            // Récupérer le flux d'erreurs du processus
            InputStream errorStream = process.getErrorStream();
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));

            // Lire les lignes de sortie du processus
            String inputLine, errorLine;
            boolean remainingLines = true;
            while (remainingLines) {
                inputLine = reader.readLine();
                errorLine = errorReader.readLine();

                if (inputLine != null){
                    result.add(inputLine);
                }
                if (errorLine != null){
                    result.add(errorLine);
                }

                remainingLines = (inputLine != null) || (errorLine != null);
            }

            // Attendre que le processus se termine
            int exitCode = process.waitFor();

            // Vérifier le code de sortie du processus
            if (exitCode == 0) {
                result.add(Arrays.toString(cmd) + " end with success");
            }else {
                result.add("Error with python script : " + Arrays.toString(cmd));
            }

        } catch (IOException | InterruptedException e) {
            result.add(e.getMessage());
        }

        return result;
    }
}