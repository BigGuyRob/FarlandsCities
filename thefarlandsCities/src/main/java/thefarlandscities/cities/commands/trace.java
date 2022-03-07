package thefarlandscities.cities.commands;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import org.apache.commons.lang.Validate;
import org.bson.Document;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import thefarlandscities.cities.Cities;
import thefarlandscities.cities.City;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class trace implements CommandExecutor {


    private MongoClient mongoClient;
    private Cities plugin;
    public String name;
    private List<City> cityList;


    public trace(Cities plugin, MongoClient mongoClient, List<City> cityList){
        this.plugin = plugin;
        this.mongoClient = mongoClient;
        this.cityList = cityList;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;
        int x = (int) p.getLocation().getX();
        int y2 = (int) p.getLocation().getY();
        int z = (int) p.getLocation().getZ();
        boolean inCity = false;
        City dbcity = null;
        MongoCollection<Document> collection = mongoClient.getDatabase("whitelist").getCollection("cities");
        List<Document> cities = (List<Document>) collection.find().into(new ArrayList<Document>());
        Location og = new Location(p.getWorld(), (double)x, (double)y2, (double)z);

        for(City city : cityList){
            if(city.getPolygon().contains(x,z)){
             dbcity = city;
             inCity = true;
            }
        }

        if(!inCity){
            p.sendMessage("You are not in a city");
            return false;
        }else{
        int[] xi = dbcity.getPolygon().xpoints;
        int[] yi = dbcity.getPolygon().ypoints;
        if(p.getWorld().getName().endsWith("_nether")){p.sendMessage("cannot trace in nether"); return false;}
        for(int i = 1; i < xi.length; i++) {
            Location loc1 = new Location(p.getWorld(), xi[i - 1], y2, yi[i - 1]);
            Location loc2 = new Location(p.getWorld(), xi[i], y2, yi[i]);
            try {
                drawLine(loc1, loc2, .1, p);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        p.sendMessage("Borders drawn");
        return true;
        }
    }
        public void drawLine(Location loc1, Location loc2, double space, Player p) throws InterruptedException {
            World world = loc1.getWorld();
            Validate.isTrue(loc2.getWorld().equals(world), "Lines cannot be in different worlds!");
            double distance = loc2.distance(loc1);
            Vector p1 = loc1.toVector();
            Vector p2 = loc2.toVector();
            Vector vector = p2.clone().subtract(p1).multiply(space);
            for (double i = 0; i <= distance; i += space) {
                Vector addition = new Vector().copy(vector).multiply(i);
                Location newLoc = loc1.clone().add(addition);
                try {
                    TimeUnit.MICROSECONDS.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println("FUcked");
                }
                p.sendMessage(newLoc.toString());
                p.teleport(newLoc);
                loc1.getWorld().spawnParticle(Particle.REDSTONE,newLoc, 0, 0, 2, 0, new Particle.DustOptions(Color.WHITE, 1));
            }
        }
}
