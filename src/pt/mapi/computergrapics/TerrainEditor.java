package pt.mapi.computergrapics;

import com.sun.opengl.util.Animator;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;


public class TerrainEditor
{
    private static GLCanvas canvas;
    private static Terrain terrain, water;
    private static MapReader mapReader;

    public static void main(String[] args)
    {
        /* Create windows. */
        Frame terrainFrame = new Frame("Terrain Viewer");
        TerrainControlFrame controlFrame = new TerrainControlFrame();
        new Thread(controlFrame).start();

        /* Generate terrain and water. */
        terrain = new Terrain(1025, 1025); //only powers of two for the DiamondSquare algorithm
        terrain.generateRandomTerrain(false);
        water  = new Terrain(513, 513);
        DiamondSquareNoise.setInitialRandomRange(50);
        DiamondSquareNoise.setInitialCornersValue(1);
        water.generateRandomTerrain(false);
        DiamondSquareNoise.setInitialRandomRange(750);
        DiamondSquareNoise.setInitialCornersValue(35);

        /* Generate texture. */
        TextureHandler terrainTexture = new TextureHandler("texture5.JPG");
        TextureHandler skyTexture = new TextureHandler("texture6.jpg");
        TextureHandler waterTexture = new TextureHandler("texture7.jpg");
        terrain.setTexture(terrainTexture);
        Sky.setTexture(skyTexture);
        water.setTexture(waterTexture);

        /* Prepare Open GL. */
        GLCapabilities capabilities = new GLCapabilities();
        canvas = new GLCanvas(capabilities);
        EventListener eventListener = new EventListener();
        canvas.addGLEventListener(eventListener);
        canvas.addMouseListener(eventListener);
        canvas.addMouseMotionListener(eventListener);
        canvas.addMouseWheelListener(eventListener);
        canvas.addKeyListener(eventListener);

        /* Fill windows. */
        terrainFrame.add(canvas);
        terrainFrame.setSize(640, 480);

        /* Define animator and stop behaviour. */
        final Animator animator = new Animator(canvas);
	terrainFrame.addWindowListener(new WindowAdapter()
        {
                @Override
                public void windowClosing(WindowEvent e)
                {
                  new Thread(new Runnable() {
                      public void run() {
                        animator.stop();
                        System.exit(0);
                      }
                    }).start();
                }
	});

        /* Show and center windows. */
	terrainFrame.setLocationRelativeTo(null);
        terrainFrame.setVisible(true);
        controlFrame.pack();
        controlFrame.setVisible(true);

        /* Start loop. */
        animator.start();
    }

    public static Terrain getTerrain()
    {
        return terrain;
    }

    static void setTerrain(Terrain newTerrain)
    {
        terrain = newTerrain;
    }

    public static Terrain getWater()
    {
            return water;
    }
}

