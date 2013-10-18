var nodeData;
var expTitle;
var expText;
var expContainer;
var workflowChart;

function start() {
	expTitle = d3.select("#explanationTitle");
	expText = d3.select("#explanationText");
	expContainer = d3.select("#explanation");
	d3.xml("SingleCall.svg", "image/svg+xml", function(xml) {
		workflowChart = document.importNode(xml.documentElement, true);
    	d3.select("#viz").node().appendChild(workflowChart);
    	workflowChart = d3.select(workflowChart);
		d3.json("data.json", parseData);
	});
}

function parseData( err, data ) {
	nodeData = data;
	_.keys(nodeData).forEach(
			function(k){ var name = "#"+k;
				workflowChart.select(name).
										  on( "click",     function(){ click(this,k);   })
										 .on( "mouseover", function(){ highlight(this); } )
										 .on( "mouseout",  function(){ unHighlight(this); } )
										 .style("cursor","pointer")
										 ;
					   }
	  						);
}

function click( node, nodeId ) {
	var info = nodeData[nodeId];
	expTitle.text(info.title);
	expText.text(info.text);
	expContainer.transition().
			duration(1000).
			style("background-color","#FF0").
			transition().style("background-color","#DDD").
			delay(1000);

}

function highlight( n ) {
	d3.select( n ).
	   transition().
	   style("stroke-width", 4);
}

function unHighlight( n ) {
	d3.select( n ).
	   transition().
	   style("stroke-width", 1);
}