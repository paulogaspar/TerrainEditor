/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pt.mapi.computergrapics;

/**
 *
 * @author Paulo
 */
public class PerlinNoise
{
    // got code and adapted from: http://www.dreamincode.net/forums/topic/66480-perlin-noise/

    public static float noise(float x, float y) {
        double floorx = (double) ((int) x);//This is kinda a cheap way to floor a float integer.
        double floory = (double) ((int) y);
        double s, t, u, v;//Integer declaration
        s = findnoise2(floorx, floory);
        t = findnoise2(floorx + 1, floory);
        u = findnoise2(floorx, floory + 1);//Get the surrounding pixels to calculate the transition.
        v = findnoise2(floorx + 1, floory + 1);
        double int1 = interpolate1(4*s, t, x - floorx);//Interpolate between the values.
        double int2 = interpolate1(u, v, x - floorx);//Here we use x-floorx, to get 1st dimension. Don't mind the x-floorx thingie, it's part of the cosine formula.
        return (float) interpolate1(int1, int2, y - floory);//Here we use y-floory, to get the 2nd dimension.
    }

    public static double findnoise2(double x, double y) {
        int n = (int) x + (int) y * 57;
        n = (n << 13) ^ n;
        int nn = (n * (n * n * 60493 + 19990303) + 1376312589) & 0x7fffffff;
        return 1.0 - ((float) nn / 1073741824.0);
    }

    public static double interpolate1(double a, double b, double x) {
        double ft = x * 3.1415927;
        double f = (1.0 - Math.cos(ft)) * 0.5;
        return a * (1.0 - f) + b * f;
    }

}
