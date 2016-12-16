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

public class Basic{
	List<String> labels=new ArrayList<>();
	int editCount;
	Date lastEdit;
	String title;
	HashMap<String, String> innerLink=new HashMap<>();
	String descriptText;
	HashMap<String, String> referMaterial=new HashMap<>();
	
	public Basic(Document htmlpage){
		setTitle(htmlpage);
		setLabels(htmlpage);
		setEditCount(htmlpage);
		setInnerLink(htmlpage);
		setDescriptText(htmlpage);
		setReferMaterial(htmlpage);
		setLastEdit(htmlpage);
	}

	public List<String> getLabels() {
		return labels;
	}

	private void setLabels(Document htmlpage) {
		Elements labelSelect=htmlpage.select("span[class=taglist]");
		for(int i=0;i<labelSelect.size();i++){
			labels.add(labelSelect.get(i).text());
		}
	}

	public int getEditCount() {
		return editCount;
	}

	private void setEditCount(Document htmlpage) {
		Elements editCountSelect=htmlpage.select("dd.description > ul > li");
		String textInList;
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
		Element lastEditSelect=htmlpage.select("span[class=j-modified-time]").first();
		try {
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
			if(link.matches("^/view/.*htm")){
				name=innerLinkSelect.get(i).text();
				link=Claw.BASEBAIKEURL+link;
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

	private void setReferMaterial(Document htmlpage) {
		Elements referMaterialSelect=htmlpage.select("li[class=reference-item ]");
		Element material;
		for(int i=0;i<referMaterialSelect.size();i++){
			material=referMaterialSelect.get(i).select("a[class=text").first();
			String name, link;
			name=material.text();
			link=material.attr("href");
			referMaterial.put(name, link);
		}
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
		System.out.println("title:"+title);
		System.out.println("labels:"+labels);
		System.out.println("edit Count:"+editCount);
		System.out.println("last date:"+lastEdit);
		System.out.println("refer material:"+referMaterial);
		System.out.println("descript text:"+descriptText);
		System.out.println("inner link:"+innerLink);
		System.out.println("================================");
		return descriptText;
	}	
}