/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pt.mapi.computergrapics;

import java.awt.Image;
import java.awt.image.PixelGrabber;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

/**
 *
 * @author Paulo
 */
public final class MapReader
{
    private int[] heightMap;
    private int width, length;

    public MapReader() {}

    public float[][] getMap()
    {
        float[][] result = new float[width][length];

        for (int i = 0; i < width; i++)
            for (int j = 0; j < length; j++)
                result[i][j] = (float) heightMap[i*width + j];

        return result;
    }

    public void readMap(String strName, int width, int lenght)
    {
        assert strName != null;

        this.width = width;
        this.length = lenght;
        heightMap = new int[width * lenght];
        loadRawFile(strName, heightMap);

        for (int i=0; i<10; i++)
            System.out.println(heightMap[i]);
    }

    private void loadRawFile(String strName, int[] pHeightMap) {
        try
        {
            Image image = new ImageIcon(strName).getImage();
            int[] pixels = new int[width*length];
            PixelGrabber pxg = new PixelGrabber(image, 0, 0, width, length, pHeightMap, 0, width);
            pxg.grabPixels();

//            InputStream input = new FileInputStream(strName);
//            readBuffer(input, pHeightMap);
//            input.close();
        }
        catch (Exception ex) {
            Logger.getLogger(MapReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        

        for (int i = 0; i < pHeightMap.length; i++)
            pHeightMap[i] &= 0xFF;                 //Quick fix
    }

    private static void readBuffer(InputStream in, byte[] buffer) throws IOException {
        int bytesRead = 0;
        int bytesToRead = buffer.length;
        
        while (bytesToRead > 0) {
            int read = in.read(buffer, bytesRead, bytesToRead);
            bytesRead += read;
            bytesToRead -= read;
        }
    }
}
