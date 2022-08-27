package thefarlandscities.cities.listeners;

import com.mongodb.client.MongoClient;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import thefarlandscities.cities.Cities;
import thefarlandscities.cities.City;

import java.util.ArrayList;
import java.util.List;


public class breakListener implements Listener {

    private static Cities plugin;
    private List<City> cityList;
    public breakListener(Cities plugin, List<City> cityList) {
        this.plugin = plugin;
        this.cityList = cityList;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event){
        Block b = event.getBlock();
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
