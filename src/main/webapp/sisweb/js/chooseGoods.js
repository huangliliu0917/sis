function changeShelvesModel(obj){
    var sub=layer.load(0, {shade: false});
    $.ajax({
        url: 'switchShelves',
        type: 'GET',
        data:{customerId:customerId},
        dataType: "json",
        success: function (resultModel) {
            layer.close(sub);
            if (resultModel.code==200) {
                var modelTest=$(obj).text();
                if(modelTest=='当前模式:一键铺货'){
                    layer.msg("切换成功!取消所有商品的上架")
                }else {
                    layer.msg("切换成功!已上架所有商品")
                }
                window.location.reload();
            } else {
                layer.msg(resultModel.message);
            }
        }
    });
}