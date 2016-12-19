package utilizeCorpus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import baikeClaw.Basic;
import baikeClaw.ProcessItem;
import tools.ParallelClaw;
import tools.SortMapByValue;

/*
 * input the search word and return their labels
 */

public class LabelFW {
	private Basic returnBasic;
	private List<Basic> innerBasic;
	private HashMap<String, String> innerLinks;
	private List<ReturnExtend> labelResult;
	public LabelFW(String search){
		returnBasic=new ProcessItem(search).getBasicInfo();
		innerLinks=returnBasic.getInnerLink();
		List<String> basicLabels=returnBasic.getLabels();
		Map<String, Double> labelDouble=new HashMap<>();
		ReturnExtend returnExtend=new ReturnExtend();
		if(innerLinks==null&&basicLabels!=null){//没有内链，且存在标签
			for(String label:basicLabels){
				labelDouble.put(label, 1.0);
			}
			returnExtend.setLabelWords(labelDouble);
			labelResult.add(returnExtend);
		}
		else if(innerLinks!=null){//有内链
			innerBasic=ParallelClaw.getReturnBasic(innerBasic, innerLinks);
			innerBasic.add(returnBasic);
			List<String> innerLabels;
			for(Basic basic:innerBasic){
				innerLabels=basic.getLabels();
				if(innerLabels!=null){
					for(String label:innerLabels)
					if(labelDouble.containsKey(label)){
						labelDouble.put(label, labelDouble.get(label)+1);
					}
					else labelDouble.put(label, 1.0);
				}
			}
			labelDouble=SortMapByValue.sortByComparator(labelDouble, false);
			returnExtend.setLabelWords(labelDouble);
		}
	}
	public List<ReturnExtend> getLabelResult() {
		return labelResult;
	}
}
