package emea.fe;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import emea.fe.services.TwitterProvider;

/**
 * Handles requests for the application home page.
 * @author ebornier
 */
@Controller
public class DashBoardController {
	
	private static final Logger logger = LoggerFactory.getLogger(DashBoardController.class);
	@Autowired
	private TwitterProvider twitterProvider;
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		return "home.jsp";
	}
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/tweets", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE
			)
	public @ResponseBody String getTweets(Locale locale, Model model) {
		logger.info("#getTweets");
		return twitterProvider.fetchTweets();
	}
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/defineTweetRequest", method = RequestMethod.PUT
			)
	public @ResponseBody Boolean defineTweetRequest(Locale locale, @RequestBody String wordsToTrack)  {
		twitterProvider.defineTweetRequest(wordsToTrack);
		return true;
	}
	


}
