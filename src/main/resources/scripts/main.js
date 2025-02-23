$(() => {
  var table = layui.table;
  table.init('demo', {
    height: 600,
    limit: 10
  });


  $('#addNew').click(() => {
    layer.open({
      title: '在线调试'
      , content: $('#addNform')
    });
  })

})