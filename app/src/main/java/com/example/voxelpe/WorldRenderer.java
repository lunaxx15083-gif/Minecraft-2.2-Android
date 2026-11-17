package com.example.voxelpe;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import java.util.Random;

public class WorldRenderer implements GLSurfaceView.Renderer {
    float turn=35, look=0, forward=0, jump=0; int blocksChanged=0;
    private final int[][] heights = new int[11][11];

    public WorldRenderer(long seed) {
        Random random = new Random(seed);
        for (int x=0; x<11; x++) for (int z=0; z<11; z++) {
            int broad = (int)(2.0 * Math.sin((x + seed % 13) * 0.55) + 2.0 * Math.cos((z - seed % 7) * 0.45));
            heights[x][z] = Math.max(0, Math.min(4, 1 + broad / 2 + random.nextInt(2)));
        }
    }
    private FloatBuffer vertices, colors;
    private final float[] cube = {
      -0.5f,-0.5f,0.5f, 0.5f,-0.5f,0.5f, 0.5f,0.5f,0.5f, -0.5f,0.5f,0.5f,
      0.5f,-0.5f,-0.5f, -0.5f,-0.5f,-0.5f, -0.5f,0.5f,-0.5f, 0.5f,0.5f,-0.5f,
      -0.5f,-0.5f,-0.5f, -0.5f,-0.5f,0.5f, -0.5f,0.5f,0.5f, -0.5f,0.5f,-0.5f,
      0.5f,-0.5f,0.5f, 0.5f,-0.5f,-0.5f, 0.5f,0.5f,-0.5f, 0.5f,0.5f,0.5f,
      -0.5f,0.5f,0.5f, 0.5f,0.5f,0.5f, 0.5f,0.5f,-0.5f, -0.5f,0.5f,-0.5f,
      -0.5f,-0.5f,-0.5f, 0.5f,-0.5f,-0.5f, 0.5f,-0.5f,0.5f, -0.5f,-0.5f,0.5f
    };
    private final float[] faceColors = {0.28f,0.62f,0.22f,1, 0.28f,0.48f,0.18f,1, 0.35f,0.55f,0.22f,1, 0.35f,0.55f,0.22f,1, 0.38f,0.72f,0.25f,1, 0.32f,0.28f,0.18f,1};
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0.48f,0.72f,0.96f,1); gl.glEnable(GL10.GL_DEPTH_TEST); gl.glEnable(GL10.GL_CULL_FACE);
        ByteBuffer vb=ByteBuffer.allocateDirect(cube.length*4).order(ByteOrder.nativeOrder()); vertices=vb.asFloatBuffer(); vertices.put(cube).position(0);
        ByteBuffer cb=ByteBuffer.allocateDirect(faceColors.length*4).order(ByteOrder.nativeOrder()); colors=cb.asFloatBuffer(); colors.put(faceColors).position(0);
    }
    public void onSurfaceChanged(GL10 gl,int w,int h) { gl.glViewport(0,0,w,h); gl.glMatrixMode(GL10.GL_PROJECTION); gl.glLoadIdentity(); GLU.gluPerspective(gl,65f,(float)w/(float)Math.max(1,h),0.1f,100f); }
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT|GL10.GL_DEPTH_BUFFER_BIT); gl.glMatrixMode(GL10.GL_MODELVIEW); gl.glLoadIdentity();
        gl.glRotatef(look,1,0,0); gl.glRotatef(turn,0,1,0); gl.glTranslatef(0,-1.3f,-7f+forward);
        // Deterministic, seed-based starter terrain. Each column is generated
        // from the saved world seed so the same world looks the same on reload.
        for(int x=0;x<11;x++) for(int z=0;z<11;z++) {
            int height = heights[x][z];
            for(int y=0;y<height;y++) {
                gl.glPushMatrix(); gl.glTranslatef(x-5, y-0.5f, z-5); drawCube(gl); gl.glPopMatrix();
            }
        }
    }
    private void drawCube(GL10 gl) {
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY); gl.glVertexPointer(3,GL10.GL_FLOAT,0,vertices);
        for(int f=0;f<6;f++) { gl.glColor4f(faceColors[f*4],faceColors[f*4+1],faceColors[f*4+2],1); gl.glDrawArrays(GL10.GL_TRIANGLE_FAN,f*4,4); }
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }
}
