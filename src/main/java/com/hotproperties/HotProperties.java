package com.hotproperties;

import com.hotproperties.command.HotPropertiesCommand;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HotProperties implements DedicatedServerModInitializer {

    public static final String MOD_ID = "hotproperties";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeServer() {
        LOGGER.info("Initializing HotProperties...");
        
        CommandRegistrationCallback.EVENT.register((dispatcher, buildContext, environment) -> {
            HotPropertiesCommand.register(dispatcher);
        });
    }
}
