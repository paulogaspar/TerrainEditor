package pt.mapi.computergrapics;

/**
 *
 * @author Paulo
 */
public class Terrain
{
    private  float[][] height_matrix = null;
    private  VertexNormal[][] normals_matrix = null;
    private  int width, lenght;
    private  float max = 0, min = Float.MAX_VALUE, global_height_level;
    private  TextureHandler texture;
    private MapReader mapReader;

    public Terrain(int width1, int width2)
    {
        height_matrix = new float[width1][width2];
        normals_matrix = new VertexNormal[width1][width2];
        this.width = width1;
        this.lenght = width2;
    }

    void createHeightFromFile(String text)
    {
        assert text != null;

        mapReader = new MapReader();
        mapReader.readMap(text, width, lenght);
        height_matrix = mapReader.getMap();

        max = 0;
        min = Float.MAX_VALUE;

        for (int i = 0; i < width; i++)
            for (int j = 0; j < lenght; j++)
                if (height_matrix[i][j] > max)
                    max = height_matrix[i][j];
                else if (height_matrix[i][j] < min)
                    min = height_matrix[i][j];

        recalculateNormals(0, 0, width, lenght);
    }

    public void generateRandomTerrain(boolean usePerlinNoise)
    {
        max = 0;
        min = Float.MAX_VALUE;

        if (usePerlinNoise)
        {

            for (int i = 0; i < width; i++)
                for (int j = 0; j < lenght; j++) {
                    height_matrix[i][j] = PerlinNoise.noise(i, j);
                    if (height_matrix[i][j] > max) {
                        max = height_matrix[i][j];
                    } else if (height_matrix[i][j] < min) {
                        min = height_matrix[i][j];
                    }
                }
        }
        else
        {
            DiamondSquareNoise terrainGenerator = new DiamondSquareNoise(width, lenght);

            for (int i = 0; i < width; i++)
                for (int j = 0; j < lenght; j++) {
                    height_matrix[i][j] = terrainGenerator.getValueAt(i, j);
                    if (height_matrix[i][j] > max) {
                        max = height_matrix[i][j];
                    } else if (height_matrix[i][j] < min) {
                        min = height_matrix[i][j];
                    }
                }
        }

        recalculateNormals(0, 0, width, lenght);
    }

    public  void recalculateNormals(int x0, int z0, int x1, int z1)
    {
         /* Calculate vertices normals. */
       for (int z = z0; z<z1; z++)
            for (int x = x0; x<x1; x++)
            {
                float sx = height_matrix[x<width-1 ? x+1 : x][z] - height_matrix[x>0 ? x-1 : x][z];
                if (x == 0 || x == width-1)
                    sx *= 2;

                float sy = height_matrix[x][z<lenght-1 ? z+1 : z] - height_matrix[x][z>0 ? z-1 : z];
                if (z == 0 || z == lenght -1)
                    sy *= 2;

                normals_matrix[x][z] = new VertexNormal(-sx, 2, sy);
                normals_matrix[x][z].normalize();
            }

       /* Smooth normals. */
       float neighbourWeight = 0.5f;
       for (int z = z0; z<z1; z++)
            for (int x = x0; x<x1; x++)
            {
                VertexNormal sum = normals_matrix[x][z];

                //System.out.println("Original: " + sum.x + " " + sum.y + " " + sum.z);

                if (x>0) sum.weightedSum(normals_matrix[x-1][z], neighbourWeight);
                if (x<x1-1) sum.weightedSum(normals_matrix[x+1][z], neighbourWeight);
                if (z>0) sum.weightedSum(normals_matrix[x][z-1], neighbourWeight);
                if (z<z1-1) sum.weightedSum(normals_matrix[x][z+1], neighbourWeight);

                //System.out.println("New: " + sum.x + " " + sum.y + " " + sum.z);
                normals_matrix[x][z].normalize();
            }
    }

    public  void setHeightAt(float x, float z, float value)
    {
        assert height_matrix != null;

        height_matrix[(int)x][(int)z] = value;
    }

    public  float getHeightAt(float x, float z)
    {
        assert height_matrix != null;

        return height_matrix[(int)x][(int)z];
    }

    public  float getNormalXAt(float x, float z)
    {
        assert normals_matrix != null;

        return normals_matrix[(int)x][(int)z].x;
    }

    public  float getNormalYAt(float x, float z)
    {
        assert normals_matrix != null;

        return normals_matrix[(int)x][(int)z].y;
    }

    public  float getNormalZAt(float x, float z)
    {
        assert normals_matrix != null;

        return normals_matrix[(int)x][(int)z].z;
    }

    public  int getXLength()
    {
        return width;
    }

    public  int getZLength()
    {
        return lenght;
    }

    public  float getYLength()
    {
        return max-min;
    }

    /**
     * @return the texture
     */
    public  TextureHandler getTexture() {
        return texture;
    }

    /**
     * @param texture the texture to set
     */
    public  void setTexture(TextureHandler texture) {
        this.texture = texture;
    }

    public void setGloablHeight(float value)
    {
        global_height_level = value;
    }

    public float getGlobalHeight()
    {
        return global_height_level;
    }

    public int getWidth() {
        return width;
    }

    public int getLenght() {
        return lenght;
    }
}
