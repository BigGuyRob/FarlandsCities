package thefarlandscities.cities.commands;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import thefarlandscities.cities.Cities;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.valueOf;

public class permitadminadd implements CommandExecutor {

    private Cities plugin;
    private MongoClient mongoClient;
    private Document dbcity;

    public permitadminadd(Cities plugin, MongoClient mongoClient) {
        this.plugin = plugin;
        this.mongoClient = mongoClient;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        MongoCollection<Document> collection = mongoClient.getDatabase("whitelist").getCollection("cities");
        List<Document> cities = (List<Document>) collection.find().into(
                new ArrayList<Document>());
        Player player = (Player) sender;
        int xt = (int) player.getLocation().getX();
        int zt = (int) player.getLocation().getZ();
        //look for what city the player is in
        for (Document City : cities) {
            List<String> x = (List<String>) City.get("x");
            List<String> y = (List<String>) City.get("y");
            Polygon polygon = new Polygon();
            for (int i = 0; i < x.size(); i++) {
                polygon.addPoint(valueOf(x.get(i)), valueOf(y.get(i)));
            }
            if (polygon.contains(xt, zt)) {
                dbcity = City;
                player.sendMessage("You are in " + dbcity.get("label"));
            }
        }
        List<String> owner = (List<String>) dbcity.get("owner");
        if (owner.contains(player.getName())) {
            // do the command structure
                Player target = Bukkit.getPlayer(args[0]);
                if (target != null && target!=player ) {
                    List<String> admins = (List<String>) dbcity.get("admins");
                    List<String> permits = (List<String>) dbcity.get("permits");
                    if(!permits.contains(target.getName())){
                        player.sendMessage("Player must have a permit before being an admin");
                    }else {
                        if (admins.contains(target.getName())) {
                            player.sendMessage(target.getName() + " is already an admin in " + dbcity.get("label"));
                        } else {
                            admins.add(target.getName());
                            updateDB(dbcity, collection, admins);
                            player.sendMessage("You have added " + target.getName() + "as an admin in " + dbcity.get("label"));
                        }
                    }
                } else {
                    player.sendMessage("The second arguement must be an online player name and you cannot add yourself as an admin");
                }
        }else{
            //Player does not have "permission" to use this command
            player.sendMessage("You cannot add admins to " + dbcity.get("label"));
        }
        return true;
        }

    public void updateDB(Document City, MongoCollection<Document> mongoCollection, List<String> temppermits){
        BasicDBObject query = new BasicDBObject();
        query.append("label", City.get("label"));
        BasicDBObject newDoc = new BasicDBObject();
        newDoc.append("$set", new BasicDBObject().append("admins", temppermits));
        mongoCollection.updateOne(query,newDoc);
    }
    }
