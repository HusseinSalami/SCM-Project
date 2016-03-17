$ ->
  $.get "/test", (tests) ->
    $.each tests, (index, test) ->
         $("#testDef").append $("<li  id= 'testDef " + test.idTestDef + "'>").text test.name
         $("#select_list").append $("<option value='" + test.idTestDef + "'>" + test.name + " </option>")