$(function () {
    getSisGoodsList();
    //if (typeof WeixinJSBridge == "undefined"){
    //    if( document.addEventListener ){
    //        document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);
    //    }else if (document.attachEvent){
    //        document.attachEvent('WeixinJSBridgeReady', onBridgeReady);
    //        document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
    //    }
    //}else{
    //    onBridgeReady();
    //}
    //
    //function onBridgeReady(){
    //    WeixinJSBridge.call('hideToolbar');
    //}
})

//var operTypeInTop=0;

function getSisGoodsList(){
    //$("#loading").show();
    //$("#loading").show();
    $("#resultMsg").text("加载中...");
    $("#resultDescription").show();
    $("#goodsList").hide();
    $("#goodsList").Jload({
            url: "getSisGoodsList",
            method: "POST",
            msgImg: "../sisweb/JLoad/img/loading_cart.gif",
            data: {page: 1, pageSize: 8, selected: true, isAjax:1,customerId:customerId},
            noneTemplete: "<div class='fk_hracf noshop'><img src='../sisweb/images/blank.png' width='100%' />" +
            "<p class='tit_rem_big'>暂无单品</p></div></div>",// 没有数据模版
            isArtTemplete: true,
            Templete: $("#goodsTemplate").html()
        }
        ,function(){
            $("#resultDescription").hide();
            //$("#loading").hide();
            $("#goodsList").show();
            //if(operTypeInTop==2){
            //    jBox.tip("置顶成功!");
            //}
        }
    );
}

function operationOne(goodsId,operType){
    //operTypeInTop = operType;
    //jBox.tip("正在操作...", "loading");
    if(operType!=2){
        $("#resultMsg").text("操作中...");
        $("#resultDescription").show();
    }
    $.ajax({
        url: 'operGoods',
        type: 'POST',
        data: {"operType": operType, "goodsId": goodsId,customerId:customerId},
        dataType: "json",
        success: function (result) {
            $("#resultDescription").hide();
            if (result.success) {
                if (operType == 0){
                    $("#sisGoods_"+goodsId).hide();
                    $("#goodsMsg").text("下架成功");
                    $("#goodsOperDescprition").show();
                    setTimeout('$("#goodsOperDescprition").hide()',1000);
                    //jBox.info("下架成功!");
                    //jBox.alert("下架成功!");
                }
                else if (operType == 1)
                    jBox.alert("上架成功!");
                else if (operType == 2){
                    $(window).unbind("scroll");
                    getSisGoodsList();
                }
                else if (operType == 3)
                    jBox.alert("删除成功!");
            } else {
                $("#goodsMsg").text(result.msg);
                $("#goodsOperDescprition").show();
                setTimeout('$("#goodsOperDescprition").hide()',1000);
                //jBox.alert(result.msg);
            }
        },error:function(){
            $("#resultDescription").hide();
            $("#goodsMsg").text("服务器繁忙");
            $("#goodsOperDescprition").show();
            setTimeout('$("#goodsOperDescprition").hide()',1000);
            //jBox.alert("操作失败");
        }
    });

}

function operationBrand(brandId,type){
    jBox.tip("正在操作...", "loading");
    $.ajax({
        url: 'operBrand',
        type: 'POST',
        data: {"operType": type, "brandId": brandId,customerId:customerId},
        dataType: "json",
        success: function (result) {
            if (result.success) {
                if (type == 1)
                    jBox.tip("上架成功!");
                else if (type == 0){
                    $("#sisBrand_"+brandId).hide();
                    jBox.tip("下架成功!");
                }
                else if (type == 2){
                    jBox.tip("置顶成功!");
                    getSisBrandList();
                }
            } else {
                jBox.tip(result.msg);
            }
        }
    });
}