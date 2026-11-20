package com.lebronsaddons.mixin;

import com.lebronsaddons.AddonsScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PauseScreen.class)
public abstract class GameMenuScreenMixin extends Screen {
    protected GameMenuScreenMixin(Component title){super(title);}
    @Inject(method="init",at=@At("TAIL"))
    private void lebronsaddons$addButton(CallbackInfo ci){
        addRenderableWidget(Button.builder(Component.literal("Lebron's Addons"),b->minecraft.gui.setScreen(new AddonsScreen((Screen)(Object)this)))
            .bounds(width/2-102,height/4+145,204,20).build());
    }
}
