package baikeClaw;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

//import org.jsoup.nodes.Document;

public class MongoDBOP {
	private String title;
	private Basic basic;
	
	
	
	static MongoCredential credential =null;
	static MongoClient mongoClient =null;
	static MongoDatabase db =null;
	static MongoCollection<Document> collection =null;
	
//	static{
//		credential = MongoCredential.createCredential("lab681","Plover","bigsearch".toCharArray());
//		mongoClient = new MongoClient(new ServerAddress("218.76.52.44",27017),Arrays.asList(credential));	//3006设置为Mongodb端口号
//		db = mongoClient.getDatabase("Plover");
//		collection = db.getCollection("BaikeInfo"); 
//	}
	public MongoDBOP() {
		// TODO Auto-generated constructor stub
	}
	public boolean updateMongoDB(Basic basic){
		return false;
	}
	public Basic searchMongDB(String title){
		this.title=title;
		return basic;
	}
	
	public static void main(String[] args){
		try {  
            //连接到MongoDB服务 如果是远程连接可以替换“localhost”为服务器所在IP地址  
            //ServerAddress()两个参数分别为 服务器地址 和 端口  
            ServerAddress serverAddress = new ServerAddress("218kk.76.52.44",27017);  
            List<ServerAddress> addrs = new ArrayList<ServerAddress>();  
            addrs.add(serverAddress);  
              
            //MongoCredential.createScramSha1Credential()三个参数分别为 用户名 数据库名称 密码  
            MongoCredential credential = MongoCredential.createScramSha1Credential("username", "databaseName", "password".toCharArray());  
            List<MongoCredential> credentials = new ArrayList<MongoCredential>();  
            credentials.add(credential);  
              
            //通过连接认证获取MongoDB连接  
            MongoClient mongoClient = new MongoClient(addrs,credentials);  
              
            //连接到数据库  
            MongoDatabase mongoDatabase = mongoClient.getDatabase("databaseName");  
            System.out.println("Connect to database successfully");  
        } catch (Exception e) {  
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );  
        }  
	}
}
