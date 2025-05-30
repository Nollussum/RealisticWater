package Nollussum.realisticWater;

import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.world.WorldInitEvent;

public class WaterBlockFlowingEvent implements Listener{
    public boolean IsFluidWaterEnabled(){
        return true;
    }

    @EventHandler
    public void WorldInitializes(WorldInitEvent e){//disables water duplication bug.
        e.getWorld().setGameRule(GameRule.WATER_SOURCE_CONVERSION, false);
    }

    @EventHandler
    public void WaterBlockFlows(BlockFromToEvent event) {

        //Only water flow will have unique behavior(lava flow and dragon egg teleports are the other two possibilities)
        Block fromBlock = event.getBlock();
        Block toBlock = event.getToBlock();
        if (toBlock.getType() != Material.WATER && toBlock.getType() != Material.AIR && fromBlock.getType() != Material.WATER) {return;}
        event.setCancelled(true);

        //fromLevel & frLvl_Val
        Levelled fromLevel = (Levelled) fromBlock.getBlockData();
        int frLvl_Val = fromLevel.getLevel();

        //toLevel & toLvl_Val
        Levelled toLevel;
        int toLvl_Val;
        if (toBlock.getType() == Material.WATER){
            toLevel = (Levelled) toBlock.getBlockData();
            toLvl_Val = ((Levelled) toBlock.getBlockData()).getLevel();
        }else{
            toBlock.setType(Material.WATER);
            toLevel = (Levelled) toBlock.getBlockData();
            toLvl_Val = 8;
        }

        /*Levelled toLevel;
        int toLvl_Val = 0;
        try{//If toBlock is water, store waterLevel
            toLevel = (Levelled) toBlock.getBlockData();
            toLvl_Val = toLevel.getLevel();
        }
        catch(NullPointerException e) {//If toBlock is not water, set it to water, and store it's waterLevel as 0.
            toBlock.setType(Material.WATER);
            toLevel = (Levelled) toBlock.getBlockData();
        }*/

        //Makes sure target water is less than source water
        if (toLvl_Val < frLvl_Val){return;}//If toLvl has more water(aka, smaller level)

        //Calculations
        if (((toLvl_Val-frLvl_Val)/2)>= (IsFluidWaterEnabled() ? 0 : 1)){
            int NetChange = ((toLvl_Val-frLvl_Val)/2);
            //NetChange = Mean of both values + (1 only if enabled, 0 if disabled)

            //Checks to make sure the amount of water being moved is possible, and makes it possible if it is not.
            if (toLvl_Val-NetChange < 0){NetChange = toLvl_Val;}
            if (frLvl_Val+NetChange > 7){NetChange = frLvl_Val-NetChange;}

            //sets fromBlock's water level
            fromLevel.setLevel(frLvl_Val+NetChange);
            fromBlock.setBlockData(fromLevel);

            //sets toBlock to calculated water level.
            toLevel.setLevel(toLvl_Val-NetChange);
            toBlock.setBlockData(toLevel);
            }
        }
    }