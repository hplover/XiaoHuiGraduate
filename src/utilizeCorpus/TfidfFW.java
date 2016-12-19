package utilizeCorpus;

import java.util.ArrayList;
import java.util.List;

import baikeClaw.Basic;
import baikeClaw.ProcessItem;
import tools.DataBaseOP;
import tools.TFIDF;

/*
 * input search word return its feature words based on TFIDF
 */

public class TfidfFW {
	private String searchWord;
	private Basic returnBasic;
	private List<Basic> randomBasic=new ArrayList<>();
	private List<ReturnExtend> tfidfResult=new ArrayList<>();
	private static final int count=10;
	public TfidfFW(String searchWord){
		this.searchWord=searchWord;
		returnBasic=new ProcessItem(searchWord).getBasicInfo();		
		randomBasic=DataBaseOP.randomBasic(count);
		randomBasic.add(returnBasic);
		setTfidfResult(new TFIDF(randomBasic).gettfidfFW());
	}
	public String getSearchWord() {
		return searchWord;
	}
	public List<Basic> getRandomBasic() {
		return randomBasic;
	}
	public List<ReturnExtend> getTfidfResult() {
		return tfidfResult;
	}
	private void setTfidfResult(List<ReturnExtend> tfidfResult) {
		this.tfidfResult = tfidfResult;
	}
	public static void main(String[] args){
		TfidfFW tt=new TfidfFW("苹果公司");
		System.out.println(tt.getTfidfResult());
	}
}
