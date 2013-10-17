package pt.mapi.computergrapics;

import com.sun.opengl.util.GLUT;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

/**
 *
 * @author Paulo
 */
public class EventListener implements GLEventListener, MouseListener, KeyListener, MouseMotionListener, MouseWheelListener
{
    protected float pyramidRotation;
    protected float cubeRotation;

    protected int x_length, z_length;
    private float y_length;
    protected final Random rand = new Random();
    private float rotation_y, rotation_x;
    private static float zoom;

    private Terrain terrain, water;
    private FloatBuffer vertices = null;
    private FloatBuffer normals = null;
    private int vertice_count;
    private GLUT glut;
    private GLU glu;

    private MouseEvent pressed_mouse, dragged_mouse = null;
    private double[] originalCoord = new double[2];
    private boolean firstClick = false;

    /**********************************************************************************/
    /*                                    openGL EVENTS                               */
    /**********************************************************************************/


    public void init(GLAutoDrawable drawable)
    {
        final GL gl = drawable.getGL();
        glut = new GLUT();
        
        terrain = TerrainEditor.getTerrain();
        water = TerrainEditor.getWater();

        /* GL parameters. */
        gl.glShadeModel(GL.GL_SMOOTH);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glClearDepth(1.0f);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LEQUAL);
        gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);

        LightController.setLight(gl);

//        vertice_count = (TerrainEditor.getTerrain().getXLength()-1) * (TerrainEditor.getTerrain().getZLength()-1) * 6; //width * length * nr_vertices_in_square
//        vertices = BufferUtil.newFloatBuffer(vertice_count * 3); //  nr_vertices * nr_axis_per_vertice
//        normals = BufferUtil.newFloatBuffer(vertice_count * 3);
        //loadVertices(gl, 0, 0, TerrainEditor.getTerrain().getXLength(), TerrainEditor.getTerrain().getZLength());
        
        zoom = -terrain.getZLength();
        water.setGloablHeight(-terrain.getZLength()-10);
        rotation_y = 45.0f;
        rotation_x = 25.0f;

        /* Enable texturing. */
        terrain.getTexture().enableTexturing(gl);
        water.getTexture().enableTexturing(gl);
        Sky.getTexture().enableTexturing(gl);
    }

    public void display(GLAutoDrawable drawable)
    {
        final GL gl = drawable.getGL();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        /* Control light. */
        if (LightController.lightChanged())
            LightController.setLight(gl);

        /* Get terrain, just in case there are modifications on the model. */
        terrain = TerrainEditor.getTerrain();
        x_length = terrain.getXLength();
        z_length = terrain.getZLength();
        y_length = terrain.getYLength();

        /* Draw scene. */
        if (TerrainControlFrame.drawSky.isSelected()) drawSky(gl);
        if (TerrainControlFrame.drawWater.isSelected()) drawWater(gl);
        drawTerrain(gl);

        if (pressed_mouse != null)
        {
            /* Save original click coordinates. */
            if (firstClick)
            {
                MouseEvent selectedMouse = pressed_mouse;
                if (dragged_mouse != null)
                    selectedMouse = dragged_mouse;
                
                int mx = selectedMouse.getX();
                int my = selectedMouse.getY();

                double[] worldpos = GetOGLPos(mx, my, gl);
                double[] originalWorldPos = GetOGLPos(pressed_mouse.getX(), pressed_mouse.getY(), gl);
                originalCoord[0] = worldpos[0];
                originalCoord[1] = worldpos[2];
                firstClick = false;
            }

            /* Generate elevantion in that spot. */
            if (pressed_mouse.getButton() == pressed_mouse.BUTTON1)
                ElevationGenerator.createElevation(terrain, (int)originalCoord[0], (int)originalCoord[1], x_length, z_length, true);
            if (pressed_mouse.getButton() == pressed_mouse.BUTTON3)
                ElevationGenerator.createElevation(terrain, (int)originalCoord[0], (int)originalCoord[1], x_length, z_length, false);
        }
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
    {
        final GL gl = drawable.getGL();
        glu = new GLU();

        gl.setSwapInterval(1);

        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();

        //glu.gluPerspective(45.0f, (double) width / (double) height, 0.1f, 10000.0f);
        glu.gluPerspective(40.0f, 16/9, 0.1f, 10000.0f);

        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

   public double[] GetOGLPos(int x, int y, GL gl)
    {
        int viewport[] = new int[4];
        double modelview[] = new double[16];
        double projection[] = new double[16];
        float winX, winY, winZ = 0;
	double posX, posY, posZ;
        double wcoord[] = new double[4];
        ByteBuffer zdepth =  ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder());
        double wcoord2[] = new double[4];

        gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
        gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, modelview, 0);
        gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, projection, 0);

	winX = (float)x;
	winY = (float)viewport[3] - (float)y; //viewport[3] is the height of the window
	gl.glReadPixels(x, (int) winY, 1, 1, GL.GL_DEPTH_COMPONENT, GL.GL_FLOAT, zdepth );
        glu.gluUnProject(winX, winY, (float) (zdepth.getFloat(0)), modelview, 0, projection, 0, viewport, 0, wcoord, 0);

	return wcoord;
    }

    private void drawTerrain(GL gl)
    {
        gl.glLoadIdentity();

        /* bind the texture */
        terrain.getTexture().bindTexture(gl);

        /* Transformations. Don't forget the reverse order!! */
        gl.glTranslatef(0, -y_length/2 , zoom); //making the object further away (last)
        gl.glRotatef(rotation_x, 1.0f, 0.0f, 0.0f); //rotating the object (third)
        gl.glRotatef(rotation_y, 0.0f, 1.0f, 0.0f); //rotating the object (second)
        gl.glTranslatef(-x_length/2, -y_length/2 , -z_length/2); //centering the object (first)

        /* Draw object. */
        for (float j=0; j<z_length-1; j++)
        {
            gl.glBegin(GL.GL_TRIANGLE_STRIP);
            for (float i=0; i<x_length; i++)
            {
                gl.glNormal3f(terrain.getNormalXAt(i, j), terrain.getNormalYAt(i, j), terrain.getNormalZAt(i, j));
                gl.glTexCoord2f(i / x_length, j / z_length);
                gl.glVertex3f(i, terrain.getHeightAt(i,j), j);
                
                gl.glNormal3f(terrain.getNormalXAt(i, j+1), terrain.getNormalYAt(i, j+1), TerrainEditor.getTerrain().getNormalZAt(i, j+1));
                gl.glTexCoord2f(i / x_length, (j+1) / z_length);
                gl.glVertex3f(i, terrain.getHeightAt(i,j+1), j+1);
            }
            gl.glEnd();
        }
    }

    private void drawWater(GL gl)
    {
        gl.glLoadIdentity();

        /* bind the texture */
        water.getTexture().bindTexture(gl);

        /* Make transformations. */
        gl.glTranslatef(0, -y_length/3 + water.getGlobalHeight() , zoom); //making the object further away (last)
        gl.glRotatef(rotation_x, 1.0f, 0.0f, 0.0f); //rotating the object (second)
        gl.glRotatef(rotation_y, 0.0f, 1.0f, 0.0f); //rotating the object (second)
        gl.glTranslatef(-x_length/2, -y_length/2 , -z_length/2); //centering the object (first)

        /* Draw object. */
        float water2terrainScale = (terrain.getXLength()-1) / (water.getXLength()-1); //draw water the same size as the terrain
        for (float j=0; j<water.getZLength()-1; j++)
        {
            gl.glBegin(GL.GL_TRIANGLE_STRIP);
            for (float i=0; i<water.getXLength(); i++)
            {
                gl.glNormal3f(water.getNormalXAt(i, j), water.getNormalYAt(i, j), water.getNormalZAt(i, j));
                gl.glTexCoord2f(i / water.getXLength(), j / water.getZLength());
                gl.glVertex3f(i*water2terrainScale, water.getHeightAt(i,j), j*water2terrainScale);

                gl.glNormal3f(water.getNormalXAt(i, j+1), water.getNormalYAt(i, j+1), TerrainEditor.getTerrain().getNormalZAt(i, j+1));
                gl.glTexCoord2f(i / water.getXLength(), (j+1) / water.getZLength());
                gl.glVertex3f(i*water2terrainScale, water.getHeightAt(i,j+1), (j+1)*water2terrainScale);
            }
            gl.glEnd();
        }
    }

    private void drawSky(GL gl)
    {
        gl.glLoadIdentity();

        /* bind the texture */
        Sky.getTexture().bindTexture(gl);

        gl.glTranslatef(-1.8f*x_length/2, -0.3f*x_length/2, zoom*1.35f);

        gl.glBegin(GL.GL_QUADS);

        gl.glTexCoord2f(0, 0);
        gl.glNormal3f(0,0,60);
        gl.glVertex3f(0, -100, -2);

        gl.glTexCoord2f(0, 1);
        gl.glNormal3f(0,0,0.5f);
        gl.glVertex3f(0, 0.8f*x_length, -2);

        gl.glTexCoord2f(1, 1);
        gl.glNormal3f(0,0,0.5f);
        gl.glVertex3f(1.8f*x_length, 0.8f*x_length, -2);

        gl.glTexCoord2f(1, 0);
        gl.glNormal3f(0,0, 60);
        gl.glVertex3f(1.8f*x_length, -100, -2);

        gl.glEnd();
    }

//    private void vertexArrayDraw(GL gl)
//    {
//        /* Any change in model? Redraw model. */
//        if (changeOccured)
//        {
//            loadVertices(gl, 0, 0, TerrainEditor.getTerrain().getXLength(), TerrainEditor.getTerrain().getZLength());
//            changeOccured = false;
//        }
//
//        /* Geometry */
//        gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
//        gl.glEnableClientState(GL.GL_NORMAL_ARRAY );
//        gl.glVertexPointer(3, GL.GL_FLOAT, 0, vertices);
//        gl.glNormalPointer(GL.GL_FLOAT, 0, normals);
//        gl.glDrawArrays(GL.GL_TRIANGLES, 0, vertice_count);
//        gl.glDisableClientState(GL.GL_NORMAL_ARRAY );
//        gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
//    }
//
//    private void loadVertices(GL gl, int x0, int z0, int x1, int z1)
//    {
//        int nX = 0, nZ, nTri = 0, index = 0;						// Create Variables
//	float flX = 0, flZ = 0;
//	for( nZ = z0; nZ < z1-1; nZ++ )
//            for( nX = x0; nX < x1-1; nX++ )
//                for( nTri = 0; nTri < 6; nTri++ )
//                {
//                    flX = (float) nX + ( ( nTri == 1 || nTri == 2 || nTri == 5 ) ? 1 : 0.0f );
//                    flZ = (float) nZ + ( ( nTri == 2 || nTri == 4 || nTri == 5 ) ? 1 : 0.0f );
//
//                    index = 18 * (terrain.getXLength()-1) * nZ + 18*nX + 3*nTri;
//
//                    vertices.put(index, flX);
//                    vertices.put(index+1, terrain.getHeightAt(flX, flZ));
//                    vertices.put(index+2, flZ);
//
//                    normals.put(index, terrain.getNormalXAt(flX, flZ));
//                    normals.put(index+1, terrain.getNormalYAt(flX, flZ));
//                    normals.put(index+2, terrain.getNormalZAt(flX, flZ));
//                }
//
//       // vertices.flip();
//       // normals.flip();
//        System.out.println("LoadVertices");
//    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {}

    public static void setZoom(float newZoom)
    {
        zoom = newZoom;
    }

    public static float getZoom()
    {
        return zoom;
    }

    /**********************************************************************************/
    /*                                     MOUSE EVENTS                               */
    /**********************************************************************************/

    public void mousePressed(MouseEvent e)
    {
        pressed_mouse = e;
        firstClick = true;
    }
    
    public void mouseReleased(MouseEvent e) 
    {
        pressed_mouse = null;
        dragged_mouse = null;
        firstClick = false;
    }

    public void mouseWheelMoved(MouseWheelEvent e)
    {
        int notches = e.getWheelRotation();
        zoom -= 13*notches;
    }

    public void mouseDragged(MouseEvent e) 
    {
        dragged_mouse = e;
        firstClick = true;
    }
    
    public void mouseMoved(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseClicked(MouseEvent e){}

    /**********************************************************************************/
    /*                                     KEY EVENTS                                 */
    /**********************************************************************************/

    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode())
        {
            case KeyEvent.VK_ESCAPE:
                System.exit(0);
                break;
            case KeyEvent.VK_LEFT:
                rotation_y -= 5;
                break;
            case KeyEvent.VK_RIGHT:
                rotation_y += 5;
                break;
            case KeyEvent.VK_UP:
                rotation_x -= 5;
                break;
            case KeyEvent.VK_DOWN:
                rotation_x += 5;
                break;
        }
    }

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}

}
