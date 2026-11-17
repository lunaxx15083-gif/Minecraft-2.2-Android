package com.example.voxelpe;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Button;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends Activity {
    private WorldStore worlds;

    public void onCreate(Bundle state) {
        super.onCreate(state);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        worlds = new WorldStore(this);
        showWorlds();
    }

    private TextView title(String text) {
        TextView v = new TextView(this);
        v.setText(text); v.setTextColor(Color.WHITE); v.setTextSize(25);
        v.setTypeface(Typeface.DEFAULT_BOLD); v.setGravity(Gravity.CENTER);
        v.setPadding(8, 16, 8, 16);
        return v;
    }

    private Button button(String text) {
        Button b = new Button(this); b.setText(text); return b;
    }

    private void showWorlds() {
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL); page.setPadding(14, 10, 14, 10);
        page.setBackgroundColor(Color.rgb(45, 65, 43));
        page.addView(title("Voxel PE — Worlds"));
        Button create = button("Create new world");
        page.addView(create, new LinearLayout.LayoutParams(-1, -2));
        create.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { showCreateWorld(); } });
        ScrollView scroll = new ScrollView(this);
        LinearLayout list = new LinearLayout(this); list.setOrientation(LinearLayout.VERTICAL);
        ArrayList<WorldStore.World> all = worlds.list();
        if (all.size() == 0) {
            TextView empty = new TextView(this); empty.setText("No worlds yet. Create your first world!");
            empty.setTextColor(Color.WHITE); empty.setTextSize(17); empty.setPadding(12, 24, 12, 24); list.addView(empty);
        }
        for (int i = 0; i < all.size(); i++) {
            final WorldStore.World w = all.get(i);
            Button item = button(w.name + "\n" + (w.creative ? "Creative" : "Survival") + "  •  Seed: " + w.seed);
            list.addView(item, new LinearLayout.LayoutParams(-1, -2));
            item.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { startGame(w); } });
        }
        scroll.addView(list); page.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        setContentView(page);
    }

    private void showCreateWorld() {
        LinearLayout form = new LinearLayout(this); form.setPadding(18, 8, 18, 8);
        form.setOrientation(LinearLayout.VERTICAL);
        final EditText name = new EditText(this); name.setSingleLine(true); name.setHint("World name"); name.setText("My World"); form.addView(name);
        final EditText seed = new EditText(this); seed.setSingleLine(true); seed.setHint("Seed (optional number)"); seed.setInputType(2); form.addView(seed);
        final RadioGroup mode = new RadioGroup(this); mode.setOrientation(RadioGroup.VERTICAL);
        final RadioButton survival = new RadioButton(this); survival.setText("Survival"); survival.setId(1); survival.setChecked(true);
        final RadioButton creative = new RadioButton(this); creative.setText("Creative"); creative.setId(2);
        mode.addView(survival); mode.addView(creative); form.addView(mode);
        new AlertDialog.Builder(this).setTitle("Create new world").setView(form)
            .setPositiveButton("Create", new android.content.DialogInterface.OnClickListener() {
                public void onClick(android.content.DialogInterface d, int which) {
                    String n = name.getText().toString().trim(); if (n.length() == 0) n = "My World";
                    long s;
                    try { s = seed.getText().toString().trim().length() == 0 ? System.currentTimeMillis() : Long.parseLong(seed.getText().toString().trim()); }
                    catch (Exception e) { s = System.currentTimeMillis(); }
                    try { startGame(worlds.create(n, s, mode.getCheckedRadioButtonId() == 2)); }
                    catch (IOException e) { new AlertDialog.Builder(MainActivity.this).setMessage("Could not save world: " + e.getMessage()).setPositiveButton("OK", null).show(); }
                }
            }).setNegativeButton("Cancel", null).show();
    }

    private void startGame(WorldStore.World world) {
        FrameLayout root = new FrameLayout(this);
        GameView game = new GameView(this, world);
        root.addView(game);
        Hud hud = new Hud(this, game, world);
        root.addView(hud);
        setContentView(root);
    }

    public void onBackPressed() { showWorlds(); }

    static class GameView extends GLSurfaceView {
        final WorldRenderer renderer;
        float downX, downY;
        GameView(Context c, WorldStore.World world) {
            super(c);
            setEGLContextClientVersion(1);
            renderer = new WorldRenderer(world.seed);
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
        Paint p = new Paint(3); GameView game; WorldStore.World world;
        Hud(Context c, GameView g, WorldStore.World w) { super(c); game=g; world=w; }
        protected void onDraw(Canvas c) {
            super.onDraw(c); float w=getWidth(), h=getHeight();
            p.setColor(0x66000000); c.drawRoundRect(18,h-118,260,h-18,18,18,p);
            c.drawRoundRect(w-220,h-132,w-22,h-18,18,18,p);
            p.setColor(Color.WHITE); p.setTextSize(25); p.setTypeface(Typeface.DEFAULT_BOLD);
            c.drawText("◀",42,h-57,p); c.drawText("▶",112,h-57,p); c.drawText("▲",184,h-57,p);
            c.drawText("BREAK",w-205,h-80,p); c.drawText("PLACE",w-110,h-80,p);
            p.setTextSize(16); c.drawText(world.name + "  •  Voxel PE",22,28,p);
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
