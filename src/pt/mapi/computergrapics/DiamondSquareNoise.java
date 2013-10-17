package pt.mapi.computergrapics;

import java.util.Random;

/**
 *
 * @author Paulo Gaspar
 */
public class DiamondSquareNoise
{
    private static float INITIAL_RANGE = 750;
    private static int DIVISOR = 2;
    private static int INITIAL_CORNER_VALUE = 35;
    private float[][] pix = null;
    private int width, height;
    private Random rand;

    public DiamondSquareNoise(int width, int height)
    {
        this.width = width;
        this.height = height;
        pix = new float[width][height];
        rand = new Random();
        generateNoise();
    }

    public float getValueAt(int x, int y)
    {
        assert pix != null;

        return pix[x][y];
    }

    public static void setInitialRandomRange(float range)
    {
        INITIAL_RANGE = range;
    }

    public static void setInitialCornersValue(int value)
    {
        INITIAL_CORNER_VALUE = value;
    }

    private void generateNoise()
    {
        float range = INITIAL_RANGE;
        int step = width - 1;
        setp(0, 0, INITIAL_CORNER_VALUE);
        setp(width - 1, 0, INITIAL_CORNER_VALUE);
        setp(0, height - 1, INITIAL_CORNER_VALUE);
        setp(width - 1, height - 1, INITIAL_CORNER_VALUE);
        while (step > 1) {
            //diamond
            for (int x = 0; x < width - 1; x += step) {
                for (int y = 0; y < height - 1; y += step) {
                    int sx = x + (step >> 1);
                    int sy = y + (step >> 1);
                    int[][] points = {
                        {x, y},
                        {x + step, y},
                        {x, y + step},
                        {x + step, y + step},};
                    computecolor(sx, sy, points, range);
                }
            }
            //square
            for (int x = 0; x < width - 1; x += step) {
                for (int y = 0; y < height - 1; y += step) {
                    int halfstep = step >> 1;
                    int x1 = x + halfstep;
                    int y1 = y;
                    int x2 = x;
                    int y2 = y + halfstep;
                    int[][] points1 = {
                        {x1 - halfstep, y1},
                        {x1, y1 - halfstep},
                        {x1 + halfstep, y1},
                        {x1, y1 + halfstep}
                    };
                    int[][] points2 = {
                        {x2 - halfstep, y2},
                        {x2, y2 - halfstep},
                        {x2 + halfstep, y2},
                        {x2, y2 + halfstep}
                    };
                    computecolor(x1, y1, points1, range);
                    computecolor(x2, y2, points2, range);
                }
            }
            range /= DIVISOR;
            step >>= 1;
        }

//        for (int i = 0; i < width; i++) {
//            for (int j = 0; j < width; j++) {
//                set(i, j, 1);
//            }
//        }
    }

    void computecolor(int x, int y, int[][] points, float range) {
        float c = 0;
        for (int i = 0; i < 4; i++) {
            if (points[i][0] < 0) {
                points[i][0] += (width - 1);
            } else if (points[i][0] > width) {
                points[i][0] -= (width - 1);
            } else if (points[i][1] < 0) {
                points[i][1] += height - 1;
            } else if (points[i][1] > height) {
                points[i][1] -= height - 1;
            }
            c += pix[points[i][0]][points[i][1]] / 4.;
        }
        c += (rand.nextFloat()*range - range / 2);
        if (c < 0) {
            c = 0;
        } else if (c > 255) {
            c = 255;
        }
        setp(x, y, c);
        if (x == 0) {
            setp(width - 1, y, c);
        } else if (x == width - 1) {
            setp(0, y, c);
        } else if (y == 0) {
            setp(x, height - 1, c);
        } else if (y == height - 1) {
            setp(x, 0, c);
        }
    }

    //set a pixel value
    void setp(int x, int y, float c)
    {
        pix[x][y] = c;
    }
}
