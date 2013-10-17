/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pt.mapi.computergrapics;

/**
 *
 * @author Paulo Gaspar
 */
public class VertexNormal
{
    public float x;
    public float y;
    public float z;

    public VertexNormal(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void normalize()
    {
//        float max = x;
//        if (y>max) max = y;
//        if (z>max) max = z;
//
//        x /= max;
//        y /= max;
//        z /= max;

        float tmp = (float)Math.sqrt((x * x) + (y * y) + (z * z) );
	x /= tmp;
        y /= tmp;
	z /= tmp;
    }

    public void weightedSum(VertexNormal normal, float weight)
    {
        this.x += (normal.x * weight);
        this.y += (normal.y * weight);
        this.z += (normal.z * weight);
    }
}
