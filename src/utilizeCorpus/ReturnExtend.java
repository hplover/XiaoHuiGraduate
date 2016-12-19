package utilizeCorpus;
import java.util.LinkedHashMap;
import java.util.Map;
/*
 * the return class of FeatureWords
 */


public class ReturnExtend{
	String title;
	Map<String, Double> tfidfWords=new LinkedHashMap<>();
	Map<String, Double> labelWords=new LinkedHashMap<>();
	Map<String, Double> illusionWords=new LinkedHashMap<>();
	
	public ReturnExtend(){
		
	}
	
	public Map<String, Double> getTfidfWords() {
		return tfidfWords;
	}

	public void setTfidfWords(Map<String, Double> tfidfWords) {
		this.tfidfWords = tfidfWords;
	}

	public Map<String, Double> getIllusionWords() {
		return illusionWords;
	}

	public void setIllusionWords(Map<String, Double> illusionWords) {
		this.illusionWords = illusionWords;
	}

	public Map<String, Double> getLabelWords() {
		return labelWords;
	}

	public void setLabelWords(Map<String, Double> labelWords) {
		this.labelWords = labelWords;
	}

	@Override
	public String toString(){
		return "\ntitle:"+title+"\nfeatureWords:"+tfidfWords+"\nlabelWords:"+labelWords+"\nillusionWords:"+illusionWords+"\n";
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
}