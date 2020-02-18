package com.smoly87.fem;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.smoly87.rendering.Java2DRenderer;
import com.smoly87.rendering.SceneRender;
import com.smoly87.rendering.ScreenConfig;

public class MainModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(SceneRender.class).to(Java2DRenderer.class);
    }

    @Provides
    ScreenConfig provideScreenConfig() {
        return ScreenConfig.builder()
                .setScreenWidth(1000)
                .setScreenHeight(1000)
                .setxMin(-5d)
                .setxMax(5d)
                .setyMin(-5d)
                .setyMax(5d)
                .build();
    }

}
