package thefarlandscities.cities.listeners;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import thefarlandscities.cities.Cities;
import thefarlandscities.cities.City;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.awt.Polygon;

import static java.lang.Integer.valueOf;

public class cityListener extends BukkitRunnable {


    private static Cities plugin;
    private MongoClient mongoClient;
    private List<City> cityList;

    public cityListener(Cities plugin, MongoClient mongoClient, List<City> cityList) {
        this.plugin = plugin;
        this.mongoClient = mongoClient;
        this.cityList = cityList;
    }

    @EventHandler
    public void run() {

        Collection<Player> p = (Collection<Player>) Bukkit.getServer().getOnlinePlayers();
        for(Player player : p){
            if(player.getWorld().getName().endsWith("_nether")){break;}

            int xt = (int) player.getLocation().getX();
            int zt = (int) player.getLocation().getZ();
            for(City city : cityList){
                if(city.getPolygon().contains(xt,zt)){
                    city.getCityBar().addPlayer(player);
                }else{
                    city.getCityBar().removePlayer(player);
                    player.setCanPickupItems(true);
                }
            }
        }
    }


}
