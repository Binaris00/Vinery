package net.satisfy.vinery.client.gui.config;

import de.cristelknight.doapi.DoApiRL;
import de.cristelknight.doapi.config.cloth.CCUtil;
import de.cristelknight.doapi.config.cloth.LinkEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.gui.entries.IntegerListEntry;
import me.shedaniel.clothconfig2.gui.entries.TextListEntry;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.satisfy.vinery.Vinery;
import net.satisfy.vinery.config.VineryConfig;

public class ClothConfigScreen {

    private static Screen lastScreen;
    private static final WidgetSprites DISCORD = new WidgetSprites(
            DoApiRL.asResourceLocation("textures/gui/dc.png"),
            DoApiRL.asResourceLocation("textures/gui/dc.png")
    );

    private static final WidgetSprites CURSEFORGE = new WidgetSprites(
            DoApiRL.asResourceLocation("textures/gui/cf.png"),
            DoApiRL.asResourceLocation("textures/gui/cf.png")
    );

    public static Screen create(Screen parent) {
        lastScreen = parent;
        VineryConfig config = VineryConfig.DEFAULT.getConfig().validate();
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setDefaultBackgroundTexture(ResourceLocation.withDefaultNamespace("textures/block/dirt.png"))
                .setTitle(Component.translatable(Vinery.MOD_ID + ".config.title").withStyle(ChatFormatting.BOLD));

        ConfigEntries entries = new ConfigEntries(builder.entryBuilder(), config, builder.getOrCreateCategory(CCUtil.categoryName("main", Vinery.MOD_ID)));
        builder.setSavingRunnable(() -> {
            VineryConfig.DEFAULT.setInstance(entries.createConfig().validate());
            VineryConfig.DEFAULT.getConfig(true, true);
        });
        return builder.build();
    }

    private static class ConfigEntries {
        private final ConfigEntryBuilder builder;
        private final ConfigCategory category;
        private final BooleanListEntry enableWineMakerSetBonus, destroyBlocks;
        private final IntegerListEntry wineTraderChance, yearLengthInDays, yearsPerEffectLevel, fermentationBarrelTime, damagePerUse, probabilityForDamage, probabilityToKeepBoneMeal, grapeGrowthSpeed;
        private final IntegerListEntry wineEffectDuration, wineEffectStrength;

        public ConfigEntries(ConfigEntryBuilder builder, VineryConfig config, ConfigCategory category) {
            this.builder = builder;
            this.category = category;

            wineTraderChance = createIntField("wineTraderChance", config.wineTraderChance(), VineryConfig.DEFAULT.wineTraderChance(), null, 0, 100);
            yearLengthInDays = createIntField("yearLengthInDays", config.yearLengthInDays(), VineryConfig.DEFAULT.yearLengthInDays(), null, 1, 1000);
            yearsPerEffectLevel = createIntField("yearsPerEffectLevel", config.yearsPerEffectLevel(), VineryConfig.DEFAULT.yearsPerEffectLevel(), null, 1, 1000);
            fermentationBarrelTime = createIntField("fermentationBarrelTime", config.fermentationBarrelTime(), VineryConfig.DEFAULT.fermentationBarrelTime(), null, 1, 10000);
            grapeGrowthSpeed = createIntField("grapeGrowthSpeed", config.grapeGrowthSpeed(), VineryConfig.DEFAULT.grapeGrowthSpeed(), null, 1, 100);

            wineEffectDuration = createIntField("wineEffectDuration", config.wineEffectDuration(), VineryConfig.DEFAULT.wineEffectDuration(), null, 1, 100000);
            wineEffectStrength = createIntField("wineEffectStrength", config.wineEffectStrength(), VineryConfig.DEFAULT.wineEffectStrength(), null, 0, 4);

            SubCategoryBuilder wineMaker = new SubCategoryBuilder(Component.empty(), Component.translatable("config.vinery.subCategory.wineMaker"));

            enableWineMakerSetBonus = createBooleanField("enableWineMakerSetBonus", config.enableWineMakerSetBonus(), VineryConfig.DEFAULT.enableWineMakerSetBonus(), wineMaker);
            probabilityToKeepBoneMeal = createIntField("probabilityToKeepBoneMeal", config.probabilityToKeepBoneMeal(), VineryConfig.DEFAULT.probabilityToKeepBoneMeal(), wineMaker, 1, 100);
            probabilityForDamage = createIntField("probabilityForDamage", config.probabilityForDamage(), VineryConfig.DEFAULT.probabilityForDamage(), wineMaker, 0, 100);
            damagePerUse = createIntField("damagePerUse", config.damagePerUse(), VineryConfig.DEFAULT.damagePerUse(), wineMaker, 1, 1000);
            destroyBlocks = createBooleanField("destroyBlocks", config.destroyBlocks(), VineryConfig.DEFAULT.destroyBlocks(), null);

            category.addEntry(wineMaker.build());
            linkButtons(Vinery.MOD_ID, category, builder, "https://discord.gg/Vqu6wYZwdZ", "https://www.curseforge.com/minecraft/mc-mods/lets-do-wine", lastScreen);
        }

        public VineryConfig createConfig() {
            return new VineryConfig(wineTraderChance.getValue(), yearLengthInDays.getValue(), yearsPerEffectLevel.getValue(), enableWineMakerSetBonus.getValue(), damagePerUse.getValue(), probabilityForDamage.getValue(), probabilityToKeepBoneMeal.getValue(), fermentationBarrelTime.getValue(), grapeGrowthSpeed.getValue(), wineEffectDuration.getValue(), wineEffectStrength.getValue(), destroyBlocks.getValue());
        }

        public BooleanListEntry createBooleanField(String id, boolean value, boolean defaultValue, SubCategoryBuilder subCategoryBuilder){
            BooleanListEntry e = CCUtil.createBooleanField(Vinery.MOD_ID, id, value, defaultValue, builder);

            if(subCategoryBuilder == null) category.addEntry(e);
            else subCategoryBuilder.add(e);

            return e;
        }

        public IntegerListEntry createIntField(String id, int value, int defaultValue, SubCategoryBuilder subCategoryBuilder, int min, int max){
            IntegerListEntry e = CCUtil.createIntField(Vinery.MOD_ID, id, value, defaultValue, builder).setMaximum(max).setMinimum(min);

            if(subCategoryBuilder == null) category.addEntry(e);
            else subCategoryBuilder.add(e);

            return e;
        }
    }

    public static void linkButtons(String MOD_ID, ConfigCategory category, ConfigEntryBuilder builder, String dcLink, String cfLink, Screen lastScreen){
        if(lastScreen == null) lastScreen = Minecraft.getInstance().screen;

        TextListEntry tle = builder.startTextDescription(Component.literal(" ")).build();
        category.addEntry(tle);
        Screen finalLastScreen = lastScreen;
        category.addEntry(new LinkEntry(CCUtil.entryName(MOD_ID,"dc"), buttonWidget -> Minecraft.getInstance().setScreen(new ConfirmLinkScreen(confirmed -> {
            if (confirmed) {
                Util.getPlatform().openUri(dcLink);
            }
            Minecraft.getInstance().setScreen(create(finalLastScreen)); }, dcLink, true)), DISCORD, 3));
        category.addEntry(tle);
        category.addEntry(new LinkEntry(CCUtil.entryName(MOD_ID,"h"), buttonWidget -> Minecraft.getInstance().setScreen(new ConfirmLinkScreen(confirmed -> {
            if (confirmed) {
                Util.getPlatform().openUri(cfLink);
            }
            Minecraft.getInstance().setScreen(create(finalLastScreen)); }, cfLink, true)), CURSEFORGE, 10));
    }
}
