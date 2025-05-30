package Nollussum.realisticWater;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class RealisticWater extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        System.out.println("aye bro :D");
        getServer().getPluginManager().registerEvents(new WaterBlockFlowingEvent(), this);
        // Plugin startup logic
    }

    @Override
    public void onDisable() {
        System.out.println("bye bro :(");
        // Plugin shutdown logic
    }
}