package jwhile.antlr4.cfg.util;

import java.util.LinkedHashMap;
import java.util.Map;

import jwhile.antlr4.cfg.entities.Label;

public class LabelGenerator {
	
	private long currentLabel = 0;
	private Map<String, Long> contextLabel = new LinkedHashMap<String, Long>();
	
	public Label getNextLabel(String context) {
		long labelId = currentLabel;
		currentLabel = currentLabel+1;
		Long contextId = this.contextLabel.get(context);
		if(contextId==null) {
			contextId = 0L;
		}
		String contextStr = context+":"+contextId;
		this.contextLabel.put(context, contextId+1);
		return new Label(labelId, contextStr);
	}

}
