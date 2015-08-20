package emea.fe.services;


public interface TwitterProvider {

	String fetchTweets();

	void defineTweetRequest(String wordsToTracks);
	
}
