package com.gakk.noorlibrary.util;

import static com.gakk.noorlibrary.util.ConstantsKt.IMAGES_BASE_URL;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class CustomMapTileProvider implements TileProvider {
    private static final int TILE_WIDTH = 256;
    private static final int TILE_HEIGHT = 256;
    private static final int BUFFER_SIZE = 16 * 1024;

    public CustomMapTileProvider() {

    }

    @Nullable
    @Override
    public Tile getTile(int i, int i1, int i2) {
        i1 = fixYCoordinate(i1, i2);
        byte[] image = readTileImage(i, i1, i2);
        return image == null ? null : new Tile(TILE_WIDTH, TILE_HEIGHT, image);
    }

    /**
     * Fixing tile's y index (reversing order)
     */
    private int fixYCoordinate(int y, int zoom) {
        int size = 1 << zoom;
        return size - 1 - y;
    }

    private byte[] readTileImage(int x, int y, int zoom) {
        InputStream in = null;
        ByteArrayOutputStream buffer = null;

        try {

            getTileFilename(x, y, zoom);

            String imagesUrl = IMAGES_BASE_URL + "HajjGuide/" + "" + x + "" + y + ".webp";
            in = new URL(imagesUrl).openStream();
            buffer = new ByteArrayOutputStream();

            int nRead;
            byte[] data = new byte[BUFFER_SIZE];

            while ((nRead = in.read(data, 0, BUFFER_SIZE)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();

            return buffer.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        } finally {
            if (in != null) try {
                in.close();
            } catch (Exception ignored) {
            }
            if (buffer != null) try {
                buffer.close();
            } catch (Exception ignored) {
            }
        }
    }

    private String getTileFilename(int x, int y, int zoom) {
        Log.e("MyTile", "map_" + zoom + '_' + x + '_' + y + ".png");
        return "map_" + zoom + '_' + x + '_' + y + ".png";
    }
}
