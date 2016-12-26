package tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jsoup.nodes.Document;

import baikeClaw.Basic;
import baikeClaw.Claw;

public class ParallelClaw{
	private static ExecutorService executorService = Executors.newFixedThreadPool(10);
	
	public static List<Basic> getReturnBasic(List<Basic> basics, Map<String, String> urls) {
		for(Entry<String, String> url:urls.entrySet()){
			executorService.execute(new clawBasic(basics, url.getValue()));
		}
		executorService.shutdown();
		try {
            executorService.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException ex) {
            System.out.println(ex.getMessage());
        }
		return basics;
	}
}

class clawBasic implements Runnable {
	Document htmlPage;
	String url;
	Basic tempBasic;
	List<Basic> result=new ArrayList<>();
	public clawBasic(List<Basic> concurrent, String value) {
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
