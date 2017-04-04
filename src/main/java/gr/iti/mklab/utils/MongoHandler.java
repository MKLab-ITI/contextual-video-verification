package gr.iti.mklab.utils;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;

public class MongoHandler {

	private String localhostIp = "localhost";
	private String username = "";
	private String password = "";
	
	private HashMap<MongoClientURI, MongoClient> connections = new HashMap<MongoClientURI, MongoClient>();
	
	private static MongoHandler sInstance = new MongoHandler();

	public static MongoHandler getInstance() {

		if (sInstance == null) {
			sInstance = new MongoHandler();
		}

		return sInstance;
	}
	
	
	  /**
	  * Checks if an activity exists with a given id. if no such activity exists
	  * returns false. Returns true for one or more activities with a matching
	  * id.
	  * 
	  * @param db
	  * @param id
	  * @return boolean - true if one or more functions with matching names exit.
	  */
	  public boolean documentsExists(MongoDatabase db, String collection, String id) {
	      FindIterable<Document> iterable = db.getCollection(collection).find(new Document("_id", id));
	      return iterable.first() != null;
	  }
	  
	

	public String getLocalhostIp() {
		return localhostIp;
	}

	public void setLocalhostIp(String localhostIp) {
		this.localhostIp = localhostIp;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public MongoClient getMongoClient(String dbString, String mongoHostIP) {
		
		//MongoClientURI uri = new MongoClientURI("mongodb://"+getUsername()+":"+getPassword()+"@"+getLocalhostIp()+"/"+dbString);
		//ask for MongoClient instance if it exists
		//MongoClientURI uri = new MongoClientURI();
		MongoClient mongo  = null;
		setLocalhostIp(mongoHostIP);
		//mongo = new MongoClient(getLocalhostIp());
		mongo = new MongoClient(getLocalhostIp());
		
		return mongo;
	}
	//savedIds = MongoHandler.getInstance().getExistingTweetsIds("VideoVerificationTweets", vInfo.getVideoId(), "id_str");
	public List<String> getExistingTweetsIds(String dbString, String coll,
			String idFieldName) throws UnknownHostException {

		List<String> ids = new ArrayList<String>();

		//Mongo mongo = new MongoClient(getLocalhostIp());
		MongoClient mongo = getMongoClient(dbString, getLocalhostIp());
		DB db = mongo.getDB(dbString);

		// get a single collection
		DBCollection collection = db.getCollection(coll);

		DBCursor cursor = collection.find();

		while (cursor.hasNext()) {
			String s = JSON.serialize(cursor.next());
			JSONObject json = new JSONObject(s);

			try {
				String id = json.getString(idFieldName).replaceAll("[^\\d.]", "");
				
				ids.add(id);
			}catch (Exception e) {
				
			}

		}
		// olga close mongo
				mongo.close();
		return ids;
	}
	
	public String getStatusFromMongo(String dbString, String coll,
			String idFieldName) throws UnknownHostException {

		List<String> ids = new ArrayList<String>();

		MongoClient mongo = getMongoClient(dbString, getLocalhostIp());
		DB db = mongo.getDB(dbString);

		// get a single collection
		DBCollection collection = db.getCollection(coll);

		DBCursor cursor = collection.find();
		
		while (cursor.hasNext()) {
			String s = JSON.serialize(cursor.next());
			JSONObject json = new JSONObject(s);
			
			if (!json.isNull(idFieldName)) {
				ids.add(json.getString(idFieldName));
			}
			
		}
		// olga close mongo
		mongo.close();
		return ids.get(0);
	}
	//MongoHandler.getInstance().updateField("VideoVerificationTweets", vInfo.getVideoId(), currentStatus, "processing_status", "searching");
	public void updateField(String dbString, String coll, String statusValue, String key, String value) {
			
		//new document to replace the old one
		BasicDBObject newDocument = new BasicDBObject();
		newDocument.put(key, value);
		
		//search query to find the existing document
		BasicDBObject searchQuery = new BasicDBObject().append(key, statusValue);
		 
		MongoClient mongo = getMongoClient(dbString, getLocalhostIp());
		DB db = mongo.getDB(dbString);

		// get a single collection
		DBCollection collection = db.getCollection(coll);
		
		//update the document
		collection.update(searchQuery, newDocument);
		// olga close mongo
				mongo.close();
	}

	public List<String> getExistingTweetsIdsByVideoId(String dbString,
			String coll, String videoID, String idFieldName)
			throws UnknownHostException {

		List<String> ids = new ArrayList<String>();

		//Mongo mongo = new MongoClient(getLocalhostIp());
		MongoClient mongo = getMongoClient(dbString, getLocalhostIp());
		DB db = mongo.getDB(dbString);

		// get a single collection
		DBCollection collection = db.getCollection(coll);

		// find object where id_str = id
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put(idFieldName, videoID);

		DBCursor cursor = collection.find(whereQuery);

		while (cursor.hasNext()) {
			String s = JSON.serialize(cursor.next());
			JSONObject json = new JSONObject(s);

			// String id = json.getString(idFieldName).replaceAll("[^\\d.]",
			// "");
			JSONArray idsArray = json.getJSONArray("tweet_ids");
			for (int i = 0; i < idsArray.length(); i++) {
				ids.add(idsArray.get(i).toString());
			}

		}
		// olga close mongo
				mongo.close();
		return ids;
	}

	public void insertSingleJSONToDBColl(JSONObject json, String dbString,
			String collString, String field) throws UnknownHostException {

		int counter = 0;

		List<String> existingTweetIds = getExistingTweetsIds(dbString,
				collString, field);

		Mongo mongo;
		try {
			//mongo = new MongoClient(getLocalhostIp());
			mongo = getMongoClient(dbString, getLocalhostIp());
			DB db = mongo.getDB(dbString);
			DBCollection collection = db.getCollection(collString);

			if (!existingTweetIds.contains(json.get(field))) {
				DBObject dbObject = (DBObject) JSON.parse(json.toString());
				collection.insert(dbObject);
				counter++;
				existingTweetIds.add(json.getString(field));
			} else {
				System.out.println(json.get(field) + " existed");
			}
			// olga close mongo
			mongo.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// info
		System.out.println(counter + " items inserted in " + dbString + ", "
				+ collString);
	}

	public void insertJSONsToDBColl(List<JSONObject> jsons, String dbString,
			String collString, String field) throws UnknownHostException {

		int counter = 0;

		List<String> existingTweetIds = getExistingTweetsIds(dbString,
				collString, field);

		Mongo mongo;
		try {
			//mongo = new MongoClient(getLocalhostIp());
			mongo = getMongoClient(dbString, getLocalhostIp());
			DB db = mongo.getDB(dbString);
			DBCollection collection = db.getCollection(collString);

			for (JSONObject json : jsons) {

				if (!existingTweetIds.contains(json.get(field))) {
					DBObject dbObject = (DBObject) JSON.parse(json.toString());
					collection.insert(dbObject);
					counter++;
					existingTweetIds.add(json.getString(field));
				} else {
					System.out.println(json.get(field) + " existed");
				}
			}
			// olga close mongo
			mongo.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// info
		System.out.println(counter + " items inserted in " + dbString + ", "
				+ collString);
	}

}
