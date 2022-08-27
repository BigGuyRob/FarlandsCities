package thefarlandscities.cities;

import com.mongodb.client.MongoClient;
import org.bson.Document;
import org.bukkit.boss.BossBar;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
public class City {

    private Polygon area;
    private BossBar cityBar;
    private String name;
    private List<String> permits;
    private MongoClient mongoClient;
    public City(Polygon polygon, BossBar bar, String name, MongoClient mongoClient){
        this.area = polygon;
        this.cityBar = bar;
        this.name = name;
        this.mongoClient = mongoClient;
    }

    public Polygon getPolygon(){
        return area;
    }

    public BossBar getCityBar(){
        return cityBar;
    }

    public String getName(){
        return name;
    }

    public List<String> getPermits(){
        Document filter = new Document("label", this.getName());
        Document found = (Document) mongoClient.getDatabase("whitelist").getCollection("cities").find(filter).first();
        List<String> s = (List<String>) found.get("permits");
        return s;
    }
}
