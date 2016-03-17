$ ->
  $.get "/metricValues", (metricValues) ->
        $.each metricValues, (index2, metricValue) ->
                $("#metricValues").append $("<li >").text metricValue.idMetricValue 