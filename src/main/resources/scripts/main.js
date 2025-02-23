layui.use(['layer', 'table', 'jquery'], function () {
  var layer = layui.layer;
  var table = layui.table;
  var $ = layui.jquery;

  $(function () {
    table.init('demo', {
      height: 600,
      limit: 10
    });


    $('#addNew').click(function () {
      layer.open({
        title: '在线调试',
        content: $('#addNform'),
        yes: function (index, layero) {
          //do something
          var formData = new FormData();
          formData.append('file', $('#fileInput')[0].files[0]); // 获取文件
          formData.append('bookName', $('#bookName').val()); // 获取书名
          formData.append('desc', $('#desc').val()); // 获取描述
          formData.append('coverImg', $('#coverImg').val()); // 获取封面
          formData.append('authorName', $('#authorName').val()); // 获取封面
          $.ajax({
            url: '/data-operate/submitForm', // 后端接口地址
            type: 'POST',
            data: formData,
            processData: false, // 不处理发送的数据
            contentType: false, // 不设置内容类型
            success: function (response) {
              alert('提交成功: ' + response.message);
            },
            error: function (error) {
              alert('提交失败: ' + error.responseText);
            }
          });
          window.location.reload();
        }
      });
    });

  })

});
