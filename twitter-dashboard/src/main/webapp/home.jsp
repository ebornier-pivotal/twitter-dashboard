<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
<title>Pivotal Twitter DashBoard</title>

<script type="text/javascript"  src="resources/js/jquery-2.1.3.js" ></script>

<script type="text/javascript"  src="resources/js/twitter-dashboard.js" ></script>

<script src="resources/js/d3.v3.min.js"></script>
<script src="resources/js/fvc.js"></script>
<script src="resources/js/aggcount.js"></script>
<script src="resources/js/bootstrap.js"></script>

<link href="resources/css/styles.css" rel="stylesheet"></link>
<link href="resources/css/streams.css" rel="stylesheet"></link>


<style type="text/css">

body {
  font: 12px sans-serif;
  text-rendering: optimizelegibility;
}

.arc path {
    stroke: #fff;
}

.aggbar {
  margin-left: 10px;
  font: 10px sans-serif;
  shape-rendering: crispEdges;
}

.aggbar rect {
  stroke: white;
  fill: steelblue;
}

.aggbar text.bar {
  fill: white;
}

</style>



</head>



<body id="streams" class="subpage subpage-stream">

<div class="pivotal-logo">
<a href="/"></a>


</div>
	<div class="section white padding-extra">
		<div style="height: 1000px;" id="visualization-container"
			class="container">
			<div class="col-sm-6 col-sm-offset-3">
				<div id="visualization">
					<ol id="twitter-stream">
						
					</ol>
				</div>
			</div>
		</div>
	
	
	 <div style="position:absolute;top:20px;right:350px;" >
 
 	   <p ><b style="color:white	">Words to track: </b><br><input id="wordsToTrackInput" ></input > <button id="wordsToTrackAction"> Go!</button></p> 
      
</div>
			
	</div>


</body>
</html>