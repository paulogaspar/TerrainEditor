package pt.mapi.computergrapics;

/**
 *
 * @author Paulo
 */
public class ElevationGenerator
{
    private static int amplitude = 10;
    private static int standard_dev_x = 40;
    private static int standard_dev_z = 40;

    /* Last influence field. */
    public static int x0 = 0;
    public static int x1 = TerrainEditor.getTerrain().getXLength();
    public static int z0 = 0;
    public static int z1 = TerrainEditor.getTerrain().getZLength();

    public static void createElevation(Terrain terrain, int initial_x, int initial_z, int x_length, int z_length, boolean elevate)
    {
        /* Calculate 99.6% influence field. */
        int dev_x = 4*standard_dev_x;
        int dev_z = 4*standard_dev_z;
        x0 = (initial_x - dev_x) < 0? 0 : initial_x - dev_x;
        z0 = (initial_z - dev_z) < 0? 0 : initial_z - dev_z;
        x1 = (initial_x + dev_x) > x_length? x_length : initial_x + dev_x;
        z1 = (initial_z + dev_z) > z_length? z_length : initial_z + dev_z;

        /* Elevate field in influence zone. */
        if (elevate)
            for (int x = x0; x < x1; x++)
                for (int z = z0; z < z1; z++)
                {
                    float gaussValue = Gaussian2dGenerator.getValueAt(initial_x, initial_z, x, z, amplitude, standard_dev_x, standard_dev_z);
                    TerrainEditor.getTerrain().setHeightAt(x, z, gaussValue + TerrainEditor.getTerrain().getHeightAt(x, z));
                }
        else
            for (int x = x0; x < x1; x++)
                for (int z = z0; z < z1; z++)
                {
                    float gaussValue = Gaussian2dGenerator.getValueAt(initial_x, initial_z, x, z, amplitude, standard_dev_x, standard_dev_z);
                    TerrainEditor.getTerrain().setHeightAt(x, z, TerrainEditor.getTerrain().getHeightAt(x, z) - gaussValue);
                }

        /* Recalculate normals of the vertex in the influence zone. */
        TerrainEditor.getTerrain().recalculateNormals(x0, z0, x1, z1);
    }

    /**
     * @param aAmplitude the amplitude to set
     */
    public static void setAmplitude(int aAmplitude) {
        amplitude = aAmplitude;
    }

    /**
     * @param aStandard_dev_x the standard_dev_x to set
     */
    public static void setStandardDev(int std)
    {
        standard_dev_x = std;
        standard_dev_z = std;
    }   
}