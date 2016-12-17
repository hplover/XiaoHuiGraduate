package baikeClaw;

import java.util.ArrayList;
/*
 * get all the basicInfo if the status means the search word is polysemy by concurrent threads
 * and write to MongoDB
 */
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Parse {
	ExecutorService executorService = Executors.newCachedThreadPool();
	
	private Claw claw;
	private Document htmlPage;
	private int status;
	private Basic basicInfo;
	private List<Basic> concurrent=new ArrayList<>();
	public Parse(String searchWord){
		claw=new Claw(searchWord);
		htmlPage=claw.getHtmlPage();
		status=claw.getStatus();
		
		switch (status) {
		case -1:
		case 0:
			System.out.printf("failed to get %s",searchWord);
			break;
		case 1:
		case 2:
			generateBasicInfo(htmlPage);
			break;
		case 3:
			generatePoly(htmlPage);
			break;
		case 4:
			generateList(htmlPage);
			break;
		default:
			break;
		}	
		writeToMongDB(basicInfo);
		writeToMongoDB(concurrent);
	}	
	
	private void writeToMongoDB(List<Basic> concurrent2) {
		// TODO Auto-generated method stub
		if(concurrent2==null&&concurrent2.size()==0){
			return ;
		}
		for(Basic basic:concurrent2){
			writeToMongDB(basic);
		}
	}

	private void writeToMongDB(Basic basicInfo2) {
		// TODO Auto-generated method stub
		if(basicInfo2==null){
			return ;
		}
		new MongoDBOP().updateMongoDB(basicInfo2);
	}

	private void generateList(Document htmlPage2) {
		ConList conList=new ConList(htmlPage2);
		HashMap<String, String> word2URL=conList.getWordURL();
		for(Entry<String, String> list:word2URL.entrySet()){
			executorService.execute(new conCurrentClaw(concurrent, list.getValue()));
		}
		executorService.shutdown();
		try {
            executorService.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException ex) {
            System.out.println(ex.getMessage());
        }
	}

	private void generatePoly(Document htmlPage2) {
		Basic defaultBasicInfo=new Basic(htmlPage2);
		concurrent.add(defaultBasicInfo);
		ConPoly conPoly=new ConPoly(htmlPage2);
		HashMap<String, String> word2URL=conPoly.getWordURL();
		for(Entry<String,String> list:word2URL.entrySet()){
			executorService.execute(new conCurrentClaw(concurrent, list.getValue()));
		}
		executorService.shutdown();
		try {
            executorService.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException ex) {
            System.out.println(ex.getMessage());
        }
	}
	
	private void generateBasicInfo(Document htmlPage2) {
		basicInfo=new Basic(htmlPage2);
	}

	public int getStatus(){
		return status;
	}
	
	public Basic getBasicInfo(){
		return basicInfo;
	}
	
	public List<Basic> getConcurrent(){
		return concurrent;
	}
	
	public static void main(String[] args){
		Parse test=new Parse("苹果");
		System.out.println(test);
	}
}


class ConList{
	HashMap<String, String> word2URL=new HashMap<>();
	public ConList(Document htmlPage) {
		Elements lists=htmlPage.select("ul[class=custom_dot  para-list list-paddingleft-1]").first().select("a[target=_blank]");
		Element list;
		String name,url;
		for(int i=0;i<lists.size();i++){
			list=lists.get(i);
			name=list.text();
			url=Claw.BASEBAIKEURL+list.attr("href");
			word2URL.put(name, url);
		}
	}
	public HashMap<String, String> getWordURL(){
		return word2URL;
	}
}

class ConPoly{
	HashMap<String, String> word2URL=new HashMap<>();
	public ConPoly(Document htmlPage){
		Elements lists=htmlPage.select("ul[class=polysemantList-wrapper cmn-clearfix]").first().select("a");
		Element list;
		String name,url;
		for(int i=0;i<lists.size();i++){
			list=lists.get(0);
			name=lists.attr("title");
			url=Claw.BASEBAIKEURL+list.attr("href");
			word2URL.put(name, url);
		}
	}
	public HashMap<String, String> getWordURL(){
		return word2URL;
	}
}


class conCurrentClaw implements Runnable {
	Document htmlPage;
	String url;
	Basic tempBasic;
	List<Basic> result=new ArrayList<>();
	public conCurrentClaw(List<Basic> concurrent, String value) {
				url=value;
		result=concurrent;
	}

	@Override
	public void run() {
		htmlPage=new Claw(url).getHtmlPage();
		tempBasic=new Basic(htmlPage);
		synchronized (result) {
			result.add(tempBasic);
		}
	}
}