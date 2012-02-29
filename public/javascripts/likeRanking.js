var chart;
$(document).ready(function() {
	var preferred = document.getElementById("preferred").getAttribute("value");
	var total = document.getElementById("total").getAttribute("value");
	var percentagePreferred = preferred/total;
	var percentageNotPreferred = (total-preferred)/total;
    chart = new Highcharts.Chart({
        chart: {
            renderTo: 'likeranking-container',
            plotBackgroundColor: null,
            plotBorderWidth: null,
            plotShadow: false
        },
        title: {
            text: 'Higher vs. Lower ranked LIKE topic'
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
                ['Higher ranked Like',   percentagePreferred],
                ['Lower ranked Like',    percentageNotPreferred]
            ]
        }]
    });
});