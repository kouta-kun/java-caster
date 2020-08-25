/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javacaster;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author kouta
 */
public class Minimap {

    public class VisibilityMap {

        private BitSet bitset;
        private Map map;
        private int height;
        private int width;

        public Boolean atPosition(int y, int x) {
            if (y > height || x > width) {
                return null;
            }
            return bitset.get(y * width + x);
        }

        public VisibilityMap(Map overMap) {
            map = overMap;
            width = overMap.getWidth();
            height = overMap.getHeight();
            bitset = new BitSet(width * height);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    bitset.set(y * width + x, false);
                }
            }
        }

        public void reportHit(int rayY, int rayX) {
            bitset.set(rayY * width + rayX, true);
        }

        public List<int[]> seenWallsList() {
            List<int[]> wallList = new ArrayList<>();
            for (int i = 0; i < bitset.size(); i++) {
                if (bitset.get(i)) {
                    int y = i / width;
                    int x = i % width;
                    int wallColor = map.atPoint(y, x);
                    wallList.add(new int[]{y, x, wallColor});
                }
            }
            return wallList;
        }
    }

    private Caster raycaster;

    public Minimap(Caster raycaster) {
        this.raycaster = raycaster;
    }

    public Caster getRaycaster() {
        return raycaster;
    }

    public void setRaycaster(Caster raycaster) {
        this.raycaster = raycaster;
    }

    public BufferedImage renderMinimap() {
        return renderMinimap(true);
    }

    public BufferedImage renderMinimap(boolean drawPos) {
        BufferedImage minimapImage = new BufferedImage(MINIMAP_WIDTH, MINIMAP_HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics = minimapImage.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, MINIMAP_WIDTH, MINIMAP_HEIGHT);
        Map map = raycaster.getMap();
        double yScale = MINIMAP_HEIGHT / map.getHeight();
        double xScale = MINIMAP_WIDTH / map.getWidth();
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                byte atPoint = map.atPoint(y, x);
                if (atPoint != 0) {
                    BufferedImage texture = (BufferedImage) Caster.WALL_TEXTURES[atPoint % Caster.WALL_TEXTURES.length];

                    int rgb = texture.getRGB(1, 1);
                    graphics.setColor(new Color(rgb));
                    graphics.fillRect((int) Math.round((x) * xScale), (int) Math.round((y) * yScale), (int) Math.round(xScale), (int) Math.round(yScale));
                }
            }
        }
        if (drawPos) {
            graphics.setColor(Color.BLACK);
            double xCenter = raycaster.getX() * xScale;
            double yCenter = raycaster.getY() * yScale;
            int x1 = (int) (xCenter - (DOTRADIUS / 2));
            int y1 = (int) (yCenter - (DOTRADIUS / 2));
            int w = (int) (DOTRADIUS);
            int h = (int) (DOTRADIUS);
            graphics.fillOval(x1, y1, w, h);
        }
        graphics.dispose();
        return minimapImage;
    }
    private static final int DOTRADIUS = 12;
    protected static final int MINIMAP_HEIGHT = 240;
    protected static final int MINIMAP_WIDTH = 320;
}
