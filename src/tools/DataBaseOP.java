package tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bson.Document;
import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import baikeClaw.Basic;

public class DataBaseOP {
	static MongoClientURI connectionString = null;
	static MongoClient mongoClient = null;
	static MongoDatabase database = null;
	static MongoCollection<Document> basicCollection;
	static MongoCollection<Document> tfidfCollection;
	static MongoCollection<Document> labelCollection;
	static MongoCollection<Document> indexCollection;

	static {
		connectionString = new MongoClientURI("mongodb://localhost:27017");
		mongoClient = new MongoClient(connectionString);
		database = mongoClient.getDatabase("plover");
		basicCollection = database.getCollection("basic");
		tfidfCollection = database.getCollection("tfidf");
		labelCollection = database.getCollection("label");
		indexCollection = database.getCollection("index");
	}

	public static boolean addBasic(Basic basic) {
		if (basic == null) {
			System.out.println("parameter is null");
			return false;
		}
		String title=basic.getTitle();
		if (basicCollection.find(new Document("title", title)).first() != null) {
			System.out.println(title + " is in the database");
			return false;
		}
		try {
			basicCollection.insertOne(basic2Document(basic));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("insert basic failed");
			return false;
		}
		return true;
	}

	public static void addPoly(List<Basic> poly) {
		if (poly == null) {
			System.out.println("parameter is null");
			return ;
		}
		for (Basic basic : poly) {
			addBasic(basic);
		}
	}

	public static void addIndex(String searchWord, String title) {
		Document document = new Document("word", searchWord).append("title", title);
		indexCollection.insertOne(document);
	}

	public static void addTfidf(Map<String, Map<String, Double>> tfidfResult) {
		if(tfidfResult.size()>0){
			for(Entry<String, Map<String, Double>> tfidf:tfidfResult.entrySet()){
				Document tfidfresult=new Document("title",tfidf.getKey()).append("tfidf", tfidf.getValue());
				tfidfCollection.insertOne(tfidfresult);
			}
		}
	}

	public static void addLabel(Map<String, Map<String, Double>> labelResult) {
		// TODO Auto-generated method stub
		if(labelResult.size()>0){
			for(Entry<String, Map<String, Double>> label:labelResult.entrySet()){
				Document tfidfresult=new Document("title",label.getKey()).append("label", label.getValue());
				labelCollection.insertOne(tfidfresult);
			}
		}
	}
	
	public static Basic getBasic(String title) {
		Document document = basicCollection.find(new Document("title", title)).first();
		if (document != null) {
			return document2Basic(document);
		}
		return null;
	}

	public static List<Basic> getPoly(List<String> titles) {
		List<Basic> results = new ArrayList<>();
		Basic temp;
		for (String title : titles) {
			temp = getBasic(title);
			if (temp != null)
				results.add(temp);
		}
		return results;
	}

	//返回searchWord对应的百科的title
	public static List<String> getIndex(String searchWord) {
		FindIterable<Document> words = indexCollection.find(new Document("word", searchWord));
		List<String> searchResult = new ArrayList<>();
		for (Document doc : words) {
			searchResult.add(doc.getString("title"));
		}
		Document title=indexCollection.find(new Document("title", searchWord)).first();
		if(title!=null&&!searchResult.contains(searchWord)){
			searchResult.add(searchWord);
		}
		return searchResult;
	}

	public static HashMap<String, Double> getTfidf(String searchWord){
		Document result=tfidfCollection.find(new Document("title", searchWord)).first();
		if(result!=null){
			return doc2MapStrDou((Document) result.get("tfidf"));
		}
		return null;
	}
	
	public static Map<String, Double> getLabel(String searchWord) {
		Document result=labelCollection.find(new Document("title", searchWord)).first();
		if(result!=null){
			return doc2MapStrDou((Document) result.get("label"));
		}
		return null;
	}
	
	public static List<Basic> randomBasices(int number) {
		if (number < 1) {
			System.out.println("number should be larger than 0 and less than 20");
			return null;
		}
		FindIterable<Document> allBasic = basicCollection.find();
		long count = basicCollection.count();
		List<Basic> listBasic = new ArrayList<>();
		Basic tempBasic;
		if (count < number) {
			for (Document basic : allBasic) {
				listBasic.add(document2Basic(basic));
			}
			return listBasic;
		}
		long random;
		for (int i = 0; i < number; i++) {
			random = (long) (Math.random() * count);
			tempBasic = document2Basic(allBasic.skip((int) random).first());
			if (!listBasic.contains(tempBasic))
				listBasic.add(tempBasic);// there is a bug. Integer maximum is
											// less than Long maximum
		}
		return listBasic;
	}

	private static Document basic2Document(Basic basic) {
		return new Document("title", basic.getTitle()).append("descText", basic.getDescriptText())
				.append("editCount", basic.getEditCount()).append("lastEdit", basic.getLastEdit())
				.append("label", basic.getLabels()).append("innerLink", basic.getInnerLink());
	}

	@SuppressWarnings("unchecked")
	private static Basic document2Basic(Document doc) {
		Basic basic = new Basic();
		basic.setTitle(doc.getString("title"));
		basic.setDescriptText(doc.getString("descText"));
		basic.setEditCount(doc.getInteger("editCount"));
		basic.setLastEdit(doc.getDate("lastEdit"));
		basic.setLabels((List<String>) doc.get("label"));
		basic.setInnerLink(doc2MapStrStr((Document) doc.get("innerLink")));
		return basic;
	}

	private static HashMap<String, String> doc2MapStrStr(Document doc) {
		HashMap<String, String> map = new HashMap<>();
		Set<String> words = doc.keySet();
		for (String word : words) {
			map.put(word, doc.getString(word));
		}
		return map;
	}

	private static HashMap<String, Double> doc2MapStrDou(Document doc) {
		LinkedHashMap<String, Double> map = new LinkedHashMap<>();
		Set<String> words = doc.keySet();
		for (String word : words) {
			map.put(word, doc.getDouble(word));
		}
		return map;
	}

	public static void main(String[] args) {
		FindIterable<Document> teString = basicCollection.find();
		System.out.println(teString.skip(10).first());
	}
}
