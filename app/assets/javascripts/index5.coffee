$ ->
  $.get "/testRunAll", (runs) ->
    $.each runs, (index, run) ->
         $("#testRunAll").append $("<li  id= 'testrun " + run.idTestDef + "'><br></li>").text "le test run "+run.idTestRun+" est fait sur le changelist de id: " + run.idChangeList