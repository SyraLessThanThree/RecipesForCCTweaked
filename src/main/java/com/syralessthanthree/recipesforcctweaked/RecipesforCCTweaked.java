package com.syralessthanthree.recipesforcctweaked;

import dan200.computercraft.api.ComputerCraftAPI;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.*;

import org.apache.logging.log4j.Logger;

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
        // logger.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }
}
