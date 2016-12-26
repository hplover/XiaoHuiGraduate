package utilizeCorpus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import baikeClaw.Basic;
import baikeClaw.ProcessItem;
import tools.DataBaseOP;
import tools.ParallelClaw;
import tools.SortMapByValue;
import tools.TFIDF;

/*
 * input search word return its feature words based on TFIDF
 */

public class FetchFW {
	private int status;
	private String searchWord, title;
	private Basic returnBasic;
	private List<Basic> polyBasices = new ArrayList<>();
	private List<Basic> randomBasices = new ArrayList<>();
	private Map<String, Map<String, Double>> tfidfResult = new HashMap<>();
	private Map<String, Map<String, Double>> labelResult = new HashMap<>();
	private static final int count = 10;

	public FetchFW(String searchWord) {
		this.searchWord = searchWord;
		List<String> words = DataBaseOP.getIndex(searchWord);
		List<Basic> polyBasices = new ArrayList<>();
		Map<String, Double> tempTfidf = new LinkedHashMap<>();
		Map<String, Double> tempLabel = new LinkedHashMap<>();
		for (String word : words) {
			polyBasices.add(DataBaseOP.getBasic(word));
			tempTfidf = DataBaseOP.getTfidf(word);
			tempLabel = DataBaseOP.getLabel(word);
			if (tempTfidf != null) {
				tfidfResult.put(word, tempTfidf);
			}
			if (tempLabel != null) {
				labelResult.put(word, tempLabel);
			}
		}
		if (tfidfResult.size() == 0) {
			if (polyBasices.size() > 0) {
				Map<String, Map<String, Double>> tempResult = new HashMap<>();
				for (Basic basic : polyBasices) {
					title = basic.getTitle();
					randomBasices = DataBaseOP.randomBasices(count);
					randomBasices.add(basic);
					tempResult = new TFIDF(randomBasices).gettfidfFW();
					tfidfResult.put(title, tempResult.get(title));
				}
				if (tfidfResult.size() != 0) {
					DataBaseOP.addTfidf(tfidfResult);
				}
			} else {
				onlineTfidf(searchWord);
			}
		}
		if (labelResult.size() == 0) {
			if (polyBasices.size() == 0) {
				ProcessItem itemForLabel = new ProcessItem(searchWord);
				switch (itemForLabel.getStatus()) {
				case -1:
				case 0:
					System.out.println("failed to get " + searchWord);
					break;
				case 1:
				case 2:
					polyBasices.add(itemForLabel.getBasicInfo());
					break;
				case 3:
				case 4:
					polyBasices = itemForLabel.getConcurrent();
				default:
					break;
				}
			}
			Map<String, String> innerLink = new HashMap<>();
			List<String> labels = new ArrayList<>();
			for (Basic basic : polyBasices) {
				Map<String, Double> labelDouble = new LinkedHashMap<>();
				title = basic.getTitle();
				innerLink = basic.getInnerLink();
				labels = basic.getLabels();
				if (innerLink == null && labels != null) {// 没有内链，且存在标签
					for (String label : labels) {
						labelDouble.put(label, 1.0);
					}
					labelResult.put(basic.getTitle(), labelDouble);
				} else if (innerLink != null) {// 有内链
					List<Basic> innerBasic = new ArrayList<>();
					innerBasic = ParallelClaw.getReturnBasic(innerBasic, innerLink);
					innerBasic.add(basic);
					List<String> innerLabels;
					for (Basic innerbasic : innerBasic) {
						innerLabels = innerbasic.getLabels();
						if (innerLabels != null) {
							for (String label : innerLabels)
								if (labelDouble.containsKey(label)) {
									labelDouble.put(label, labelDouble.get(label) + 1);
								} else
									labelDouble.put(label, 1.0);
						}
					}
					labelDouble = SortMapByValue.sortByComparator(labelDouble, false);
					labelResult.put(basic.getTitle(), labelDouble);
				}
			}
			if (labelResult.size() > 0) {
				DataBaseOP.addLabel(labelResult);
			}
		}
	}

	private void onlineTfidf(String searchWord) {
		ProcessItem processItem = new ProcessItem(searchWord);
		status = processItem.getStatus();
		switch (status) {
		case -1:
		case 0:
			System.out.printf("failed to get %s", searchWord);
			break;
		case 1:
		case 2:
			returnBasic = processItem.getBasicInfo();
			randomBasices = DataBaseOP.randomBasices(count);
			randomBasices.add(returnBasic);
			title = returnBasic.getTitle();
			tfidfResult.put(title, new TFIDF(randomBasices).gettfidfFW().get(title));
			break;
		case 3:
		case 4:
			polyBasices = processItem.getConcurrent();
			Map<String, Map<String, Double>> tempResult = new HashMap<>();
			for (Basic basic : polyBasices) {
				title = basic.getTitle();
				randomBasices = DataBaseOP.randomBasices(count);
				randomBasices.add(basic);
				tempResult = new TFIDF(randomBasices).gettfidfFW();
				tfidfResult.put(title, tempResult.get(title));
			}
			break;
		default:
			break;
		}
		if (tfidfResult.size() != 0) {
			DataBaseOP.addTfidf(tfidfResult);
		}
	}

	public int getStatus() {
		return status;
	}

	public String getSearchWord() {
		return searchWord;
	}

	public List<Basic> getRandomBasic() {
		return randomBasices;
	}

	public Map<String, Map<String, Double>> getTfidfResult() {
		return tfidfResult;
	}

	public Map<String, Map<String, Double>> getLabelResult() {
		return labelResult;
	}

	public static void main(String[] args) {
		FetchFW tt = new FetchFW("苹果");
		System.out.println(tt.getTfidfResult());
		System.out.println(tt.getLabelResult());
	}
}
