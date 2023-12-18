package com.syralessthanthree.recipesforcctweaked;


import dan200.computercraft.api.lua.IComputerSystem;
import dan200.computercraft.api.lua.ILuaAPI;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RecipesAPI implements ILuaAPI {
    private final IComputerSystem compSys;
        /*private final IAPIEnvironment env;
        private final Terminal term;

        RecipesAPI(IAPIEnvironment env_) {
            env = env_;
            term = env.getTerminal();
        }*/

    public RecipesAPI(IComputerSystem iComputerSystem) {
        compSys = iComputerSystem;
    }

    @Override
    public String[] getNames() {
        return new String[]{"recipes"};
    }

    @Nonnull
    @Override
    public String[] getMethodNames() {
        return new String[]{
                "getRecipe",
                "getRecipe3by3",
                "getFirst3by3SimpleRecipe"
        };
    }

    @Nullable
    @Override
    public Object[] callMethod(@Nonnull ILuaContext iLuaContext, int method, @Nonnull Object[] luaArguments) throws LuaException, InterruptedException {
        switch (method){

            /// getRecipe
            case 0: { // get recipe from id
                return iLuaContext.executeMainThreadTask(() -> {
                    if(!(
                            luaArguments.length == 1 ||
                            luaArguments.length == 2
                    )){
                        return new Object[0];
                    }

                    // init arguments
                    String inItemID = (String) luaArguments[0];
                    int nbtID = 0;
                    if(luaArguments.length >= 2) nbtID = ((Double) luaArguments[1]).intValue();


                    List<Object> outLua = new ArrayList<>();
                    List<Recipe> possibleRecipes = getRecipesByID(inItemID,nbtID);
                    outLua.add(recipesToLuaObjects(possibleRecipes));
                    return outLua.toArray();
                });
            }

            /// getRecipe3by3
            case 1: { // get recipe from id and format it in a 3x3 crafting grid
                return iLuaContext.executeMainThreadTask(() -> {
                    if(!(
                            luaArguments.length == 1 ||
                            luaArguments.length == 2
                    )){
                        return new Object[0];
                    }

                    // init arguments
                    String inItemID = (String) luaArguments[0];
                    int nbtID = 0;
                    if(luaArguments.length >= 2) nbtID = ((Double) luaArguments[1]).intValue();


                    List<Recipe> originalRecipes = getRecipesByID(inItemID, nbtID);
                    List<Recipe> recipes3by3 = recipesTo3by3Recipe(originalRecipes);
                    return recipesToLuaObjects(recipes3by3);
                });
            }

            /// getFirst3by3SimpleRecipe
            case 2:{ // get the first available recipe of an item and format it in a 3x3 grid and return it as only having one possibility
                return iLuaContext.executeMainThreadTask(() -> {
                    if(!(
                            luaArguments.length == 1 ||
                            luaArguments.length == 2
                    )){
                        return new Object[0];
                    }

                    // init arguments
                    String inItemID = (String) luaArguments[0];
                    int nbtID = 0;
                    if(luaArguments.length >= 2) nbtID =  ((Double) luaArguments[1]).intValue();


                    List<Recipe> originalRecipes = getRecipesByID(inItemID, nbtID);
                    if(originalRecipes.isEmpty()){
                        return new Object[0];
                    }
                    Recipe recipe3by3 = recipeTo3by3Recipe(originalRecipes.get(0));
                    return recipeToLuaObjectsOnlyOneIngr(recipe3by3);
                });
            }
        }
        return new Object[0];
    }

    Object[] recipesToLuaObjects(List<Recipe> inRecipes){
        List<Object> out = new ArrayList<>();
        for (int recipeIndex = 0; recipeIndex < inRecipes.size(); recipeIndex++) {
            out.add(recipeToLuaObjects(inRecipes.get(recipeIndex)));
        }
        return out.toArray();
    }

    Object[] recipeToLuaObjects(Recipe inRecipe){
        List<Object> luaOut = new ArrayList<>();
        List<List<Recipe.APIIngredient>> ingredients = new ArrayList<>();
        List<Object[]> ingredientsLua = new ArrayList<>();
        Point recipeSize = new Point(-1,-1);
        String returnItemID = "";
        int returnItemAmount = -1;
        int returnItemNbtID = -1;

        // parse ingredients
        ingredients = inRecipe.getIngredients();

        for (int ingredientIndex = 0; ingredientIndex < ingredients.size(); ingredientIndex++) {
            List<Recipe.APIIngredient> currIngredients = ingredients.get(ingredientIndex);
            List<Object[]> currLuaIngredients = new ArrayList<>();
            currIngredients.forEach((Recipe.APIIngredient ingr) -> {
                currLuaIngredients.add(new Object[]{
                        ingr.getItemID(),
                        ingr.getNbtID()
                });
            });
            if(currIngredients.isEmpty()){ // no item needed, so we add an unnamed ingredient to signalize that
                currLuaIngredients.add(new Object[]{
                        "",
                        0
                });
            }
            ingredientsLua.add(currLuaIngredients.toArray());
        }

        // parse recipeSize, returnItemID and returnItemAmount
        recipeSize = inRecipe.getSize();
        returnItemID = inRecipe.getResultItemID();
        returnItemNbtID = inRecipe.getResultItemNbtID();
        returnItemAmount = inRecipe.getResultAmount();

        luaOut.add(ingredientsLua.toArray());
        luaOut.add(new Object[]{recipeSize.x,recipeSize.y});
        luaOut.add(new Object[]{returnItemID,returnItemAmount,returnItemNbtID});;
        return luaOut.toArray();
    }

    Object[] recipeToLuaObjectsOnlyOneIngr(Recipe inRecipe){ // TODO
        List<Object> out = new ArrayList<>();

        return out.toArray();
    }

    List<Recipe> getRecipesByID(String itemID, int nbtID){

        List<Recipe> outValidRecipes = new ArrayList<>();

        if(itemID.equals("ping")){ // debugging
            List<Recipe.APIIngredient> dummyIngredients = new ArrayList<>();
            dummyIngredients.add(Recipe.APIIngredient.createInvalidDebugIngredient("pong", 1,0));
            Recipe dummyRecipe = new Recipe(null,new Point(-2,-2),"pong",-2);
            dummyRecipe.setIngredientsFromSingleIngredientList(dummyIngredients);
            outValidRecipes.add(dummyRecipe);
            return outValidRecipes;
        }

        Item inItem = Item.getByNameOrId(itemID);

        if(itemID.isEmpty() || inItem == null){
            outValidRecipes.add(new Recipe(new ArrayList<>(),new Point(-1,-1),"",-1));
            return outValidRecipes;
        }


        ItemStack inItemStack = new ItemStack(inItem,1,nbtID);
        inItemStack.setCount(1);

        Iterator<IRecipe> iterator = CraftingManager.REGISTRY.iterator();

        while(iterator.hasNext()){
            Recipe r = new Recipe(iterator.next());
            ItemStack itemStackResult = r.getResultItemStack();
            ItemStack itemStackResultSingle = itemStackResult.copy();
            itemStackResultSingle.setCount(1);
            if(ItemStack.areItemStacksEqualUsingNBTShareTag(inItemStack,itemStackResultSingle)){
                outValidRecipes.add(r);
            }
        }

        return outValidRecipes;
        //NonNullList<Ingredient> ingredients = getRecipeWithOutput(inItemStack).getIngredients();

            /*
            try{
                Iterator<IRecipe> iterator = CraftingManager.REGISTRY.iterator();
                for (; iterator.hasNext(); ) {
                    Recipe r = new Recipe(iterator.next());
                    ItemStack itemStackResult = r.;
                    ItemStack itemStackResultSingle = itemStackResult.copy();
                    itemStackResultSingle.setCount(1);
                    if(!ItemStack.areItemStackTagsEqual(inItemStack,itemStackResultSingle)) continue;

                    validRecipes.add(r);
                    break;
                }

                if(validRecipes == null){
                    List<Triple<List<Object>,Point,Integer>> out = new ArrayList<>();
                    out.add(new MutableTriple<>(outList,new Point(-1,-1),-1));
                    return out;
                }

                for (int recipeIndex = 0; recipeIndex < validRecipes.size(); recipeIndex++) {NonNullList<Ingredient> ingredients = validRecipes.getIngredients();

                    outList.add(ingrName);
                    //System.out.println(ingredient.getMatchingStacks()[0].getItem().getRegistryName().toString());
                }

            }catch (NullPointerException e){
                System.err.println("NullPointerException from getRecipeByID from Mod recipesforcctweaked");
                e.printStackTrace(System.err);
            }
             */

            /*try {
                NonNullList<Ingredient> ingredients = getRecipeWithOutput(Item.getByNameOrId(in)).getIngredients();
                for(Ingredient ingredient : ingredients){
                    stringBuilder.append(ingredient.getMatchingStacks()[0].getItem().getRegistryName().toString()).append("\n");
                }
            }catch (NullPointerException e){

            }*/

        // debug
        // validRecipes.add(new Recipe());
        // validRecipes.add(new Recipe());
        // debug end

        //return validRecipes;
    }

    Recipe recipeTo3by3Recipe(Recipe originalRecipe){ // TODO
            /*
            List<Object> ingredientStringIDs = originalRecipe.left; // technically is List<String> but LUA needs an Object[] (which we later get via ingredientStringIDs.toArray()) as Output
            Point recipeSize = originalRecipe.right;

            final int WIDTH = 3;
            final int RECIPE_WIDTH = recipeSize.x;
            final int RECIPE_HEIGHT = recipeSize.y;
            final int HEIGHT = 3;

            boolean recipeInvalid = recipeSize.x < 0 || recipeSize.y < 0;
            boolean recipeTooBig = recipeSize.x > WIDTH  || recipeSize.y > HEIGHT;

            if(recipeInvalid || recipeTooBig)
                return new ImmutablePair<>(new ArrayList<>(), new Point(-1,-1));

            if(WIDTH == RECIPE_WIDTH && HEIGHT == RECIPE_HEIGHT) // already a 3x3 recipe, no need to recreate it
                new ImmutablePair<>(
                        ingredientStringIDs,
                        recipeSize
                );

            List<Object> ingredients3x3 = new ArrayList<>(WIDTH*HEIGHT);
            int currIngrIndex = 0;

            for (int x = 0; x < WIDTH; x++)
                for (int y = 0; y < HEIGHT; y++) {
                    if(x < RECIPE_WIDTH && y < RECIPE_HEIGHT){
                        ingredients3x3.set(y*WIDTH + x, (Object) ingredientStringIDs.get(currIngrIndex));
                        currIngrIndex++;
                    }else{
                        ingredients3x3.set(y*WIDTH + x, (Object) "");
                    }
                }


            return new ImmutablePair<>(ingredients3x3, new Point(WIDTH,HEIGHT));
            */
        return new Recipe();
    }

    List<Recipe> recipesTo3by3Recipe(List<Recipe> originalRecipes){
        List<Recipe> out = new ArrayList<>();

        for (int recipeIndex = 0; recipeIndex < originalRecipes.size(); recipeIndex++) {
            out.add(recipeTo3by3Recipe(originalRecipes.get(recipeIndex)));
        }
        return out;
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
}
