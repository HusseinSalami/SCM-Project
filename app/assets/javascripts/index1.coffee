String str = "<input type='text' name='description'/>";
str +="<button>Add ChangeList</button>";
str += "</form> <br> <br>";
$ ->
  $.get "/versions", (versions) ->
    $.each versions, (index, version) ->
      $("#versions").append $("<li id= '" + version.id + "'>").text version.name
      $("#versions").append $("<br><ul id='version" + version.id + "'>")
      $("#versions").append $("<form method='POST' action='/changeList2/"+ version.id + "'style='float:left;'>" + str)
      $("#1er").append $("<li id='" +version.headId+"'>").text (" base id: "+version.baseId+" head id:"+version.headId+"/n")