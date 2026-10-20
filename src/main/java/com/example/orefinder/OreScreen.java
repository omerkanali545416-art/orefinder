package com.example.orefinder;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.EnumSet;

public class OreScreen extends Screen {

    public OreScreen() {
        super(Text.literal("Maden Seç"));
    }

    private static Text oreLabel(Ore o) {
        boolean on = OreConfig.enabled.contains(o);
        return Text.literal((on ? "§a✔ " : "§c✘ ") + "§f" + o.label);
    }

    private static Text radiusLabel() {
        return Text.literal("Menzil: " + OreConfig.radius + " blok");
    }

    @Override
    protected void init() {
        Ore[] ores = Ore.values();
        int top = 40;

        for (int i = 0; i < ores.length; i++) {
            Ore o = ores[i];
            int col = i % 2;
            int row = i / 2;
            int x = width / 2 - 155 + col * 160;
            int y = top + row * 24;
            addDrawableChild(ButtonWidget.builder(oreLabel(o), btn -> {
                if (!OreConfig.enabled.remove(o)) OreConfig.enabled.add(o);
                btn.setMessage(oreLabel(o));
            }).dimensions(x, y, 150, 20).build());
        }

        int bottom = top + ((ores.length + 1) / 2) * 24 + 8;

        addDrawableChild(ButtonWidget.builder(Text.literal("Hepsini Seç"), btn -> {
            OreConfig.enabled = EnumSet.allOf(Ore.class);
            clearAndInit();
        }).dimensions(width / 2 - 155, bottom, 150, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Hiçbirini Seçme"), btn -> {
            OreConfig.enabled = EnumSet.noneOf(Ore.class);
            clearAndInit();
        }).dimensions(width / 2 + 5, bottom, 150, 20).build());

        addDrawableChild(ButtonWidget.builder(radiusLabel(), btn -> {
            int idx = 0;
            for (int i = 0; i < OreConfig.RADII.length; i++) {
                if (OreConfig.RADII[i] == OreConfig.radius) idx = i;
            }
            OreConfig.radius = OreConfig.RADII[(idx + 1) % OreConfig.RADII.length];
            btn.setMessage(radiusLabel());
        }).dimensions(width / 2 - 155, bottom + 24, 150, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Bitti"), btn -> close())
                .dimensions(width / 2 + 5, bottom + 24, 150, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 16, 0xFFFFFF);
    }

    @Override
    public void close() {
        OreConfig.save();
        super.close();
    }
  }
