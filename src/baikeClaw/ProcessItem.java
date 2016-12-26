package baikeClaw;
/*
 * get all the basicInfo if the status means the search word is polysemy by concurrent threads
 * and write to MongoDB
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import tools.DataBaseOP;
import tools.ParallelClaw;

public class ProcessItem {
	private Claw claw;
	private Document htmlPage;
	private int status;
	private Basic basicInfo;
	private List<Basic> concurrent=new ArrayList<>();
	public ProcessItem(String searchWord){
		List<String> searchResult=DataBaseOP.getIndex(searchWord);
		switch (searchResult.size()) {
		case 0:
			onlineSearch(searchWord);
			break;
		case 1:
			basicInfo=DataBaseOP.getBasic(searchResult.get(0));
			status=1;
			break;
		default:
			concurrent=DataBaseOP.getPoly(searchResult);
			status=3;
			break;
		}
	}
	
	private void onlineSearch(String searchWord){
		claw=new Claw(searchWord);
		htmlPage=claw.getHtmlPage();
		status=claw.getStatus();
//		System.out.println("status"+status);
		switch (status) {
		case -1:
		case 0:
			System.out.printf("failed to get %s",searchWord);
			break;
		case 1:
		case 2:
			generateBasicInfo(htmlPage);
			DataBaseOP.addBasic(basicInfo);
			DataBaseOP.addIndex(searchWord,basicInfo.getTitle());
			break;
		case 3:
			generatePoly(htmlPage);
			DataBaseOP.addPoly(concurrent);
			for(Basic basic:concurrent){
				DataBaseOP.addIndex(searchWord, basic.getTitle());
			}
			break;
		case 4:
			generateList(htmlPage);
			DataBaseOP.addPoly(concurrent);
			for(Basic basic:concurrent){
				DataBaseOP.addIndex(searchWord, basic.getTitle());
			}
			break;
		default:
			break;
		}
	}
	
	public String toString(){
		if(status==1||status==2){
			return basicInfo.getTitle();
		}
		if(status==3||status==4){
			String result="";
			for(Basic basic:concurrent){
				result=result+basic.getTitle()+" ";
			}
			return result;
		}
		return "";
	}
	
	private void generateList(Document htmlPage2) {
		ConList conList=new ConList(htmlPage2);
		HashMap<String, String> word2URL=conList.getWordURL();
		concurrent=ParallelClaw.getReturnBasic(concurrent, word2URL);
	}

	private void generatePoly(Document htmlPage2) {
		Basic defaultBasicInfo=new Basic(htmlPage2);
		concurrent.add(defaultBasicInfo);
		ConPoly conPoly=new ConPoly(htmlPage2);
		HashMap<String, String> word2URL=conPoly.getWordURL();
		concurrent=ParallelClaw.getReturnBasic(concurrent, word2URL);
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
//		try(BufferedReader br = new BufferedReader(new FileReader("file.txt"))) {
//		    StringBuilder sb = new StringBuilder();
//		    String line = br.readLine();
//
//		    while (line != null) {
//		        sb.append(line);
//		        sb.append(System.lineSeparator());
//		        line = br.readLine();
//		    }
//		    String search = sb.toString();
//		    System.out.println(new ProcessItem(search));
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		ProcessItem test=new ProcessItem("国奥");
		System.out.println(test);
	}
}


class ConList{
	HashMap<String, String> word2URL=new HashMap<>();
	public ConList(Document htmlPage) {
		Elements lists=htmlPage.select(HTMLCode.elementList).first().select("a[target=_blank]");
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
		Elements lists=htmlPage.select(HTMLCode.elementPoly);
		Element list;
		String name,url;
		for(int i=0;i<lists.size();i++){
			list=lists.get(i);
			name=list.attr("title");
			url=Claw.BASEBAIKEURL+list.attr("href");
			word2URL.put(name, url);
		}
	}
	public HashMap<String, String> getWordURL(){
		return word2URL;
	}
}

