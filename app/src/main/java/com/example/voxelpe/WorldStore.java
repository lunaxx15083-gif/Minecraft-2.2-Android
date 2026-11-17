package com.example.voxelpe;

import android.content.Context;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Small API-8-compatible world metadata store.
 * Each world is saved as a UTF-8 text file in the application's private files directory.
 */
public final class WorldStore {
    private final File directory;

    public WorldStore(Context context) {
        directory = new File(context.getFilesDir(), "worlds");
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public static final class World {
        public final String id;
        public final String name;
        public final long seed;
        public final boolean creative;

        World(String id, String name, long seed, boolean creative) {
            this.id = id;
            this.name = name;
            this.seed = seed;
            this.creative = creative;
        }
    }

    public synchronized World create(String name, long seed, boolean creative) throws IOException {
        String id = "world_" + System.currentTimeMillis() + "_" + Math.abs((int) (seed ^ (seed >>> 32)));
        World world = new World(id, name, seed, creative);
        save(world);
        return world;
    }

    public synchronized ArrayList<World> list() {
        ArrayList<World> result = new ArrayList<World>();
        File[] files = directory.listFiles();
        if (files == null) return result;

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isFile() && file.getName().endsWith(".world")) {
                try {
                    World world = read(file);
                    if (world != null) result.add(world);
                } catch (Exception ignored) {
                    // Ignore damaged metadata files and keep other worlds available.
                }
            }
        }
        return result;
    }

    private void save(World world) throws IOException {
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Cannot create worlds directory");
        }
        File file = new File(directory, world.id + ".world");
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file), "UTF-8"));
        try {
            out.write(encode(world.name));
            out.newLine();
            out.write(Long.toString(world.seed));
            out.newLine();
            out.write(world.creative ? "1" : "0");
            out.newLine();
        } finally {
            out.close();
        }
    }

    private World read(File file) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), "UTF-8"));
        try {
            String encodedName = in.readLine();
            String seedText = in.readLine();
            String modeText = in.readLine();
            if (encodedName == null || seedText == null || modeText == null) return null;
            String name = decode(encodedName);
            long seed = Long.parseLong(seedText);
            boolean creative = "1".equals(modeText);
            String filename = file.getName();
            String id = filename.substring(0, filename.length() - ".world".length());
            return new World(id, name, seed, creative);
        } finally {
            in.close();
        }
    }

    // Hex encoding avoids Android Base64 API/version dependencies.
    private String encode(String value) {
        try {
            byte[] bytes = value.getBytes("UTF-8");
            StringBuilder out = new StringBuilder(bytes.length * 2);
            for (int i = 0; i < bytes.length; i++) {
                int b = bytes[i] & 255;
                if (b < 16) out.append('0');
                out.append(Integer.toHexString(b));
            }
            return out.toString();
        } catch (Exception e) {
            return "";
        }
    }

    private String decode(String value) throws IOException {
        if ((value.length() & 1) != 0) throw new IOException("Invalid world name");
        byte[] bytes = new byte[value.length() / 2];
        try {
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = (byte) Integer.parseInt(value.substring(i * 2, i * 2 + 2), 16);
            }
            return new String(bytes, "UTF-8");
        } catch (Exception e) {
            throw new IOException("Invalid world name");
        }
    }
}
