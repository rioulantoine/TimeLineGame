package util;

import javafx.scene.image.Image;
import java.util.HashMap;
import java.util.Map;

public class ImageManager {


    private Map<String, Image> imageCache = new HashMap<>();

    private static ImageManager instance;

    private ImageManager() {
    }

    public Image getImage(String urlImage) {
        // Vérifier si l'image est déjà dans le cache
        if (imageCache.containsKey(urlImage)) {
            System.out.println("Image - " + urlImage);
            return imageCache.get(urlImage);
        }

        // Si l'image n'est pas dans le cache, on la charge depuis l'URL
        System.out.println("Fetch image from URL " + urlImage);
        Image newImage = new Image(urlImage);

        // Ajouter l'image au cache
        imageCache.put(urlImage, newImage);
        return newImage;
    }

    public static ImageManager getInstance() {
        if (instance == null)
            instance = new ImageManager();
        return instance;
    }
}