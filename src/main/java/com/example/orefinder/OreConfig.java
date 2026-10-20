package com.example.orefinder;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class OreConfig {
    public static final int[] RADII = {16, 24, 32, 48};

    public static Set<Ore> enabled = EnumSet.of(Ore.DIAMOND);
    public static int radius = 32;

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("orefinder.txt");
    }

    public static void load() {
        Path f = file();
        if (!Files.exists(f)) return;
        try {
            Set<Ore> set = EnumSet.noneOf(Ore.class);
            for (String line : Files.readAllLines(f)) {
                line = line.trim();
                if (line.startsWith("radius=")) {
                    radius = Integer.parseInt(line.substring(7));
                } else if (!line.isEmpty()) {
                    try {
                        set.add(Ore.valueOf(line));
                    } catch (IllegalArgumentException ignored) {
                    }
                }
            }
            enabled = set;
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        List<String> lines = new ArrayList<>();
        lines.add("radius=" + radius);
        for (Ore o : enabled) lines.add(o.name());
        try {
            Files.write(file(), lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
