package com.example.voxelpe;

import android.app.Activity;
import android.os.Bundle;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.content.Context;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    public void onCreate(Bundle state) {
        super.onCreate(state);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        FrameLayout root = new FrameLayout(this);
        GameView game = new GameView(this);
        root.addView(game);
        Hud hud = new Hud(this, game);
        root.addView(hud);
        setContentView(root);
    }

    static class GameView extends GLSurfaceView {
        final WorldRenderer renderer;
        float downX, downY;
        GameView(Context c) {
            super(c);
            setEGLContextClientVersion(1);
            renderer = new WorldRenderer();
            setRenderer(renderer);
            setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        }
        public boolean onTouchEvent(MotionEvent e) {
            if (e.getAction() == MotionEvent.ACTION_DOWN) { downX=e.getX(); downY=e.getY(); return true; }
            if (e.getAction() == MotionEvent.ACTION_MOVE) {
                float dx=e.getX()-downX, dy=e.getY()-downY;
                renderer.turn += dx * 0.35f;
                renderer.look = Math.max(-35f, Math.min(35f, renderer.look + dy * 0.18f));
                downX=e.getX(); downY=e.getY(); return true;
            }
            return true;
        }
        void action(int a) { if (a==0) renderer.forward += 0.16f; if (a==1) renderer.forward -= 0.16f; if (a==2) renderer.blocksChanged++; if (a==3) renderer.jump=0.5f; }
    }

    static class Hud extends View {
        Paint p = new Paint(3); GameView game;
        Hud(Context c, GameView g) { super(c); game=g; setLayerType(View.LAYER_TYPE_SOFTWARE, null); }
        protected void onDraw(Canvas c) {
            super.onDraw(c); float w=getWidth(), h=getHeight();
            p.setColor(0x66000000); c.drawRoundRect(18,h-118,260,h-18,18,18,p);
            c.drawRoundRect(w-220,h-132,w-22,h-18,18,18,p);
            p.setColor(Color.WHITE); p.setTextSize(25); p.setTypeface(Typeface.DEFAULT_BOLD);
            c.drawText("◀",42,h-57,p); c.drawText("▶",112,h-57,p); c.drawText("▲",184,h-57,p);
            c.drawText("BREAK",w-205,h-80,p); c.drawText("PLACE",w-110,h-80,p);
            p.setTextSize(16); c.drawText("Voxel PE  •  prototype",22,28,p);
        }
        public boolean onTouchEvent(MotionEvent e) {
            if(e.getAction()==MotionEvent.ACTION_UP) {
                float x=e.getX(), y=e.getY(), w=getWidth(), h=getHeight();
                if(y>h-125 && x<260) { if(x<95) game.action(0); else if(x<165) game.action(1); else game.action(3); }
                else if(y>h-145 && x>w-225) { game.action(x>w-120?2:3); }
                return true;
            } return true;
        }
    }
}
