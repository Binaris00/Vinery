package net.satisfy.vinery.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.satisfy.vinery.Vinery;
import net.satisfy.vinery.client.gui.handler.ApplePressGuiHandler;
import net.satisfy.vinery.client.gui.handler.BasketGuiHandler;
import net.satisfy.vinery.client.gui.handler.FermentationBarrelGuiHandler;
import net.satisfy.vinery.util.VineryIdentifier;

import java.util.function.Supplier;

public class ScreenhandlerTypeRegistry {

    private static final Registrar<MenuType<?>> MENU_TYPES = DeferredRegister.create(Vinery.MOD_ID, Registries.MENU).getRegistrar();

    public static final RegistrySupplier<MenuType<FermentationBarrelGuiHandler>> FERMENTATION_BARREL_GUI_HANDLER = register("fermentation_barrel_gui_handler", () -> new MenuType<>(FermentationBarrelGuiHandler::new, FeatureFlags.VANILLA_SET));
    public static final RegistrySupplier<MenuType<ApplePressGuiHandler>> APPLE_PRESS_GUI_HANDLER = register("apple_press_gui_handler", () -> new MenuType<>(ApplePressGuiHandler::new, FeatureFlags.VANILLA_SET));
    public static final RegistrySupplier<MenuType<BasketGuiHandler>> BASKET_GUI_HANDLER = register("basket_gui_handler", () -> new MenuType<>(BasketGuiHandler::new, FeatureFlags.VANILLA_SET));


    public static <T extends AbstractContainerMenu> RegistrySupplier<MenuType<T>> register(String name, Supplier<MenuType<T>> menuType){
        return MENU_TYPES.register(VineryIdentifier.of(name), menuType);
    }

    public static void init() {
    }
}
