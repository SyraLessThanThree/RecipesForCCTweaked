TODO:
    Implement 3x3 return
    Implement simple 3x3 return
    Cache Item Recipes
    
Planned Features:
    get all ingredients as a simple list
    get all ingredients recursively from the crafting recipe of the ingredients
    assume we have all ingredients and craft the given item bottem up
    smelting recipes
    ?more ways to craft?

Found Bugs:
    ("ic2:te",3) returns a gridSize of {-1,-1}
    ("ic2:te",3) one recipe needs a furnace with nbtid 32767
        ?could be -1 or something meaning ignore nbtid?