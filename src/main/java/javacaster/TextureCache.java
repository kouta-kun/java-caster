/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javacaster;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author kouta
 */
public class TextureCache {

    private static BufferedImage wallImage = null;

    public static Image wall() {
        if (wallImage == null) {
            try {
                wallImage = ImageIO.read(TextureCache.class.getClassLoader().getResourceAsStream("woodWall.png"));
            } catch (IOException ex) {
                Logger.getLogger(TextureCache.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }
        return wallImage;
    }
    
    public static void main(String[] args) {
        System.out.println(wall());
    }
}
