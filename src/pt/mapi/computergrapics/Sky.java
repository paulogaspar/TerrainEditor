/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pt.mapi.computergrapics;

/**
 *
 * @author Paulo
 */
public class Sky
{
    private static TextureHandler texture;
    
    /**
     * @return the texture
     */
    public static TextureHandler getTexture() {
        return texture;
    }

    /**
     * @param texture the texture to set
     */
    public static void setTexture(TextureHandler texture) {
        Sky.texture = texture;
    }
}
