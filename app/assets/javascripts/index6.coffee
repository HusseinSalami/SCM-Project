$ ->
  $.get "/testeurBesoinTestRun", (testRuns) ->
    $.each testRuns, (index1, testRun) ->
        $("#testRun").append $("<li id='testRun"+testRun.idTestRun+"'>"+testRun.idChangeList+"<ul id='metri"+index1+"'>")
        

$ ->
  $.get "/testeurBesoinMetricDef", (metricDefs) ->
        $.each metricDefs, (index2, metricDef) ->
            $.each metricDef, (index3, metricdefUnitte)->
                $("#metri"+index2).append $("<li id='unite"+metricdefUnitte.idMetricDef+"'><br/>").text metricdefUnitte.name 
                $("#metri"+index2).append $("<input type='text' name='"+metricdefUnitte.idMetricDef+index2+"'/><br/>")
