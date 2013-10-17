/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pt.mapi.computergrapics;

/**
 *
 * @author Paulo
 */
public class Gaussian2dGenerator
{
    public static float getValueAt(int x0, int z0, int x, int z, float amplitude, float std_x, float std_z)
    {
        float x_difference = (x - x0)*(x - x0);
        float z_difference = (z - z0)*(z - z0);
        float x_variance = 2*std_x*std_x;
        float z_variance = 2*std_z*std_z;

        return amplitude * (float) Math.exp(-(x_difference/x_variance + z_difference/z_variance));
    }
}
