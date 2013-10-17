package pt.mapi.computergrapics;

import com.sun.opengl.util.BufferUtil;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.nio.ByteBuffer;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.swing.ImageIcon;

/**
 *
 * @author Paulo
 */
public final class TextureHandler
{
    private  int numTextures = 3;
    private  BufferedImage texture;
    private  int[] texturesData;
    private  int filter = 0;

    public TextureHandler(String filename)
    {
        initTexture(filename);
    }

    public void initTexture(String filename)
    {
        /* Load image. */
        Image textureImage = new ImageIcon(filename).getImage(); //new ImageIcon(TerrainEditor.class.getResource("texture5.JPG")).getImage();

        /* Convert image to buffered image. */
        BufferedImage textureBufferedImage = new BufferedImage(textureImage.getWidth(null),textureImage.getHeight(null),BufferedImage.TYPE_INT_RGB);
        Graphics textureGraphics = textureBufferedImage.getGraphics();
        textureGraphics.drawImage(textureImage, 0, 0, null);
        textureGraphics.dispose();
        texture = textureBufferedImage;
    }

    public void enableTexturing(GL gl)
    {
        //enable texturing
        gl.glEnable(gl.GL_TEXTURE_2D);

        //free textures space in opengl
        texturesData = new int[numTextures];
        gl.glGenTextures(numTextures, texturesData, 0);

        //generate textures from the texture images data
        ByteBuffer textureBytes = convertImageToTexture(texture, false);
        
        gl.glBindTexture(gl.GL_TEXTURE_2D, texturesData[0]);
        gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_MIN_FILTER, gl.GL_NEAREST);
        gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_MAG_FILTER, gl.GL_NEAREST);
        gl.glTexImage2D(gl.GL_TEXTURE_2D, 0, gl.GL_RGB, texture.getWidth(), texture.getHeight(), 0, gl.GL_RGB, gl.GL_UNSIGNED_BYTE, textureBytes);

        gl.glBindTexture(gl.GL_TEXTURE_2D, texturesData[1]);
        gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_MIN_FILTER, gl.GL_LINEAR);
        gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_MAG_FILTER, gl.GL_LINEAR);
        gl.glTexImage2D(gl.GL_TEXTURE_2D, 0, gl.GL_RGB, texture.getWidth(), texture.getHeight(), 0, gl.GL_RGB, gl.GL_UNSIGNED_BYTE, textureBytes);

        gl.glBindTexture(gl.GL_TEXTURE_2D, texturesData[2]);
        gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_MIN_FILTER, gl.GL_LINEAR_MIPMAP_NEAREST);
        gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_MAG_FILTER, gl.GL_LINEAR);
        new GLU().gluBuild2DMipmaps(gl.GL_TEXTURE_2D, gl.GL_RGB, texture.getWidth(), texture.getHeight(), gl.GL_RGB, gl.GL_UNSIGNED_BYTE, textureBytes);
    }

   /**
    * GRABBED FROM: http://www.aplweb.co.uk/Jogl/Rotation_and_texturing/
    * Convert an image into a suitable texture form
    * @param texture image
    * @param has alpha channel
    * @return texture as byte buffer
    */
    private  ByteBuffer convertImageToTexture(BufferedImage img, boolean storeAlphaChannel) {
        int[] packedPixels = new int[img.getWidth() * img.getHeight()];

        PixelGrabber pixelgrabber = new PixelGrabber(img, 0, 0, img.getWidth(), img.getHeight(), packedPixels, 0, img.getWidth());
        try {
            pixelgrabber.grabPixels();
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }

        int bytesPerPixel = storeAlphaChannel ? 4 : 3;
        ByteBuffer unpackedPixels = BufferUtil.newByteBuffer(packedPixels.length * bytesPerPixel);

        for (int row = img.getHeight() - 1; row >= 0; row--) {
            for (int col = 0; col < img.getWidth(); col++) {
                int packedPixel = packedPixels[row * img.getWidth() + col];
                unpackedPixels.put((byte) ((packedPixel >> 16) & 0xFF));
                unpackedPixels.put((byte) ((packedPixel >> 8) & 0xFF));
                unpackedPixels.put((byte) ((packedPixel >> 0) & 0xFF));
                if (storeAlphaChannel) {
                    unpackedPixels.put((byte) ((packedPixel >> 24) & 0xFF));
                }
            }
        }

        unpackedPixels.flip();

        return unpackedPixels;
    }

     void bindTexture(GL gl)
    {
        gl.glBindTexture(gl.GL_TEXTURE_2D, texturesData[filter]);
    }
    
}
