package utilizeCorpus;

import java.util.*;
import baikeClaw.Basic;
import baikeClaw.ProcessItem;
import tools.TFIDF;

public class FeatureWords {
	private ProcessItem returnPage;
	private int status;
	private List<String> basicFeatureWords=new ArrayList<>();
	private List<ReturnExtend> polyFeaturenWords=new ArrayList<>();
	public FeatureWords(String searchWord) {
		returnPage=new ProcessItem(searchWord);
		status=returnPage.getStatus();
		switch (status) {
		case -1:
		case 0:
			System.out.printf("failed to claw %s",searchWord);
			break;
		case 1:
		case 2:
			returnPage.getBasicInfo();
			break;
		case 3:
		case 4:
			List<Basic> conList = returnPage.getConcurrent();
			polyFeaturenWords=new TFIDF(conList).gettfidfFW();
			break;
		default:
			break;
		}
	}
	public List<ReturnExtend> getPolyFeaturenWords() {
		return polyFeaturenWords;
	}
	public List<String> getBasicFeatureWords() {
		return basicFeatureWords;
	}
	
	public static void main(String[] args){
		FeatureWords test=new FeatureWords("苹果公司");
		System.out.println(test.getPolyFeaturenWords());
	}
	
}
