package tools;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;



public class PolyFW{
	String title;
	Map<String, Double> featrueWords=new LinkedHashMap<>();
	
	public PolyFW(String title, HashMap<String, Double> hashMap){
		setTitle(title);
		setFeatrueWords(hashMap);
	}
	
	@Override
	public String toString(){
		return "\ntitle:"+title+"\nfeatureWords:"+featrueWords+"\n";
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Map<String, Double> getFeatrueWords() {
		return featrueWords;
	}
	public void setFeatrueWords(Map<String, Double> featrueWordTemp) {
		this.featrueWords = featrueWordTemp;
	}
	
}