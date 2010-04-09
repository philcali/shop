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
    });
  });
});

