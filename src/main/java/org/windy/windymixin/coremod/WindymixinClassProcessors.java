package org.windy.windymixin.coremod;

import net.neoforged.neoforgespi.transformation.ClassProcessorProvider;

/**
 * 注册 Windymixin 的 ClassProcessor，通过 META-INF/services SPI 自动发现。
 */
public class WindymixinClassProcessors implements ClassProcessorProvider {

    @Override
    public void createProcessors(Context context, Collector collector) {
        collector.add(new ContainerScreenEventProcessor());
    }
}
