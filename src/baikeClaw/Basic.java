package baikeClaw;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
/*
 * extract info from a basicInfo page
 */
public class Basic{
	private List<String> labels=new ArrayList<>();
	private int editCount;
	private Date lastEdit;
	private String title;
	private HashMap<String, String> innerLink=new HashMap<>();
	private String descriptText;
	private HashMap<String, String> referMaterial=new HashMap<>();
	
	public Basic(){
		
	}
	public Basic(Document htmlpage){
		setTitle(htmlpage);
		setLabels(htmlpage);
		setEditCount(htmlpage);
		setInnerLink(htmlpage);
		setDescriptText(htmlpage);
//		setReferMaterial(htmlpage);
		setLastEdit(htmlpage);
	}

	public List<String> getLabels() {
		return labels;
	}

	private void setLabels(Document htmlpage) {
		Elements labelSelect=htmlpage.select("span[class=taglist]");
		if(labelSelect!=null)
		for(int i=0;i<labelSelect.size();i++){
			labels.add(labelSelect.get(i).text());
		}
	}

	public void setLabels(List<String> labels) {
		this.labels = labels;
	}

	public void setEditCount(int editCount) {
		this.editCount = editCount;
	}

	public void setLastEdit(Date lastEdit) {
		this.lastEdit = lastEdit;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setInnerLink(HashMap<String, String> innerLink) {
		this.innerLink = innerLink;
	}

	public void setDescriptText(String descriptText) {
		this.descriptText = descriptText;
	}

	public void setReferMaterial(HashMap<String, String> referMaterial) {
		this.referMaterial = referMaterial;
	}

	public int getEditCount() {
		return editCount;
	}

	private void setEditCount(Document htmlpage) {
		Elements editCountSelect=htmlpage.select("dd.description > ul > li");
		String textInList;
		if(editCountSelect!=null)
		for(int i=0;i<editCountSelect.size();i++){
			textInList=editCountSelect.get(i).text();
			if(textInList.startsWith("编辑次数")){
				editCount=Integer.parseInt(textInList.substring(5, textInList.indexOf("次", 5)));
				return ;
			}
		}
	}

	public Date getLastEdit() {
		return lastEdit;
	}

	public void setLastEdit(Document htmlpage) {
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-mm-dd");
		Element lastEditSelect=htmlpage.select(HTMLCode.elementLastEdit).first();
		try {
			if(lastEditSelect!=null)
				lastEdit=dateFormat.parse(lastEditSelect.text());
		} catch (ParseException e) {
			System.out.println("fetch last edit time failed");
		}
	}
	
	public HashMap<String, String> getInnerLink() {
		return innerLink;
	}

	private void setInnerLink(Document htmlpage) {
		Elements innerLinkSelect=htmlpage.select("div[class=para]").select("div[label-module=para]").select("a[target=_blank]").select("a[href]");
		String link,name;
		for(int i=0;i<innerLinkSelect.size();i++){
			link=innerLinkSelect.get(i).attr("href");
			if(link!=null&&(link.matches("^/view/.*htm")||link.contains("/item"))){
				name=innerLinkSelect.get(i).text();
				link=Claw.BASEBAIKEURL+link;
				name=name.replaceAll("\\.", "");//保证key正确
				if(name.startsWith("$")){
					name=name.replaceAll("\\$", "");
				}
				innerLink.put(name, link);
			}
		}
	}

	public String getDescriptText() {
		return descriptText;
	}

	private void setDescriptText(Document htmlpage) {
		Elements descriptTextSelect=htmlpage.select("div[class=para]").select("div[label-module=para]");
		descriptText="";
		for(int i=0;i<descriptTextSelect.size();i++){
			descriptText=descriptText+" "+descriptTextSelect.get(i).text();
		}
	}

	public HashMap<String, String> getReferMaterial() {
		return referMaterial;
	}

	
	public String getTitle() {
		return title;
	}

	public void setTitle(Document htmlpage) {
		title=htmlpage.title();
		title=title.substring(0, title.indexOf("_"));
	}

	@Override
	public String toString(){
		return "\ntitle:"+title+"\nlabels:"+labels+"\nedit Count:"+editCount+"\nlast date:"+lastEdit+"\nrefer material:"+referMaterial+"\ndescript text:"+descriptText+"\ninner link:"+innerLink;
	}

	public Object getFeatureWords() {
		// TODO Auto-generated method stub
		return null;
	}	
}