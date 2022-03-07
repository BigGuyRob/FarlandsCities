package thefarlandscities.cities.listeners;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import thefarlandscities.cities.Cities;
import thefarlandscities.cities.City;

import java.util.List;


public class intereactListener implements Listener {

    private static Cities plugin;
    private List<City> cityList;
    public intereactListener(Cities plugin, List<City> cityList) {
        this.plugin = plugin;
        this.cityList = cityList;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        Block b = event.getClickedBlock();
        Player player = event.getPlayer();
        int xt = (int) b.getLocation().getX();
        int zt = (int) b.getLocation().getZ();
        for(City city : cityList){
            if(city.getPolygon().contains(xt,zt)){

                List<String> permits = (List<String>) city.getPermits();
                if(permits.contains(player.getName())){
                 //They are allowed to break the block so we dont have to do anything here
                }else{
                    //If they do not have a permit
                    player.sendMessage("You do not have a permit to build here");
                    event.setCancelled(true);
                }
            }else{
                city.getCityBar().removePlayer(player);
            }
        }

    }

}
