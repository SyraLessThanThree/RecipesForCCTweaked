package com.syralessthanthree.recipesforcctweaked;

import net.minecraft.block.BlockCommandBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import scala.Int;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Recipe{

    public static class APIIngredient {
        private ItemStack itemStack;
        APIIngredient(){
            itemStack = ItemStack.EMPTY;
        }
        APIIngredient(ItemStack inItemStack){
            itemStack = inItemStack;
        }
        APIIngredient(Item item){
            itemStack = new ItemStack(item);
        }
        APIIngredient(Item item,int amount,int nbtID){
            itemStack = new ItemStack(item,amount,nbtID);
        }
        APIIngredient(String itemID){
            Item item = Item.getByNameOrId(itemID);
            itemStack = new ItemStack(item,1,0);
        }
        APIIngredient(String itemID,int amount,int nbtID){
            Item item = Item.getByNameOrId(itemID);
            itemStack = new ItemStack(item,amount,nbtID);
        }
        APIIngredient(Ingredient ingredient){
            itemStack = ingredient.getMatchingStacks()[0];
        }
        APIIngredient(Ingredient ingredient,int index){
            itemStack = ingredient.getMatchingStacks()[index];
        }

        protected static APIIngredient createInvalidDebugIngredient(String itemID,int amount,int nbtID){
            ItemStack debugItemStack = new ItemStack(Item.getByNameOrId("minecraft:command_block"),amount,nbtID);
            //debugItemStack.getItem().setRegistryName(itemID);
            debugItemStack.setCount(amount);
            return new APIIngredient(debugItemStack);
        }

        ItemStack getItemStack(){
            return itemStack.copy();
        }

        String getItemID(){
            ResourceLocation resourceLocation = itemStack.getItem().getRegistryName();
            if(itemStack.isEmpty() || resourceLocation == null) return "";
            return resourceLocation.toString();
        }

        int getNbtID(){
            return itemStack.getMetadata();
        }

        Pair<String, Integer> getIdNbtPair(){
            return new ImmutablePair<>(getItemID(),getNbtID());
        }
    }

    /// first array = fields, second array = possible ingredients
    /** this is kinda confusing\n
     * \n
     * the objects in the first array all represent a field\n
     * so in a crafting recipe that is always 3x3 (aka in a shaped recipe in a crafting bench for example)
     * the first list is exactly 9 elements big\n
     * \n
     * but an ingredient can have multiple possible items\n
     * for example you could craft a torch with both coal or charcoal
     */
    private List<List<APIIngredient>> matchingIngredients;

    /// setting the ingredient list from a list where only one ingredient is possible

    /**
     * this should only ever be used when you initialize the recipe with a null as ingredients and have a\n
     * List of ingredients where only one ingredient is possible
     */
    void setIngredientsFromSingleIngredientList(List<APIIngredient> singleIngredientList) throws IllegalStateException{
        if(matchingIngredients != null)
            throw new IllegalStateException("ingredients already set");
        matchingIngredients = new ArrayList<>();
        for (int i = 0; i < singleIngredientList.size(); i++) {
            List<APIIngredient> possibleSingleIngredients = new ArrayList<>(1);
            possibleSingleIngredients.add(singleIngredientList.get(i));
            matchingIngredients.add(possibleSingleIngredients);
        }
    }
    List<List<APIIngredient>> getIngredients(){
        return new ArrayList<>(matchingIngredients);
    }

    private Point size;
    Point getSize(){
        return (Point) size.clone();
    }

    private ItemStack resultItemStack;
    String getResultItemID(){
        ResourceLocation resourceLocation = resultItemStack.getItem().getRegistryName();
        if(resourceLocation == null || resultItemStack.isEmpty()){
            return "";
        }else{
            return resourceLocation.toString();
        }
    }
    int getResultItemNbtID(){
        int nbtID = resultItemStack.getMetadata();
        if(resultItemStack.isEmpty()){
            return -1;
        }else{
            return nbtID;
        }
    }
    ItemStack getResultItemStack(){
        return resultItemStack.copy();
    }

    Item getResultItem(){
        return resultItemStack.copy().getItem();
    }
    int getResultAmount(){
        return resultItemStack.getCount();
    }

    Recipe(){
        matchingIngredients = new ArrayList<>(0);
        size = new Point(-1,-1);
        resultItemStack = ItemStack.EMPTY;
        resultItemStack.setCount(-1);
    }

    Recipe(List<List<APIIngredient>> ingredients_, Point size_, Item resultItem_, int resultAmount_){
        matchingIngredients = ingredients_;
        size = size_;
        resultItemStack = new ItemStack(resultItem_,resultAmount_);
    }

    Recipe(List<List<APIIngredient>> ingredients_, Point size_, String resultItemID_, int resultAmount_){
        matchingIngredients = ingredients_;
        size = size_;
        Item resultItem_ = Item.getByNameOrId(resultItemID_);
        if(resultItem_ == null){
            resultItemStack = ItemStack.EMPTY;
            resultItemStack.setCount(-1);
            return;
        }
        resultItemStack = new ItemStack(resultItem_,resultAmount_);
    }

    Recipe(List<List<APIIngredient>> ingredients_, Point size_, ItemStack resultItemStack_){
        matchingIngredients = ingredients_;
        size = size_;
        resultItemStack = resultItemStack_;
    }

    Recipe(IRecipe inRecipe){
        initIngredientsFromIRecipe(inRecipe);
        size = getRecipeSize(inRecipe);
        resultItemStack = inRecipe.getRecipeOutput();
    }

    /// cursed function to get the minimum 2-dimensional size of a recipe
    /**
     * This looks like an innocent function to get the size of a Recipe right?\n
     * well you're wrong, this function is so cursed I can't\n
     * the only way to get the hint of a size of a recipe from forge (I have found)
     * is to use 'recipe.canFit(width, height)' which returns a boolean,\n
     * so we just test every possibility of the size based on the length of the ingredients
     * @return using Point as an explicit 2 size int[]
     */
    private Point getRecipeSize(IRecipe recipe){
        int recipeLength = recipe.getIngredients().size();
        List<Point> possibleSizes = new ArrayList<>();
        List<Point> checkedSizes = new ArrayList<>();
        for(int i = 1; i < recipeLength; i++) {
            for(int j = recipeLength; j >= 1; j--) {

                if(i >= 3 || j >= 3) continue; // TODO make this modular somehow? but for now assume a simple crafting bench

                Point size = new Point(i,j);
                if(checkedSizes.contains(size)) continue;
                if(i*j == recipeLength && recipe.canFit(i,j)) possibleSizes.add(size);
                checkedSizes.add(size);
            }
        }

        if(!possibleSizes.isEmpty()){
            if(possibleSizes.size()>1){
                System.out.println(String.format("Recipe that makes %s has multiple Sizes:",recipe.getRecipeOutput().getItem().getRegistryName().toString()));
                System.out.println(possibleSizes);
            }
            return possibleSizes.get(0);
        }else{
            return new Point(-1,-1);
        }
    }
    private void initIngredientsFromIRecipe(IRecipe inRecipe) {
        NonNullList<Ingredient> ingredientList = inRecipe.getIngredients();
        matchingIngredients = new ArrayList<>();
        for (int ingredientIndex = 0; ingredientIndex < ingredientList.size(); ingredientIndex++) {
            Ingredient ingredient = ingredientList.get(ingredientIndex);
            List<APIIngredient> currPossibleIngridients = new ArrayList<>();
            ItemStack[] matchingStacks = ingredient.getMatchingStacks();
            for (int itemStackIndex = 0; itemStackIndex < matchingStacks.length; itemStackIndex++) {
                ItemStack matchingItemStack = matchingStacks[itemStackIndex];
                if (matchingItemStack == null || matchingItemStack.isEmpty() ||
                        matchingItemStack.getCount() <= 0) {
                    currPossibleIngridients.add(new APIIngredient());
                    continue;
                }
                Item matchingItem = matchingItemStack.getItem();
                if (matchingItem == null) {
                    currPossibleIngridients.add(new APIIngredient());
                    continue;
                }
                ResourceLocation matchingItemRL = matchingItem.getRegistryName();
                if (matchingItemRL == null || matchingItemRL.toString().isEmpty()) {
                    currPossibleIngridients.add(new APIIngredient());
                    continue;
                }
                // valid item
                currPossibleIngridients.add(new APIIngredient(matchingItemStack));
            }
            matchingIngredients.add(currPossibleIngridients);
        }
    }
}
