/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pt.mapi.computergrapics;

import javax.media.opengl.GL;

/**
 *
 * @author Paulo
 */
public class LightController
{
    private static float LightAmbient[] = { 0.5f, 0.5f, 0.5f, 1.0f };
    private static float LightDiffuse[] = { 0.6f, 0.6f, 0.6f, 1.0f };
    private static float LightPosition[] = { 5.0f, 10.0f, 2.0f, 0.0f };

    private static boolean lightChanged = true;

    public static void setAmbientLight(float value)
    {
        LightAmbient[0] = value;
        LightAmbient[1] = value;
        LightAmbient[2] = value;
        
        lightChanged = true;
    }

    public static void setDiffuseLight(float value)
    {
        LightDiffuse[0] = value;
        LightDiffuse[1] = value;
        LightDiffuse[2] = value;

        lightChanged = true;
    }

    public static float[] getAmbienteLight()
    {
        return LightAmbient;
    }

    public static float[] getDiffuseLight()
    {
        return LightDiffuse;
    }

    public static float[] getLightPosition()
    {
        return LightPosition;
    }
    
    public static void setLight(GL gl)
    {
        /* Light */
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, LightController.getAmbienteLight(), 0);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, LightController.getDiffuseLight(), 0);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, LightController.getLightPosition(), 0);
        gl.glEnable(GL.GL_LIGHTING);
        gl.glEnable(GL.GL_LIGHT0);

        lightChanged = false;
    }

    public static boolean lightChanged()
    {
        return lightChanged;
    }
}
