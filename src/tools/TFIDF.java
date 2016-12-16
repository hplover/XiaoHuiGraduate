package tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.lang.Math;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.NLPTokenizer;

import baikeClaw.Basic;
import utilizeCorpus.PolyFW;

public class TFIDF {
	List<Basic> polyList=new ArrayList<>();
	List<PolyFW> polyFeatureWords=new ArrayList<>();
//	Set<List<String>> polyDescSegWords=new HashSet<>();
	public TFIDF() {
		
	}

	public TFIDF(List<Basic> conList) {
		if(conList!=null&&conList.size()>0){
			for(Basic article:conList){
				polyFeatureWords.add(new PolyFW(article.getTitle(), segWords(article.getDescriptText())));
			}
		}
		int idf=0,size=polyFeatureWords.size();
		HashMap<String, Double> featrueWordTemp=new HashMap<>();
		for(int i=0;i<size;i++){
			featrueWordTemp=polyFeatureWords.get(i).getFeatrueWords();
			for(Entry<String, Double> word:featrueWordTemp.entrySet()){
				for(int k=0;k<size;k++){
					if(i!=k){
						if(word.getKey().equals("公司"))
							System.out.println("asdfasdf");
						if(polyFeatureWords.get(k).getFeatrueWords().containsKey(word.getKey())){
							idf++;
						}
					}
				}
				featrueWordTemp.put(word.getKey(), word.getValue()*Math.log(size/(idf+1)));
				idf=0;
			}
			System.out.println(featrueWordTemp);
		}
	}

	public List<PolyFW> getpolyFW() {
		return polyFeatureWords;
	}
	
	private HashMap<String, Double> segWords(String text){
		if(text==null||text.length()==0){
			return null;
		}
		List<Term> rawSegWords=NLPTokenizer.segment(text);
		HashMap<String,Double> segWords=new HashMap<>();
		int wordCount=0;
		for(Term term:rawSegWords){
			if(term.nature.startsWith('n')&&term.nature.toString().length()>1&&term.word.length()>1){
				wordCount++;
				if(!segWords.containsKey(term.word)){
					segWords.put(term.word, (double) 1);
				}
				else{
					segWords.put(term.word, segWords.get(term.word)+1);
				}
			}
				
		}
		for(Entry<String, Double> word:segWords.entrySet()){
			segWords.put(word.getKey(), word.getValue()/wordCount);
		}
		System.out.println(segWords);
		return segWords;
	}
	
	
	
	
	
	
	
	
	
}
