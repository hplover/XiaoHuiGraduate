package tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.lang.Math;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import baikeClaw.Basic;

public class TFIDF {
	List<Basic> polyList=new ArrayList<>();
	Map<String,Map<String, Double>> tfidfFW=new HashMap<>();
	public TFIDF() {
		
	}

	public TFIDF(List<Basic> conList) {
		if(conList!=null&&conList.size()>0){
			for(Basic article:conList){
//				if(tfidfFW.containsKey(article.getTitle())){
//					continue;
//				}
				tfidfFW.put(article.getTitle(), segWords(article.getDescriptText()));
			}
			Map<String, Double> featrueWordTemp=new HashMap<>();
			String title;
			Double idf=0.0;
			for(Entry<String, Map<String, Double>> article:tfidfFW.entrySet()){
				featrueWordTemp=article.getValue();
				title=article.getKey();
				for(Entry<String, Double> word:featrueWordTemp.entrySet()){
					for(Entry<String, Map<String, Double>> bticle:tfidfFW.entrySet()){
						if(title!=bticle.getKey()){
							if(bticle.getValue().containsKey(word.getValue())){
								idf++;
							}
						}
					}
					featrueWordTemp.put(word.getKey(), word.getValue()*Math.log((tfidfFW.size()/(idf+1))));
					idf=0.0;
				}
				featrueWordTemp=SortMapByValue.sortByComparator(featrueWordTemp, false);
//				tfidfFW.remove(article.getKey());
				tfidfFW.put(article.getKey(), featrueWordTemp);
				
			}
		}
		
	}

	public Map<String, Map<String, Double>> gettfidfFW() {
		return tfidfFW;
	}
	
	private HashMap<String, Double> segWords(String text){
		if(text==null||text.length()==0){
			return null;
		}
		Segment segment = HanLP.newSegment().enableAllNamedEntityRecognize(true);
		List<Term> rawSegWords=segment.seg(text);
		HashMap<String,Double> segWords=new HashMap<>();
		int wordCount=0;
		String wordName;
		for(Term term:rawSegWords){
			if(term.nature.startsWith('n')&&!term.nature.toString().equals("nz")&&term.word.length()>1){
				wordCount++;
				wordName=term.word;
				wordName=wordName.replaceAll("\\.", "");
				if(wordName.startsWith("$")){
					wordName=wordName.replaceAll("\\$", "");
				}
				if(!segWords.containsKey(wordName)){
					segWords.put(wordName, (double) 1);
				}
				else{
					segWords.put(wordName, segWords.get(wordName)+1);
				}
			}
				
		}
		for(Entry<String, Double> word:segWords.entrySet()){
			segWords.put(word.getKey(), word.getValue()/wordCount);
		}
		return segWords;
	}	
}

