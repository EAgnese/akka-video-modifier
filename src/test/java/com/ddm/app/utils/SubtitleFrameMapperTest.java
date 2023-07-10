package com.ddm.app.utils;

import com.ddm.app.businesslogic.utils.SubtitleFrameMapper;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class SubtitleFrameMapperTest {
    @Test
    public void testSubtitleReader() {
        // Contenu du fichier de sous-titres de test
        String subtitlePath = "src/test/java/com/ddm/app/utils/subtitle-test1.txt";

        // Nombre d'images par seconde de la vidéo
        int fps = 24;

        // Création de l'instance de la classe SubtitleReader
        SubtitleFrameMapper subtitleReader = new SubtitleFrameMapper(fps, subtitlePath);

        // Récupération de la HashMap des frames et des sous-titres associés
        Map<Integer, String> framesWithSubtitles = subtitleReader.mapFramesToSubtitles();

        // Vérification des résultats attendus
        Assert.assertEquals(24+1+26+1, framesWithSubtitles.size());
        Assert.assertEquals("Salut je suis un test", framesWithSubtitles.get(0));
        Assert.assertEquals("Salut je suis un test", framesWithSubtitles.get(24));
        Assert.assertNull(framesWithSubtitles.get(25));
        Assert.assertNull(framesWithSubtitles.get(57));
        Assert.assertEquals("Ceci est un autre sous-titre", framesWithSubtitles.get(58));
        Assert.assertEquals("Ceci est un autre sous-titre", framesWithSubtitles.get(84));
        Assert.assertNull(framesWithSubtitles.get(85));
        // ... autres assertions pour les autres frames

        // ... autres tests à effectuer si nécessaire
    }
}
