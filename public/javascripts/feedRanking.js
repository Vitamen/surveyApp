var chart;
$(document).ready(function() {
	console.log(preferred);
    chart = new Highcharts.Chart({
        chart: {
            renderTo: 'feedranking-container',
            type: 'column'
        },
        title: {
            text: 'Which RSS feeds get selected most often?'
        },
        xAxis: {
            categories: ['TechCrunch', 'Engadget', 'Mashable', 'Lifehacker', 'CBS Tech Talk']
        },
        yAxis: {
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
            data: [25,-30,-20,10,15]
        }]
    });
});