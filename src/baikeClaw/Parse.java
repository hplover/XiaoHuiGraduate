package baikeClaw;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Parse {
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
	}	
	
	private void generateList(Document htmlPage2) {
		ConList conList=new ConList(htmlPage2);
		HashMap<String, String> word2URL=conList.getWordURL();
		Basic basicInfoList;
		for(Entry<String, String> list:word2URL.entrySet()){
			basicInfoList=new Basic(new Claw(list.getValue()).getHtmlPage());
			concurrent.add(basicInfoList);
		}

		System.out.println(concurrent);
	}

	private void generatePoly(Document htmlPage2) {
		Basic defaultBasicInfo=new Basic(htmlPage2);
		concurrent.add(defaultBasicInfo);
		ConPoly conPoly=new ConPoly(htmlPage2);
		HashMap<String, String> word2URL=conPoly.getWordURL();
		Basic otherBasicInfo;
		for(Entry<String,String> list:word2URL.entrySet()){
			otherBasicInfo=new Basic(new Claw(list.getValue()).getHtmlPage());
			concurrent.add(otherBasicInfo);
		}
		System.out.println(concurrent);
	}
	
	private void generateBasicInfo(Document htmlPage2) {
		basicInfo=new Basic(htmlPage2);
		System.out.println(basicInfo);
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
//		System.out.println(test);
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