/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javacaster;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kouta
 */
public class Map {

    /*    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        StringReader sr = new StringReader("MAP :: [3, 5];\nPAL :: [2]; PAL[0@2] <- 0; PAL[0] <- 0;");
        try {
            loadMap(sr);
        } catch (IOException ex) {
            Logger.getLogger(Map.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/
    public static Map emptyMap(int mapSize) {
        byte[][] map = new byte[mapSize][mapSize];
        byte[][] lightmap = new byte[mapSize][mapSize];
        for (int x = 0; x < mapSize; x++) {
            for (int y = 0; y < mapSize; y++) {
                map[y][x] = 0;
                lightmap[y][x] = 0;
            }
        }
        for (int i = 0; i < mapSize; i++) {
            map[0][i] = 1;
            map[i][0] = 1;
            map[mapSize - 1][i] = 1;
            map[i][mapSize - 1] = 1;
        }

        for (int i = 3; i < 7; i++) {
            map[3][i] = 1;
            map[i][3] = 1;
            map[6][i] = 1;
            map[i][6] = 1;
        }
        lightmap[1][1] = 1;
        lightmap[mapSize - 2][mapSize - 2] = 1;
        lightmap[mapSize - 2][1] = 1;
        lightmap[1][mapSize - 2] = 1;
        return new Map(map, lightmap);
    }

    private static byte decHexUnit(char charAt) {
        if (charAt >= '0' && charAt <= '9') {
            return (byte) (charAt - '0');
        } else if (charAt >= 'A' && charAt <= 'F') {
            return (byte) ((charAt - 'A') + 10);
        } else {
            throw new UnknownError("not in hex range");
        }
    }

    public void set(int ycoord, int xcoord, byte b) {
        this.grid[ycoord][xcoord] = b;
    }

    boolean lightAtPoint(int y, int x) {
        return this.lightMap[y][x] != 0;
    }

    private class MapDefinition {

        private final byte[][] mapGrid;
        private final byte[] mapPalette;

        public MapDefinition(byte[][] mapGrid, byte[] mapPalette) {
            this.mapGrid = mapGrid;
            this.mapPalette = mapPalette;
        }

        public byte[][] getMapGrid() {
            return mapGrid;
        }

        public byte[] getMapPalette() {
            return mapPalette;
        }

    };

    public static Map loadMapSimple(Scanner sc) {
        List<String> mapDef = new ArrayList<>();

        while (sc.hasNextLine()) {
            mapDef.add(sc.nextLine());
        }

        int length = mapDef.get(0).length();

        for (int i = 1; i < mapDef.size(); i++) {
            if (mapDef.get(i).length() != length) {
                return null;
            }
        }

        byte[][] map = new byte[mapDef.size()][length];
        for (int i = 0; i < mapDef.size(); i++) {
            String mapLine = mapDef.get(i);
            for (int ci = 0; ci < mapLine.length(); ci++) {
                map[i][ci] = decHexUnit(mapLine.charAt(ci));
            }
        }

        byte[][] lightmap = new byte[map.length][map[0].length];
        for (byte[] lightmap1 : lightmap) {
            for (int ii = 0; ii < lightmap1.length; ii++) {
                lightmap1[ii] = 0;
            }
        }

        return new Map(map, lightmap);
    }

    final byte[][] grid;
    final byte[][] lightMap;
    private final int width;
    private final int height;

    public Map(byte[][] grid, byte[][] lightMap) {
        height = grid.length;
        width = grid[0].length;
        for (int y = 0; y < height; y++) {
            assert grid[y].length == width;
        }
        this.grid = grid;
        this.lightMap = lightMap;
    }

    public byte atPoint(int y, int x) {
        return grid[y][x];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}
