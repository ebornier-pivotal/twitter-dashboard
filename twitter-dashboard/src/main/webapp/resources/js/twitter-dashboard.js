var tweets;

function addTweet(position) {
	
	if (tweets.length < 50){
		console.log("#call to fetch data");
		$.ajax({
		    url : '/tweets',
			//url : 'http://localhost:8080/twitter/tweets',
		    async: true,
		    success : function(data) {
		    console.log("#during call to fetch data : " + data.length);
		    	
		    var i = 0;
		    while (i < data.length){
		       tweets.push(data[i]);	
		       i++;
		    }
		    
		    //print 
			console.log("#after call to fetch data : " + tweets.length);
			
			var tweet = tweets.shift();
			
			//if no tweet were fetched
			if (tweet === undefined){
				console.log("# no tweet were fetched");
				return;
			}
			
			var y;
			
			var tweet9 = $("#twitter-stream li").last();
			if (tweet9 !== undefined && tweet9.css("top") !== undefined){
				var tweet9_y = parseInt(tweet9.css("top")
						.replace("px", ""));
				y = tweet9_y - 120;
				if (y > 0){y =0};
				
			} else {
				y = 0; //position * 120
			}
			
			var textSplit = tweet.text.split(" ");
			
			//target style
			var indexSplit = 0;
			var targets = new Array();
			while (indexSplit < textSplit.length){
				if (textSplit[indexSplit].indexOf("@") != -1){
					var wrap = '<a href=""'
						+ ' style="font-weight: lighter;" target="_blank">' + textSplit[indexSplit] + '</a>'
					tweet.text = tweet.text.replace(textSplit[indexSplit], wrap);
					targets.push(textSplit[indexSplit]);
					
				}
				indexSplit++;
			}
			
			var tweetHtml = '<li style="left:-300px; top: '
					+ y
					+ 'px;" class="twitter-stream-tweet"><div'
					+ ' class="twitter-stream-content slideDown">'
					+ ' <div class="twitter-stream-item-header">'
					+ '	<a class="twitter-stream-link"'
					+ '	href="http://www.twitter.com/shuksrpcol"><img'
					+ ' src="' +  tweet.user.profile_background_image_url + '"'
					+ ' class="twitter-stream-avatar"><span class="left"><span'
					+ ' class="twitter-stream-fullname">' +  tweet.user.screen_name  + '</span>'
					+ ' </div>'
					+ ' <div class="twitter-stream-tweet-text">'
					+ tweet.text
					+ ' </div>' + ' </div></li>';

			
			
			$("#twitter-stream").append(tweetHtml);

			if (tweet.sentimentPositive == false){
				$("#twitter-stream li").last().css("background-color", "#FE2E2E");
			}
		    
		    }
		});
	}

	
}

function move(srcIndex) {

	var atLeastOne = false;
	
	$("#twitter-stream li").each(function (){
		atLeastOne = true;
		var src = $(this);
		if (src.css("top") !== undefined){
			var currentY = parseInt(src.css("top")
					.replace("px", ""));
			var newY = currentY + 20;
				
			if (newY < 1000){
				 src.css("top", newY + "px");
			} else {
				src.remove();
				addTweet(0); 
			}		
	}});
	
	if (!atLeastOne){
		console.log("#atleastone action");
		addTweet(0); 
	}
	
};


function initTweetFlow(){
	console.log("init tweet flow");
	tweets = new Array();
							
	setInterval(function() {
		console.log("refresh");
		var i = 0;
		while (i <= 10){
			 move (i);
		i++;
	 }
				    		 
	}, 200	);
	
}


$(document)
		.ready(
				function() {
				
			initTweetFlow();
					
			$("#wordsToTrackAction").click(function(){
				$.ajax({
				    url : '/defineTweetRequest',
				    async: true,
				    method: "PUT",
				    data : $("#wordsToTrackInput").val(),
				    success : function(data) {
				    	tweets = new Array(); //temp
				    	//initTweetFlow();	
					    }
				});
			})
});