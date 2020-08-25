/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javacaster;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.logging.Logger;

/**
 *
 * @author kouta
 */
public class Caster {

    private static final int WIDTH = 320;
    private static final int HEIGHT = 240;
    private static final int HALF_HEIGHT = HEIGHT / 2;

    private static final double FOV = 60.0;
    private static final double HALF_FOV = FOV / 2;
    private static final double INCREMENT_ANGLE = FOV / (double) Caster.WIDTH;

    public static final Image[] WALL_TEXTURES = {
        TextureCache.wall()
    };

    private final Map map;

    public Caster(Map map) {
        this.map = map;
    }

    private double x = 2.0;
    private double y = 2.0;

    private double angle = 90.0;

    public void turnLeft() {
        angle -= TURN_SPEED;
        if (repaintListener != null) {
            repaintListener.invoke(this);
        }
    }

    public void turnRight() {
        angle += TURN_SPEED;
        if (repaintListener != null) {
            repaintListener.invoke(this);
        }
    }

    public void forward() {
        double xStep = Math.cos(toRad(angle)) / MOVE_SPEED;
        double yStep = Math.sin(toRad(angle)) / MOVE_SPEED;
        double newY = y + yStep;
        double newX = x + xStep;
        if (newY > 0 && newY < map.grid.length && newX > 0 && newX < map.grid[(int) Math.floor(newY)].length && map.grid[(int) Math.floor(newY)][(int) Math.floor(newX)] == 0) {
            x = newX;
            y = newY;
        }
        if (repaintListener != null) {
            repaintListener.invoke(this);
        }
    }

    public void backward() {
        double xStep = Math.cos(toRad(angle)) / MOVE_SPEED;
        double yStep = Math.sin(toRad(angle)) / MOVE_SPEED;
        double newY = y - yStep;
        double newX = x - xStep;
        if (newY > 0 && newY < map.grid.length && newX > 0 && newX < map.grid[(int) Math.floor(newY)].length && map.grid[(int) Math.floor(newY)][(int) Math.floor(newX)] == 0) {
            x = newX;
            y = newY;
        }
        if (repaintListener != null) {
            repaintListener.invoke(this);
        }
    }

    public static final double TURN_SPEED = 5.0;
    public static final double MOVE_SPEED = 3.0;

    private Minimap minimap = null;

    public BufferedImage render() {
        BufferedImage bufImg = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics = bufImg.createGraphics();
        graphics.fillRect(0, 0, WIDTH, HEIGHT);
        renderFPScene(graphics);
        Minimap minimapSingleton = getMinimap();
        BufferedImage minimapImage = minimapSingleton.renderMinimap();
        graphics.drawImage(minimapImage, WIDTH - (WIDTH / 4), HEIGHT / 10, WIDTH / 5, HEIGHT / 4, null);
        graphics.setColor(Color.BLACK);
        graphics.drawRect(WIDTH - (WIDTH / 4), HEIGHT / 10, WIDTH / 5, HEIGHT / 4);
        return bufImg;
    }

    public Minimap getMinimap() {
        if (minimap == null) {
            minimap = new Minimap(this);
        }
        return minimap;
    }

    private static final int LIGHTING_PRECISION = 4;

    private void renderFPScene(Graphics2D graphics) {
        // Calculate lighting map (2)
        float[][] lightmap = calculateLightMap();
        double rayAngle = angle - HALF_FOV;
        for (int i = 0; i < WIDTH; i++) {
            // Ray data
            double rayX = x, rayY = y;

            // Ray path increments
            double xStep = Math.cos(toRad(rayAngle)) / PRECISION;
            double yStep = Math.sin(toRad(rayAngle)) / PRECISION;

            int mul = findWall(rayY, yStep, rayX, xStep);

            rayX = rayX + xStep * mul;
            rayY = rayY + yStep * mul;

            // Pythagoras theorem
            double distance = Math.sqrt(Math.pow(Math.abs((x - rayX)), 2.0) + Math.pow(Math.abs((y - rayY)), 2.0));

            // Fisheye
            distance = distance * Math.cos(toRad(rayAngle - angle));
            // Wall Height
            int wallHeight = (int) ((HALF_HEIGHT) / distance);

            int floorHSize = drawSky(graphics, wallHeight, i);

            drawWall(rayY, rayX, wallHeight, i, graphics, floorHSize);

            drawFloor(rayX, rayY, floorHSize, lightmap, graphics, i, wallHeight);

            // Increment
            rayAngle += INCREMENT_ANGLE;
        }
    }

    private int findWall(double rayY, double yStep, double rayX, double xStep) {
        double rayYIncrement = 0;
        double rayXIncrement = 0;
        byte[][] wallMap = map.grid;
        do {
            rayYIncrement += yStep * 1.5;
            rayXIncrement += xStep * 1.5;
        } while (wallMap[(int) Math.floor(rayY + rayYIncrement)][(int) Math.floor(rayX + rayXIncrement)] == 0);
        return (int) Math.floor(rayYIncrement / yStep);
    }

    private void drawFloor(double rayX, double rayY, int floorHSize, float[][] lightmap, Graphics2D graphics, int i, int wallHeight) {
        // Draw floor (1)
        double deltaX = rayX - x;
        double deltaY = rayY - y;
        for (int yFloor = 0; yFloor < floorHSize; yFloor++) {
            double normSY = ((double) floorHSize - yFloor) / (double) floorHSize;
            int sampleX = (int) Math.round((x + (normSY * deltaX)) * LIGHTING_PRECISION);
            int sampleY = (int) Math.round((y + (normSY * deltaY)) * LIGHTING_PRECISION);

            float lightLevel = Math.max(Math.min(lightmap[sampleY][sampleX], 1.0f), 0);

            graphics.setColor(new Color(0, (int) Math.floor(255.0f * lightLevel), 0));

            graphics.drawRect(i, HALF_HEIGHT + wallHeight + yFloor, 1, 1);
        }
    }

    private void drawWall(double rayY, double rayX, int wallHeight, int i, Graphics2D graphics, int floorHSize) {
        // Draw wall
        // get texture
        final int wallColor = (map.atPoint((int) Math.floor(rayY), (int) Math.floor(rayX)) % WALL_TEXTURES.length);
        BufferedImage imageTexture = (BufferedImage) WALL_TEXTURES[wallColor];

        for (int y = 0; y < wallHeight * 2; y++) {
            // we draw 1px using ycoord and 1px using xcoord bc i cant find a way to "rotate" the vector, fixing this is a TODO
            int interleaveOffset = i + y;
            boolean parityBit = (interleaveOffset & 1) == 1;

            // this is like a multiplexer or a selector, we choose rayX when we drawing an even pixel and rayY when we drawing an odd pixel
            double xCoord = parityBit ? rayX : rayY;
            xCoord = xCoord - Math.floor(xCoord);

            int sampleX = (int) ((xCoord) * imageTexture.getWidth());
            double yCoord = ((double) y) / (double) (wallHeight * 2);
            int sampleY = (int) ((yCoord) * imageTexture.getHeight());

            int sample = imageTexture.getRGB(sampleX, sampleY);

            graphics.setColor(new Color(sample));

            // we draw pixel by pixel, should fix this but too lazy
            graphics.drawRect(i, floorHSize + y, 1, 1);
        }
    }

    private int drawSky(Graphics2D graphics, int wallHeight, int i) {
        // Draw sky
        graphics.setColor(Color.CYAN);
        int floorHSize = HALF_HEIGHT - wallHeight;
        graphics.drawLine(i, 0, i, floorHSize);
        return floorHSize;
    }

    private float[][] calculateLightMap() {
        int lmH = map.getHeight() * LIGHTING_PRECISION;
        int lmW = map.getWidth() * LIGHTING_PRECISION;
        float[][] lightMap = new float[lmH][lmW];
        for (int yL = 0; yL < lmH; yL++) {
            for (int xL = 0; xL < lmW; xL++) {
                lightMap[yL][xL] = BASE_LIGHTING_LEVEL;
            }
        }
        for (Integer[] lightSource : getLightPointSet()) {
            double yS = lightSource[0] * LIGHTING_PRECISION;
            double xS = lightSource[1] * LIGHTING_PRECISION;
            for (int yL = 0; yL < lmH; yL++) {
                for (int xL = 0; xL < lmW; xL++) {
                    double yDelta = (yL - yS) / (double) LIGHTING_PRECISION;
                    double xDelta = (xL - xS) / (double) LIGHTING_PRECISION;
                    lightMap[yL][xL] += Math.max(1.0f - (DROPOFF * Math.sqrt((yDelta * yDelta) + (xDelta * xDelta))), 0);
                }
            }
        }
        return lightMap;
    }
    private static final float BASE_LIGHTING_LEVEL = 0.25f;
    private static final float DROPOFF = 0.4f;

    private HashSet<Integer[]> getLightPointSet() {
        HashSet<Integer[]> lightPoints = new HashSet<>();
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                if (map.lightAtPoint(y, x)) {
                    lightPoints.add(new Integer[]{y, x});
                }
            }
        }
        return lightPoints;
    }
    private static final Logger LOG = Logger.getLogger(Caster.class.getName());

    public Map getMap() {
        return map;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getAngle() {
        return angle;
    }

    RepaintListener repaintListener = null;

    public void setRepaintListener(RepaintListener listener) {
        repaintListener = null;
    }

    public static double toRad(double rayAngle) {
        return rayAngle * Math.PI / 180.0;
    }

    private static final int PRECISION = 256;

    public static interface RepaintListener {

        void invoke(Caster caster);
    }
}
