package emea.fe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.ConnectionFactory;

import emea.fe.services.SentimentAnalysis;
import emea.fe.services.TwitterProvider;
import emea.fe.services.impl.BasicSentimentAnalysis;
import emea.fe.services.impl.RabbitTwitterProvider;


@Configuration
@ComponentScan("emea.fe")
@PropertySource("classpath:dashboard.properties")
public class TwitterDashBoardConfiguration {

	@Value("${xd.server.ip}")
	private String XdServerIP;
	@Value("${xd.server.port}") 
	private Integer XdServerPort;
	@Value("${rabbit.server.uri}") 
	private String rabbitServerURI;
	
	/*
	 * TODO configure the xd from vcap if possible
	 */
	@Bean 
	public TwitterProvider twitterProvider(){
		RabbitTwitterProvider rabbitTwitterProvider = new RabbitTwitterProvider();
		rabbitTwitterProvider.setXdServerIP(XdServerIP);
		rabbitTwitterProvider.setXdServerPort(XdServerPort);		
		return rabbitTwitterProvider;
	}
	
	/*
	 * Configure factory from properties or from vcap if deployed/binded 
	 * into PCF
	 */
	@Bean
	public ConnectionFactory rabbitConnectionFactory() throws Exception{
		ConnectionFactory rabbitConnectionFactory = new ConnectionFactory();
		String vcapEnv = System.getenv("VCAP_SERVICES");
		if (vcapEnv != null){
			Map vcapObject = new ObjectMapper().readValue(
						System.getenv("VCAP_SERVICES"), HashMap.class);
			String vcapRabbitServerUri = (String) ((List)((Map)((Map) ((Map) ((List) vcapObject
					.get("p-rabbitmq")).get(0)).get("credentials"))).get("uris")).get(0);
			rabbitConnectionFactory.setUri(vcapRabbitServerUri);
		} else {
			rabbitConnectionFactory.setUri(rabbitServerURI);
		}
		return rabbitConnectionFactory;
	}
	
	@Bean 
	public SentimentAnalysis<JSONObject> sentimentAnalysis(){
		return new BasicSentimentAnalysis();
	}
	
}
