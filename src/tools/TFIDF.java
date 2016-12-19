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
import utilizeCorpus.ReturnExtend;

public class TFIDF {
	List<Basic> polyList=new ArrayList<>();
	List<ReturnExtend> tfidfFW=new ArrayList<>();
	public TFIDF() {
		
	}

	public TFIDF(List<Basic> conList) {
		ReturnExtend tempFw;
		if(conList!=null&&conList.size()>0){
			for(Basic article:conList){
				tempFw=new ReturnExtend();
				tempFw.setTitle(article.getTitle());
				tempFw.setTfidfWords(segWords(article.getDescriptText()));
				tfidfFW.add(tempFw);
			}
			int idf=0,size=tfidfFW.size();
			Map<String, Double> featrueWordTemp=new HashMap<>();
			for(int i=0;i<size;i++){
				featrueWordTemp=tfidfFW.get(i).getTfidfWords();
				for(Entry<String, Double> word:featrueWordTemp.entrySet()){
					for(int k=0;k<size;k++){
						if(i!=k){
							if(tfidfFW.get(k).getTfidfWords().containsKey(word.getKey())){
								idf++;
							}
						}
					}
					featrueWordTemp.put(word.getKey(), word.getValue()*Math.log(size/(idf+1)));
					idf=0;
				}
				featrueWordTemp=SortMapByValue.sortByComparator(featrueWordTemp, false);
				tfidfFW.get(i).setTfidfWords(featrueWordTemp);
			}
		}
		
	}

	public List<ReturnExtend> gettfidfFW() {
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
		for(Term term:rawSegWords){
			if(term.nature.startsWith('n')&&!term.nature.toString().equals("nz")&&term.word.length()>1){
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
		return segWords;
	}	
}

