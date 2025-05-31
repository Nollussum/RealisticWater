package Nollussum.realisticWater;

import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Levelled;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.FluidLevelChangeEvent;
import org.bukkit.event.world.WorldInitEvent;


public class WaterBlockFlowingEvent implements Listener{
    BlockFace[] flowFaces = {BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

    public boolean IsFluidWaterEnabled(){
        return true;
    }

    @EventHandler
    public void WorldInitializes(WorldInitEvent e){//disables water duplication bug.
        e.getWorld().setGameRule(GameRule.WATER_SOURCE_CONVERSION, false);
    }

    /*@EventHandler
    public void WWW(FluidLevelChangeEvent)*/ //Next Update will use this instead

    @EventHandler
    public void WaterBlockFlows(BlockFromToEvent event){
        Block Source = event.getBlock();
        if (Source.getType() != Material.WATER) {return;}

        Block target = event.getToBlock();

        //Makes sure event only continues for the TargetBlock
        for (BlockFace face : flowFaces){
            Block foo = Source.getRelative(face);
            if ((foo.getType() == Material.WATER || foo.getType() == Material.AIR) && (Source.getRelative(face).equals(target))){
                break;}//if TargetBlock == target: continue event
            if(face.equals(flowFaces[flowFaces.length-1])){
                event.setCancelled(true);return;}//No TargetBlock or TargetBlock != target: cancel event
        }

        int n_viable_flowFaces = 0;
        for (BlockFace face : flowFaces){
            Block foo = Source.getRelative(face);
            if(foo.getType()==Material.WATER || foo.getType()==Material.AIR){n_viable_flowFaces += 1;}
        }
        for (BlockFace face : flowFaces){
            Block to = Source.getRelative(face);
            if (to.getType() != Material.WATER && to.getType() != Material.AIR){continue;}
            //For every viable toBlock

            //SourceLevel & SourceLevel_val
            Levelled SourceLevel = (Levelled) Source.getBlockData();
            int SourceLevel_val = SourceLevel.getLevel();

            //toLevel & toLevel_val
            Levelled toLevel;
            int toLevel_val;
            if (to.getType() == Material.WATER){
                toLevel = (Levelled) to.getBlockData();
                toLevel_val = ((Levelled) to.getBlockData()).getLevel();
            }else{
                to.setType(Material.WATER);
                toLevel = (Levelled) to.getBlockData();
                toLevel_val = 8;
            }

            //Makes sure target water is less than source water
            if (toLevel_val < SourceLevel_val){return;}//If toLvl has more water(aka, smaller level)

            //Calculations
            //Checks to make sure the amount of water being moved is possible.
            int NetChange = ((n_viable_flowFaces-1>=0) ? 1:0);
            if (toLevel_val-NetChange < 0){NetChange = 0;
            }else if (SourceLevel_val+NetChange > 7){NetChange = 0;}
            //sets fromBlock's water level
            SourceLevel.setLevel(SourceLevel_val+NetChange);
            Source.setBlockData(SourceLevel);

            //sets toBlock to calculated water level.
            toLevel.setLevel(toLevel_val-NetChange);
            to.setBlockData(toLevel);
        }
    }

}