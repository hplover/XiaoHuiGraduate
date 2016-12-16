package baikeClaw;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Claw {
	private int status;// failed(-1),none(0),basicInfo(1), synonym(2), poly(3),list(4)
	private String searchString;
	private Document htmlPage;
	private String userAgent;
	private String url;
	private static List<String> userAgents = new ArrayList<>();
	private static int userAgentLen;

	public final static String BASEBAIKEURL="http://baike.baidu.com";
	public final static String BASESEARCHURL = "http://baike.baidu.com/search/word?word=";
	private static int clawCount = 4;

	// 静态块，初始化客户端信心
	static {
		try (InputStream fis = new FileInputStream("resources/user_agents");
				InputStreamReader fisr = new InputStreamReader(fis, Charset.forName("UTF-8"));
				BufferedReader br = new BufferedReader(fisr);) {
			String line;
			while ((line = br.readLine()) != null) {
				userAgents.add(line);
			}
			userAgentLen = userAgents.size();
		} catch (FileNotFoundException e) {
			System.out.println("user agent not found. use default setting");
		} catch (IOException e) {
			System.out.println("loading useragent failed. use default setting");
		}
	}
	
	public Claw(String searchString) {
		this.searchString=searchString;
		if(!isURL(searchString))
			url = generateURL(searchString);
		else {
			url=searchString;
		}
		// generate user agent
		if (userAgentLen > 0) {
			int index = new Random().nextInt(userAgentLen);
			userAgent = userAgents.get(index);
		}
		
		// generate HTMLPage
		htmlPage = clawHTMLPage();

		// generate Status
		status = clawStatus();
		
		//write to file
		if(status>0)
			writeHtml();
	}
	
	private boolean isURL(String searchString){
	    String str=searchString.replaceAll("^((https|http|ftp|rtsp|mms)?://)[^\\s]+", "");
	    if(str.isEmpty()){
	    	return true;
	    }
		return false;
		
	}
	
	
	private String generateURL(String searchString){
		// generate url
		String encodeSearchWord = searchString;
		try {
			encodeSearchWord = URLEncoder.encode(searchString, "utf-8");
		} catch (UnsupportedEncodingException e) {
			System.out.println("searchWord encode failed.");
			return "";
		}
		return BASESEARCHURL + encodeSearchWord;
	}
	

	private int clawStatus() {
		if (htmlPage == null) {
			return -1;
		}
		String webpage=htmlPage.toString();
		if(webpage.contains(Status.notExist1)||webpage.contains(Status.notExist2)){
			return 0;
		}
		if(webpage.contains(Status.existBody)){
			if(webpage.contains(Status.poly)){
				return 3;
			}
			if(webpage.contains(Status.synonym)){
				return 2;
			}
			if(webpage.contains(Status.list)){
				return 4;
			}
			return 1;
		}
		return 0;
	}

	private Document clawHTMLPage() {
		int i = 0;
		Document webpage = null;
		boolean isYield = true;
		while (isYield) {
			try {
				webpage = Jsoup.connect(url).userAgent(userAgent).get();
				isYield = false;
			} catch (Exception e) {
				i++;
				if (i >= clawCount) {
					System.out.printf("claw %s failed\n", url);
					return null;
				}
			}
		}
		return webpage;
	}

	public boolean writeHtml(){
		try (
				FileWriter fileWriter=new FileWriter("resources/html", true);
				BufferedWriter bufferedWriter=new BufferedWriter(fileWriter);
				PrintWriter out=new PrintWriter(bufferedWriter);
				){
			out.println(htmlPage.toString());
			out.println("\n=========================================================\n\n\n");
			return true;
		} catch (Exception e) {
			System.out.println("html write to file failed");
		}
		return false;
	}
	
	public Document getHtmlPage() {
		return htmlPage;
	}

	
	public String getURL() {
		return url;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public int getStatus() {
		return status;
	}

	public String getSearchWord() {
		return searchString;
	}

	public static void main(String[] args) {
		//水晶:poly
		//啊；力量的十分骄傲了可靠的健身房了；:none
		//朴槿惠：basicinfo
		//国防科大: synonym
		String[] teStrings=new String[6];
		teStrings[0]="asdfjlajsdfljadlkfasdf";
		teStrings[1]="朴槿惠";
		teStrings[2]="国防科大";
		teStrings[3]="国奥";
		teStrings[4]="苹果";
		int i=0;
		while(i<5){
			Claw tt = new Claw(teStrings[i]);
			System.out.println(tt.getSearchWord());
			System.out.println(tt.getURL());
			System.out.println(tt.getUserAgent());
			System.out.println(tt.getStatus());
			i++;
			System.out.println("===================");
		}
	}
}

class Status{
	public static String notExist1="百度百科尚未收录词条";
	public static String notExist2="<p class=\"sorryCont\"><span class=\"sorryTxt\">";
	public static String existBody="<div class=\"main-content\">";
	public static String poly="<ul class=\"polysemantList-wrapper cmn-clearfix\">";
	public static String synonym="<span class=\"view-tip-panel\">";
	public static String list="<ul class=\"custom_dot  para-list list-paddingleft-1\">";
//	public static String 
}

