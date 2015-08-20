package emea.fe.services.impl;

import org.json.JSONObject;

import emea.fe.services.SentimentAnalysis;

public class BasicSentimentAnalysis implements SentimentAnalysis<JSONObject>{

	//TODO temp implementation, should be improved with
	//more complex algorithm (third party library or Dieter Flick
	//PCF service)
	@Override
	public boolean getSentiment(JSONObject tweetObject) {
		String text = tweetObject.getString("text");
		if (text.toLowerCase().contains("is") 
				|| text.toLowerCase().contains("n't") 
				|| text.toLowerCase().contains("not")){
			return false;
		}
		return true;
	}

}
