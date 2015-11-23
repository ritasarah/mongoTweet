/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mongotweet;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import java.util.List;

/**
 *
 * @author ASUS
 */
public class MongoTweet {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Since 2.10.0, uses MongoClient
	MongoClient mongo = new MongoClient( "192.168.1.24");
        
	MongoDatabase db = mongo.getDatabase("anda");

//        boolean auth = db.authenticate("hadoop", "hadoop".toCharArray());
        
        MongoIterable<String> dbs = mongo.listDatabaseNames();
	for(String adb : dbs){
		System.out.println(adb);
	}
        
    }
    
}
