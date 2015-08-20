package emea.fe.services.impl;

import java.io.IOException;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import emea.fe.DashBoardController;
import emea.fe.services.SentimentAnalysis;
import emea.fe.services.TwitterDashboardException;
import emea.fe.services.TwitterProvider;

public class RabbitTwitterProvider implements TwitterProvider {

	private static final Logger logger = LoggerFactory.getLogger(RabbitTwitterProvider.class);
	
	@Autowired
	private SentimentAnalysis<JSONObject> sentimentAnalysis;
	@Autowired
	private ConnectionFactory rabbitConnectionFactory;
	
	private QueueingConsumer consumer;
	private Channel channel;
	private String xdServerIP;
	private Integer xdServerPort;
	
	private static final String JSON_SENTIMENT_POSITIVE_FIELD = "sentimentPositive";
	private final static String QUEUE_NAME = "twitterqueue";
	
	public RabbitTwitterProvider() {}
	
	@PostConstruct
	public void postConstruct() throws Exception {
	  connect();
	}
	
	private void connect() throws TwitterDashboardException{	
		Connection connection;
		try {
			connection = rabbitConnectionFactory.newConnection();
			channel = connection.createChannel();
			channel.queueDeclare(QUEUE_NAME, false, false, false, null);
			consumer = new QueueingConsumer(channel);	
			channel.basicConsume(QUEUE_NAME, true, consumer);
		} catch (IOException e) {
			throw new TwitterDashboardException(e);
		}
			
	}
	
	@Override
	public String fetchTweets(){
		JSONArray tweetsJSON = new JSONArray();
		
		QueueingConsumer.Delivery delivery;
		int max = 0;
	
		if (consumer == null){
			return "{}";//temp
		}
		
		try {
			while (max < 10 && (delivery = consumer.nextDelivery(1000)) != null){
					
					String message = new String(delivery.getBody()) ;
					handleSpecialChar(message);
					JSONObject jsonObject = new JSONObject(message);
					
					//enrich with sentiment
					boolean sentiment = sentimentAnalysis.getSentiment(jsonObject);
					jsonObject.put(JSON_SENTIMENT_POSITIVE_FIELD, sentiment);
					tweetsJSON.put(jsonObject);
					
					System.out.println(" [x] Received '" + message + "'");
					max++;
			}
		} catch (Exception e) {
			throw new TwitterDashboardException(e);
		}
		System.out.println(" [x] tweets number retrieved from rabbit '" + max + "'");			
		return tweetsJSON.toString();
	}
	
	private void handleSpecialChar(String message){
		message.replaceAll("à", "a");
		message.replaceAll("é", "e");
		message.replaceAll("è", "e");
	}
	
	@Override
	public void defineTweetRequest(String wordsToTrack){
		logger.info("#Define new words to track : " + wordsToTrack );
		
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.delete("http://" + xdServerIP  + ":" + xdServerPort + "/streams/definitions");

		
		restTemplate = new RestTemplate();
	
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
		
		MultiValueMap<String, String> mvm = new LinkedMultiValueMap<String, String>();
		mvm.add("name", "twitterstream");
	
		String username = " --username='" + rabbitConnectionFactory.getUsername() + "' ";
		String password = " --password='" + rabbitConnectionFactory.getPassword() + "' ";
		String address = "--addresses=" + rabbitConnectionFactory.getHost() + " ";
		String queue = "--routingKey='\"" + QUEUE_NAME + "\"' ";
		String vhost= "--vhost=" + rabbitConnectionFactory.getVirtualHost()   + " " ;
		
		String definition =
				"twittersearch --consumerKey=afes2uqo6JAuFljdJFhqA --consumerSecret=0top8crpmd1MXGEbbgzAwVJSAODMcbeAbhwHXLnsg"
				+ " --query='"+ wordsToTrack+ "' "
				+ "| rabbit " + username + password + address +  queue + vhost;
		
		System.out.println(definition);
		
		mvm.add("definition", definition);
		
		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(mvm, requestHeaders);

		ResponseEntity<String> res = restTemplate.exchange("http://" + xdServerIP  + ":" + xdServerPort + "/streams/definitions.json", HttpMethod.POST, requestEntity, String.class);
		
		logger.info("#Stream created : " +  res.getBody());
		
		try {
			channel.queuePurge(QUEUE_NAME);
			consumer = new QueueingConsumer(channel);	
			channel.basicConsume(QUEUE_NAME, true, consumer);
		} catch (IOException e) {
			throw new TwitterDashboardException(e);
		}
	}
	
	public String getXdServerIP() {
		return xdServerIP;
	}

	public void setXdServerIP(String xdServerIP) {
		this.xdServerIP = xdServerIP;
	}

	public Integer getXdServerPort() {
		return xdServerPort;
	}

	public void setXdServerPort(Integer xdServerPort) {
		this.xdServerPort = xdServerPort;
	}
	
	public void setRabbitConnectionFactory(ConnectionFactory rabbitConnectionFactory) {
		this.rabbitConnectionFactory = rabbitConnectionFactory;
	}
	
}
