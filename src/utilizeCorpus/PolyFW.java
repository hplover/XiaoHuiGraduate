package utilizeCorpus;
import java.util.HashMap;

public class PolyFW{
	String title;
	HashMap<String, Double> featrueWords=new HashMap<>();
	
	public PolyFW(String title, HashMap<String, Double> hashMap){
		setTitle(title);
		setFeatrueWords(hashMap);
	}
	
	@Override
	public String toString(){
		System.out.println(title);
		System.out.println(featrueWords);
		System.out.println("===================");
		return "";
	}



	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public HashMap<String, Double> getFeatrueWords() {
		return featrueWords;
	}
	public void setFeatrueWords(HashMap<String, Double> hashMap) {
		this.featrueWords = hashMap;
	}
	
}