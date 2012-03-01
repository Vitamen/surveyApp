var chart;
$(document).ready(function() {
	var i=1;
	var names = new Array();
	while (true)
	{
		var feedNameElement = document.getElementById("feed_name_"+i);
		if (!feedNameElement) {
			break;
		}
		var feedName = feedNameElement.getAttribute("value");
		feedName = feedName.replace(/_/g, " ");
		names.push(feedName);
		i++
	}
	
	var frequencies = new Array();
	i=1;
	while (true)
	{
		var feedFrequencyElement = document.getElementById("feed_frequency_"+i);
		if (!feedFrequencyElement) {
			break;
		}
		var calculated = feedFrequencyElement.getAttribute("value");
		frequencies.push(calculated*100-50);
		i++;
	}
	console.log("there are "+names.length+" name");
	console.log(names);
	console.log(frequencies);
    chart = new Highcharts.Chart({
        chart: {
            renderTo: 'feedranking-container',
            type: 'column'
        },
        title: {
            text: 'Which RSS feeds get selected most often?'
        },
        xAxis: {
            categories: names,
            labels: {
                rotation: -90,
                align: 'right'
            }
        },
        yAxis: {
            min: -50,
            max: 50,
        	labels: {
                formatter: function() {
                    return this.value+50;
                },
                style: {
                    color: '#89A54E'
                }
            },
            title: {
                text: '% of selection',
                style: {
                    color: '#89A54E'
                }
            }
        },
        tooltip: {
            formatter: function() {
                return ''+
                    this.series.name +': '+ this.y +'';
            }
        },
        credits: {
            enabled: false
        },
        series: [{
            name: 'Feed Selection',
            data: frequencies
        }]
    });
});