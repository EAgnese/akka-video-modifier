import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PythonScriptRunner {
    public static void main(String[] args) {
        try {
            // Chemin vers le script Python à exécuter
            String pythonScriptPath = "../../subtitles.py";
            String imagePath = "../../images/SWMG.png";
            String subtitles = "test 8000";
            String exportFolder = "../../export";


            // Construire la commande pour exécuter le script Python
            String[] cmd = {"python3", pythonScriptPath, "-p", imagePath, "-s", subtitles, "-x", exportFolder};

            // Lancer le processus externe pour exécuter le script Python
            Process process = Runtime.getRuntime().exec(cmd);

            // Récupérer le flux de sortie du processus
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            // Lire les lignes de sortie du processus
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // Attendre que le processus se termine
            int exitCode = process.waitFor();

            // Vérifier le code de sortie du processus
            if (exitCode == 0) {
                System.out.println("Le script Python s'est terminé avec succès.");
            } else {
                System.out.println("Le script Python a rencontré une erreur.");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}