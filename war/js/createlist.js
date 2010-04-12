$(document).ready(function() {
  $('.create_list').click(function() {
    var html = ''+ 
      '<form id="listform" method="get">' +
      ' List <input class="listname" type="text" name="listname"><br/>' +
      '<input type="submit" value="submit">'+
      '</form>';
    $('#dynamic').html(html);

    $('#listform').submit(function () {
      $.get('/createlist/' + $('.listname').val(), function (data) {
        $('#dynamic').html(data);
        tag_list();
      });
      return false;
    });
  });

  $('.manage_list').click(function() {
    $.get('/lists', function(data) {
      $('#dynamic').html(data);
      
      $('.edit').each(function(index) {
        var id = $(this).attr('id').split('_')[1];
        $(this).click(function () {
          $.get('/list/' + id, function(innerdata) {
            $('#dynamic').html(innerdata);
            tag_list();
          });
        });
      });

      $('.delete').each(function(index) {
        var id = $(this).attr('id').split('_')[1];
        $(this).click(function() {
          $.post('/delete/' + id, function(innerdata) {
            $('.success').text(innerdata);
            $('#list_'+id).remove();
          });
        });
      });
      
      $('.share').each(function(index) {
    	  var id = $(this).attr('id').split('_')[1];
    	  $(this).click(function (){
    		  $('.results').html('<form id="shareform">'+
    		    'Share with <input id="user" type="text" name="user"><br/>' +
    		    '<input type="submit" value="Share">' + 
    		  '</form>');
    		  $('#shareform').submit(function(){
    			 var user = $("#user").val();
    			 $.post("/sharelist/" + id + "/" + user, function(data){
    				 $('.success').html(data);
    			 });
    			 return false;
    		  });
    	  });
      });
    });
  });
  
  $('.shared_list').click(function() {
	  $.get("/requests", function(data) {
		  $('#dynamic').html(data);
		  
		  $('.accept').each(function(index) {
			  $(this).click(function(){
				  var key = $(this).attr('id').split('_')[1];
				  $.post('/acceptlist/' + key, function(data){
					  if(data == "success") {
						  $('#list_' + jquerySafe(key)).remove();
					  } else {
						  $('.error').html("You requested this one.");
						  $('.error').click(function(){
							 $(this).html(''); 
						  });
					  }
				  });  
			  });
		  });
		  
		  $('.deny').each(function(index){
			  $(this).click(function (){
				  var key = $(this).attr('id').split('_')[1];
				  alert(jquerySafe(key));
				  $('#list_' + jquerySafe(key)).remove();
				  /*
				  $.post('/denyrequest/' + key, function(data){
					  $('#list_' + jquerySafe(key)).remove();
					  $('.success').html("Removed request");
				  });
				  */
			  });
		  });
	  });
  });
});

function jquerySafe(string) {
	var possible = ['@', '.', '!', ':', '~', '#', ';', '&', '$', '^', '|', 
	                '=', '>' ,'(', ')', '[',']', "'", '"', '+', '*', ',', '/'];
	for (var i = 0; i < possible.length; i++) {
		var regexp = new RegExp('\\' + possible[i], 'gi');
		string = string.replace(regexp, '\\\\' + possible[i]);
	}
	return string;
}