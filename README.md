### Use Github Projects for feature and bug tracking

First working release
you can only really use
int nbtid = 0
recipe.getRecipe("modid:itemid",nbtid) // nbtid is optional which will than just use 0

for examples try
```
getRecipe("minecraft:wool",0) // simple 2x2 recipe
getRecipe("minecraft:wool",3) // same block, different nbt, different recipe
getRecipe("minecraft:wooden_door") // non 3x3 recipe
getRecipe("minecraft:trapdoor") // technically same output as the door but different gridSize
getRecipe("minecraft:diamond_pickaxe") // recipe with empty places
getRecipe("ic2:te",3) // multiple different recipes
```

this would return this (written in a broken mix of lua and java):

```
// to return an array of
{
      // an array for the grid of
      {
          // one grid field
          // array of 'possible ingredients'
          { // one grid field start
              // an ingredient
              { String itemid , int nbtid } // an empty ingredient is { "", 0 }
              [, { String itemid , int nbtid } ] // possible alternate ingredients
              [, {...} ] // possible more ...
          } // one grid field end
          
          // second grid field
          // array of 'possible ingredients'
          ,{ // possible second grid field
              // ...
          } // possible second grid field
      }, // end of grid
      { int gridWidth, int gridHeight }, // gridSize
      { String returnItemID, int returnItemAmount, int returnItemNbtID} // return item
}
```