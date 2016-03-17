String button = "<button>Add Version</button>";
String button2="<button>Run Test</button>";
button2+="</form>";
button += "</form>";
$ ->
  $.get "/changeLists", (changeLists) ->
    $.each changeLists, (index, changeList) ->
      $("#version"+changeList.versionId).append $("<li  id= 'change" + changeList.id + "'></li>").text changeList.description
      $("#change"+changeList.id).append $("<div id= 'diver" + changeList.id + "' >")
      $("#diver"+changeList.id).append("<form method='POST' action='/version3/"+changeList.id+ "'  id= 'form" + changeList.id + "'style='float:left;'>"+ button+"<form method='GET' action='/testRun/"+changeList.id+ "'  id= 'form_test_run" + changeList.id + "'>"+ button2+"<br/>")

