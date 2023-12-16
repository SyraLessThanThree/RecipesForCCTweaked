package com.example.examplemod;

import dan200.computercraft.api.ComputerCraftAPI;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

import dan200.computercraft.api.lua.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mod(modid = RecipesforCCTweaked.MODID, name = RecipesforCCTweaked.NAME, version = RecipesforCCTweaked.VERSION)
public class RecipesforCCTweaked
{
    public static final String MODID = "recipesforcctweaked";
    public static final String NAME = "Recipes for CC:Tweaked";
    public static final String VERSION = "0.1";

    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        //ForgeChunkManager.setForcedChunkLoadingCallback(this, this);
        ComputerCraftAPI.registerAPIFactory(RecipesAPI::new);
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        // some example code
        logger.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }

    public class RecipesAPI implements ILuaAPI{
        private final IComputerSystem computer;
        RecipesAPI(IComputerSystem computer) {
            this.computer = computer;
        }

        @Override
        public String[] getNames() {
            return new String[0];
        }

        @Override
        public void startup() {
            ILuaAPI.super.startup();
        }

        @Override
        public void update() {
            ILuaAPI.super.update();
        }

        @Override
        public void shutdown() {
            ILuaAPI.super.shutdown();
        }

        @Nonnull
        @Override
        public String[] getMethodNames() {
            return new String[0];
        }

        @Nullable
        @Override
        public Object[] callMethod(@Nonnull ILuaContext iLuaContext, int i, @Nonnull Object[] objects) throws LuaException, InterruptedException {
            return new Object[0];
        }
    }
}
