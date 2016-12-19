package tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.bson.Document;
import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import baikeClaw.Basic;
import utilizeCorpus.ReturnExtend;

public class DataBaseOP {	
	static MongoClientURI connectionString =null;
	static MongoClient mongoClient =null;
	static MongoDatabase database =null;
	static MongoCollection<Document> basicCollection;
	static MongoCollection<Document> extentCollection;
	
	static{
		 connectionString = new MongoClientURI("mongodb://localhost:27017");
		 mongoClient = new MongoClient(connectionString);
		 database = mongoClient.getDatabase("plover");
		 basicCollection = database.getCollection("basic");
		 extentCollection = database.getCollection("extent");
	}
	
	public static boolean addBasic(Basic basic){
		if(basic==null){
			System.out.println("parameter is null");
			return false;
		}
		if(basicCollection.find(new Document("title",basic.getTitle())).first() != null){
			System.out.println(basic.getTitle()+" is in the database");
			return false;
		}
		try {
			basicCollection.insertOne(basic2Document(basic));
		} catch (Exception e) {
			System.out.println("insert basic failed");
			return false;
		}
		return true;
	}
	public static boolean addePoly(List<Basic> poly){
		if(poly==null){
			System.out.println("parameter is null");
			return false;
		}
		for(Basic basic:poly){
			try {
				addBasic(basic);
			} catch (Exception e) {
				System.out.println("insert poly failed");
				return false;
			}
		}
		return true;
	}
	public static Basic getBasicCellection(String title){
		Document document=basicCollection.find(new Document("title",title)).first();
		if(document!=null){
			return document2Basic(document);
		}
		return null;
	}

	public static Boolean inBasicCellection(String title){
		if(basicCollection.find(new Document("title",title)).first() != null){
			return true;
		}
		return false;
	}
	
	public static List<Basic> randomBasic(int number){
		if(number<1){
			System.out.println("number should be larger than 0 and less than 20");
			return null;
		}
		FindIterable<Document> allBasic=basicCollection.find();
		long count=basicCollection.count();
		List<Basic> listBasic=new ArrayList<>();
		Basic tempBasic;
		if(count<number){
			for(Document basic:allBasic){
				listBasic.add(document2Basic(basic));
			}
			return listBasic;
		}
		long random;
		for(int i=0;i<number;i++){
			random=(long) (Math.random()*count);
			tempBasic=document2Basic(allBasic.skip((int) random).first());
			System.out.println(random);
			if(!listBasic.contains(tempBasic))
				listBasic.add(tempBasic);//there is a bug. Integer maximum is less than Long maximum
		}
		return listBasic;
	}
	
	private static Document basic2Document(Basic basic){
		return new Document("title",basic.getTitle())
				.append("descText", basic.getDescriptText())
				.append("editCount", basic.getEditCount())
				.append("lastEdit", basic.getLastEdit())
				.append("label", basic.getLabels())
//				.append("referMaterial", basic.getReferMaterial())
				.append("innerLink", basic.getInnerLink());
	}
	@SuppressWarnings("unchecked")
	private static Basic document2Basic(Document doc){
		Basic basic=new Basic();
		basic.setTitle(doc.getString("title"));
		basic.setDescriptText(doc.getString("descText"));
		basic.setEditCount(doc.getInteger("editCount"));
		basic.setLastEdit(doc.getDate("lastEdit"));
		basic.setLabels((List<String>)doc.get("label"));
		basic.setInnerLink(doc2MapStrStr((Document)doc.get("innerLink")));
//		basic.setReferMaterial(doc2MapStrStr((Document) doc.get("referMaterial")));
		return basic;		
	}

	private static HashMap<String, String> doc2MapStrStr(Document doc){
		HashMap<String, String> map=new HashMap<>();
		Set<String> words=doc.keySet();
		for(String word:words){
			map.put(word, doc.getString(word));
		}
		return map;
	}
	
	private static HashMap<String, Double> doc2MapStrDou(Document doc){
		LinkedHashMap<String, Double> map=new LinkedHashMap<>();
		Set<String> words=doc.keySet();
		for(String word:words){
			map.put(word, doc.getDouble(word));
		}
		return map;
	}
	
	private static Document fw2Document(ReturnExtend fw){
		return new Document().append("title", fw.getTitle())
				.append("tfidfWords", fw.getTfidfWords())
				.append("labelWords", fw.getLabelWords())
				.append("illusionWords", fw.getIllusionWords());
	}
	
	private static ReturnExtend document2Fw(Document fw){
		ReturnExtend returnFw=new ReturnExtend();
		returnFw.setTitle(fw.getString("title"));
		returnFw.setTfidfWords(doc2MapStrDou((Document) fw.get("tfidfWords")));
		return returnFw;
	}
	
	
	public static void addExtent(List<ReturnExtend> returnFW) {
		// TODO Auto-generated method stub
		String title;
		Document tempDoc;
		for(ReturnExtend fw:returnFW){
			title=fw.getTitle();
			tempDoc=extentCollection.find(new Document("title",title)).first();
			if(tempDoc==null){
				extentCollection.insertOne(fw2Document(fw));
			}
		}
	}
	
	public static ReturnExtend searchExtend(String search) {
		Document searchDoc=extentCollection.find(new Document("title", search)).first();
		if(searchDoc==null){
			System.out.println(search+" not found");
			return null;
		}
		return document2Fw(searchDoc);
	}
	
	public static void main(String[] args){
//		Basic test=DataBaseOP.searchDataBase("苹果公司");
////		System.out.println(test.getTitle());
////		System.out.println(test.getInnerLink());
////		System.out.println(test.getDescriptText());
////		System.out.println(test.getLabels());
////		System.out.println(test.getLastEdit());
////		System.out.println(test.getEditCount());
//		System.out.println(test);
		
		FindIterable<Document> teString=basicCollection.find();
		System.out.println(teString.skip(10).first());
		
	}
	
}
