var chart;
$(document).ready(function() {
	var calculated = document.getElementById("generic_calculated").getAttribute("value");
	var total = document.getElementById("generic_total").getAttribute("value");
	var calculatedPercentage = calculated/total;
	var genericPercentage = (total-calculated)/total;
    chart = new Highcharts.Chart({
        chart: {
            renderTo: 'genericranking-container',
            plotBackgroundColor: null,
            plotBorderWidth: null,
            plotShadow: false
        },
        title: {
            text: 'Generic vs. Calculated topic'
        },
        tooltip: {
            formatter: function() {
                return '<b>'+ this.point.name +'</b>: '+ this.percentage +' %';
            }
        },
        plotOptions: {
            pie: {
                allowPointSelect: true,
                cursor: 'pointer',
                dataLabels: {
                    enabled: true,
                    color: '#000000',
                    connectorColor: '#000000',
                    formatter: function() {
                        return '<b>'+ this.point.name +'</b>: '+ this.percentage +' %';
                    }
                }
            }
        },
        series: [{
            type: 'pie',
            name: 'Browser share',
            data: [
                ['Generic Topic', genericPercentage],
                ['Calculated Topic', calculatedPercentage]
            ]
        }]
    });
});