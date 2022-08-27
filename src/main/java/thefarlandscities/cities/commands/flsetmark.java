package thefarlandscities.cities.commands;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import org.apache.commons.lang.Validate;
import org.bson.BSON;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import thefarlandscities.cities.Cities;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.valueOf;

public class flsetmark implements CommandExecutor {


    private MongoClient mongoClient;
    private Cities plugin;
    public String name;
    public flsetmark(Cities plugin, MongoClient mongoClient){
        this.plugin = plugin;
        this.mongoClient = mongoClient;

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;
        int x = (int) p.getLocation().getX();
        int y2 = (int) p.getLocation().getY();
        int z = (int) p.getLocation().getZ();
        MongoCollection<Document> collection = mongoClient.getDatabase("whitelist").getCollection("cities");
        List<Document> cities = (List<Document>) collection.find().into(new ArrayList<Document>());
        Location og = new Location(p.getWorld(), (double)x, (double)y2, (double)z);

        for(Document City: cities){
            if(City.get("onMap").equals("no")){
                p.sendMessage(p.getWorld().toString());
                List<String> xi = (List<String>) City.get("x");
                List<String> y = (List<String>) City.get("y");
                for (int i = 0; i < xi.size(); i++) {
                    p.setFlying(true);
                    int ix = Integer.parseInt(xi.get(i));
                    int iy = Integer.parseInt(y.get(i));
                    p.teleport(new Location(p.getWorld(), (double) ix, 100 ,(double) iy));
                   // Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "/dmarker addcorner " + ix + " 0 " + iy + " TheFarlands");
                    p.chat("/dmarker addcorner ");
                }
                plugin.deregisterListener();
                plugin.getCitybars(cities);
                plugin.registerListener();
                p.sendMessage("Adding Markers");
                name = (String) City.get("label");
                name = name.replaceAll("\\s", "");
                p.chat("/dmarker addarea " + name);
                p.chat("/dmarker updatearea label:" + name + " fillcolor:" + City.get("fill") + " color:" + City.get("outline"));
                p.teleport(og);
                updateDB(City, collection);
            }
        }
        return false;
    }


    public void updateDB(Document City, MongoCollection<Document> mongoCollection){
        BasicDBObject query = new BasicDBObject();
        query.append("label", City.get("label"));
        BasicDBObject newDoc = new BasicDBObject();
        newDoc.append("$set", new BasicDBObject().append("onMap", "yes"));
        mongoCollection.updateOne(query,newDoc);
    }
}
