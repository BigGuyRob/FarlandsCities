package thefarlandscities.cities;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import com.mongodb.client.MongoClients;
import org.bukkit.scheduler.BukkitTask;
import thefarlandscities.cities.commands.*;
import thefarlandscities.cities.listeners.breakListener;
import thefarlandscities.cities.listeners.cityListener;
import thefarlandscities.cities.listeners.intereactListener;
import thefarlandscities.cities.listeners.placeListener;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.valueOf;

public final class Cities extends JavaPlugin {
    private  List<City> cityList = new ArrayList<City>();
    private MongoClient mongoClient;
    public intereactListener i;
    public breakListener b;
    public BukkitTask c;
    private placeListener p;
    @Override
    public void onEnable() {
        // Plugin startup logic

        saveDefaultConfig();
        mongoClient = MongoClients.create("MONGODBSTRING");
        MongoCollection<Document> collection = mongoClient.getDatabase("whitelist").getCollection("cities");
        List<Document> cities = (List<Document>) collection.find().into(
                new ArrayList<Document>());
        getCitybars(cities);
        getCommand("flsetmark").setExecutor(new flsetmark(this, mongoClient));
        getCommand("permit").setExecutor(new permit(this, mongoClient));
        getCommand("depermit").setExecutor(new depermit(this, mongoClient));
        getCommand("permitaddadmin").setExecutor(new permitadminadd(this, mongoClient));
        getCommand("permitremoveadmin").setExecutor(new permitadminremove(this, mongoClient));
        getCommand("trace").setExecutor(new trace(this, mongoClient, cityList));
        registerListener();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void getCitybars(List<Document> cities){
        List<City> tempcitylist = new ArrayList<City>();
        for(City city: cityList){
            city.getCityBar().removeAll();
        }
        for(Document City: cities){
            String cityName = (String) City.get("label");
            String cityColor = (String) City.get("color");
            BossBar cityBar = Bukkit.getServer().createBossBar(cityName, BarColor.valueOf(cityColor), BarStyle.SOLID, BarFlag.PLAY_BOSS_MUSIC);
            List<String> x = (List<String>) City.get("x");
            List<String> y = (List<String>) City.get("y");
            Polygon polygon = new Polygon();
            for (int i = 0; i < x.size(); i++) {
                polygon.addPoint(valueOf(x.get(i)), valueOf(y.get(i)));
            }
            City tempcity = new City(polygon, cityBar, cityName, mongoClient);
            tempcitylist.add(tempcity);
        }
        cityList = tempcitylist;

    }

    public void registerListener(){
        c = new cityListener(this, mongoClient, cityList).runTaskTimer(this,0, 40L);
       b = new breakListener(this, cityList);
       p = new placeListener(this, cityList);
       i = new intereactListener(this, cityList);
    }

    public void deregisterListener(){
        HandlerList.unregisterAll(b);
        HandlerList.unregisterAll(p);
        HandlerList.unregisterAll(i);
        c.cancel();
    }



}
