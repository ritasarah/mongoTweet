/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mongotweet;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;
import org.bson.conversions.Bson;

/**
 *
 * @author ASUS
 */
public class MongoTweet {

    static MongoClient mongo;
    static MongoDatabase db;
    static String collectionName;
        
    public static void main(String[] args) {
        // Since 2.10.0, uses MongoClient
        run();
    }
    
    public static void run() {
        printModeList();
        connect();
        
        boolean stopper = false;
        String mode = "", username = "";
        Scanner input = new Scanner(System.in);
        
        while (!stopper) {
            System.out.print("> ");
            mode = input.next().toLowerCase();
            if (mode.equals("/exit")) {
                stopper = true;
            }
            else if (mode.equals("/login")) {
                username = input.next().toLowerCase();
                String password = input.next().toLowerCase();
                System.out.println("login successful!");
            }
            else if (mode.equals("/register")) {
                username = input.next().toLowerCase();
                String password = input.next().toLowerCase();
                registerUser(username, password);
            }
            else if (mode.equals("/follow")) {
                String friend_name = input.next().toLowerCase();
                followFriend (username, friend_name);
            }
            else if (mode.equals("/tweet_msg")) {
                String msg = input.nextLine().toLowerCase();
                tweet(username, msg);
            }
            else if (mode.equals("/tweet")) {
                String nickname = input.next().toLowerCase();
                showTweet(nickname);
            }
            else if (mode.equals("/timeline")) {
                String nickname = input.next().toLowerCase();
                showTimeline(nickname);
            }
        }
    }
    
    public static void printModeList() {
        System.out.println("Ketik '/login nickname password' untuk login ke akun Anda");
        System.out.println("Ketik '/register nickname password' untuk bergabung ke twitter");
        System.out.println("Ketik '/follow friend_name' untuk mem-follow teman Anda");
        System.out.println("Ketik '/tweet_msg pesan_Anda' untuk men-tweet pesan Anda");
        System.out.println("Ketik '/tweet username' untuk menampilkan tweet dari username tertentu");
        System.out.println("Ketik '/timeline username' untuk menampilkan timeline dari username tertentu");
        System.out.println("Ketik '/exit' untuk keluar dari program\n");
    }
    
    public MongoTweet(String collection) {
        collectionName = collection;
    }
    
    public static void connect() {
        mongo = new MongoClient("localhost");
	db = mongo.getDatabase("anda");
    }
    
    public static void createCollection(String name) {
        db.createCollection(name);
    }
    
    public static void registerUser (String uname, String pass) {
        Document doc = new Document().append("username", uname).append("password", pass);
        insertDocument("users", doc);
        System.out.println(uname+" registered");
    }
    
    public static void insertDocument(String collection, Document obj) {
        MongoCollection coll = db.getCollection(collection);
        coll.insertOne(obj);
    }
    
    public static void followFriend(String uname,String friend){
        Document doc = new Document().append("username", uname).append("friend", friend).append("since", System.currentTimeMillis());
        insertDocument("friends", doc);
        doc = new Document().append("username", friend).append("follower", uname).append("since", System.currentTimeMillis());
        insertDocument("followers", doc);
        System.out.println(uname+" is now friends with "+friend);
    }
    
    public static void tweet(String uname,String tweet){
        Random rand = new Random();
        String id = "" + rand.nextInt(99999999);
                
        Document doc = new Document().append("tweet_id", id).append("username", uname).append("body", tweet);
        insertDocument("tweets", doc);
        doc = new Document().append("tweet_id", id).append("username", uname).append("time", System.currentTimeMillis());
        insertDocument("timeline", doc);
        insertDocument("userline", doc);
        System.out.println(uname+":"+tweet+" published");
        
        MongoCollection coll = db.getCollection("followers");
        BsonDocument where = new BsonDocument().append("username", new BsonString(uname));
        MongoCursor<Document> cursor = coll.find(where).iterator();
        
        String username;
        while (cursor.hasNext()) { 
            Document tmp = cursor.next();
            username = (String) tmp.get("follower");
            doc = new Document().append("tweet_id", id).append("username", username).append("time", System.currentTimeMillis());
            insertDocument("timeline", doc);
        }
    }
    
    public static void showTweet(String uname){
        MongoCollection coll = db.getCollection("tweets");
        BsonDocument where = new BsonDocument().append("username", new BsonString(uname));
        MongoCursor<Document> cursor = coll.find(where).iterator();
        
        String username, body;
        while (cursor.hasNext()) { 
            Document tmp = cursor.next();
            
            username = (String) tmp.get("username");
            body = (String) tmp.get("body");
            System.out.format("%s : %s \n", username, body);
        }
    }
    
    public static void showTimeline(String uname){
        MongoCollection coll = db.getCollection("timeline");
//        MongoCursor<Document> cursor = coll.find().iterator();
        BsonDocument where = new BsonDocument().append("username", new BsonString(uname));
        MongoCursor<Document> cursor = coll.find(where).iterator();
        
        String username, body, tweet_id;
        while (cursor.hasNext()) { 
            tweet_id = (String) cursor.next().get("tweet_id");
            BsonDocument where2 = new BsonDocument().append("tweet_id", new BsonString(tweet_id));
            MongoCollection col2 = db.getCollection("tweets/");
            MongoCursor<Document> cursor2 = col2.find(where2).iterator();
            while (cursor2.hasNext()) {
                Document tmp = cursor2.next();
                username = (String) tmp.get("username");
                body = (String) tmp.get("body");
                System.out.format("%s : %s \n", username, body);
            }
        }
    }
}
