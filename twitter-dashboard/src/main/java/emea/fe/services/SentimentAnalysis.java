package emea.fe.services;

public interface SentimentAnalysis<T> {

	boolean getSentiment(T object);
	
}
