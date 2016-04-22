function changeShelvesModel(obj){
    jBox.tip("正在操作...", "loading");
    $.ajax({
        url: 'switchShelves',
        type: 'GET',
        dataType: "json",
        success: function (resultModel) {
            if (resultModel.code==200) {
                var modelTest=$(obj).text();
                if(modelTest=='当前模式:一键铺货'){
                    jBox.tip("切换成功!取消所有商品的上架");
                    //$(obj).text("当前模式:自主选货");
                }else {
                    jBox.tip("切换成功!已上架所有商品");
                    //$(obj).text("当前模式:一键铺货");
                }
                window.location.reload();
            } else {
                jBox.tip(resultModel.message);
            }
        }
    });
}