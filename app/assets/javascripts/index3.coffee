$ ->
  $.get "/metric", (metrics) ->
    $.each metrics, (index, metric) ->
         $("#test").append $("<li  id= 'metric " + metric.idMetricDef + "'>").text metric.name
         $("#listMetric").append $("<input type='checkbox' class = 'metric' onclick='myFunction()' value='"+metric.idMetricDef+"' >"+metric.name+"</input></br>")