package artifality.mixin.common;

import artifality.extension.LightningExtension;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.InteractionObserver;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VillagerEntity.class)
public abstract class VillagerMixin extends MerchantEntity implements InteractionObserver, VillagerDataContainer {

    public VillagerMixin(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "onStruckByLightning", at = @At("HEAD"), cancellable = true)
    void onStruckByLightning(ServerWorld world, LightningEntity lightning, CallbackInfo ci){
        if(lightning instanceof LightningExtension extension){
            if(!extension.artifality$canSpawnFire()){
                super.onStruckByLightning(world, lightning);
                ci.cancel();
            }
        }
    }
}
