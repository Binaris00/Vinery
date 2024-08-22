package net.satisfy.vinery.registry;

import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.Llama;
import net.satisfy.vinery.Vinery;
import net.satisfy.vinery.entity.TraderMuleEntity;
import net.satisfy.vinery.entity.WanderingWinemakerEntity;
import net.satisfy.vinery.util.VineryIdentifier;

import java.util.function.Supplier;

public class EntityRegistry {
	private static final Registrar<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Vinery.MOD_ID, Registries.ENTITY_TYPE).getRegistrar();
	
	public static final RegistrySupplier<EntityType<TraderMuleEntity>> MULE = create("mule",
			() -> EntityType.Builder.of(TraderMuleEntity::new, MobCategory.CREATURE).sized(0.9f, 1.87f).clientTrackingRange(10).build(VineryIdentifier.of("mule").toString())
	);
	
	public static final RegistrySupplier<EntityType<WanderingWinemakerEntity>> WANDERING_WINEMAKER = create("wandering_winemaker",
			() -> EntityType.Builder.of(WanderingWinemakerEntity::new, MobCategory.CREATURE)
			                       .sized(0.6f, 1.95f)
			                       .clientTrackingRange(10)
			                       .build(VineryIdentifier.of("wandering_winemaker").toString()));

	public static <T extends EntityType<?>> RegistrySupplier<T> create(final String path, final Supplier<T> type) {
		return ENTITY_TYPES.register(VineryIdentifier.of(path), type);
	}
	
	public static void init() {
		EntityAttributeRegistry.register(MULE, () -> Llama.createAttributes().add(Attributes.MOVEMENT_SPEED, 0.2f));
		EntityAttributeRegistry.register(WANDERING_WINEMAKER, WanderingWinemakerEntity::createMobAttributes);
	}
}