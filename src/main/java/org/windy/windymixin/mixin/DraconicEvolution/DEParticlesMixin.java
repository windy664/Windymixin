package org.windy.windymixin.mixin.DraconicEvolution;

import com.brandon3055.draconicevolution.client.DEParticles;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DEParticles.class)
public class DEParticlesMixin {
    @Inject(method = "init", at = @At("HEAD"), remap = false)
    private static void onInit(IEventBus modBus, CallbackInfo ci) {
        // 注册粒子类型 - 服务端必须做
        DEParticles.PARTICLE_TYPES.register(modBus);

        // 客户端才注册粒子渲染工厂
        if (FMLEnvironment.dist.isClient()) {
            modBus.addListener(DEParticles::registerFactories);
        }

        // 阻止原方法（因为我们手动调用了全部逻辑）
        ci.cancel();
    }
}
