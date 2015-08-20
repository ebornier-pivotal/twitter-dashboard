# twitter-dashboard

Nice demo app to demonstrate how we can easiliy build stream application based on Spring XD + RabbitMQ.

The demo app displays a stream of tweets based on a query done by the user. The query is a set of words to track. 

The web app reads from a rabbit queue. Spring XD is responsible for feeding the queue.

The spring-xd stream is dynamically defined/deployed into spring-xd server each time the user fill the "words to track" input. In such way, the user can change the stream definition without to interact explicitly with spring-xd (convenient for demo purpose). 

The spring-xd & rabbit server address are configured in dashboard.properties.

If the app is binded to a rabbitmq service (in Pivotal Cloud Foundry), this instance is used instead of using the one defined in dashboard properties.

Currently, this demo is not able to read vcap to bind the spring-xd server.

The demo works fine on a laptop, but I encountered issues with CF deployment. I have to fix it.

 ![Twitter DashBoard](https://raw.githubusercontent.com/ebornier-pivotal/twitter-dashboard/master/twitter-dashboard.png)
