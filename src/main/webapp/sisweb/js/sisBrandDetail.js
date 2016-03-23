

//function onBridgeReady() {
//    WeixinJSBridge.call('hideOptionMenu');
//}


var sisBrandDetailProvider = {};

sisBrandDetailProvider.init = function () {
    sisBrandDetailProvider.getGoods();
}

$(function () {
    //if (typeof WeixinJSBridge == "undefined") {
    //    if (document.addEventListener) {
    //        document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);
    //    } else if (document.attachEvent) {
    //        document.attachEvent('WeixinJSBridgeReady', onBridgeReady);
    //        document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
    //    }
    //} else {
    //    onBridgeReady();
    //}

    sisBrandDetailProvider.init();
})

/**
 *
 * @param name
 * @param cId
 * @param sortOption    0:直推返利
 *                      1:商品人气
 */
sisBrandDetailProvider.getGoods = function () {
    var brandId = $("#brandId").val();
    if (null == brandId || 'undefined' == typeof(brandId)) {
        return;
    }
    var data = {
        page: 1,
        pageSize: 10,
        isAjax: 1,
        brandId: brandId,
        customerId:customerId
    }
    $("#loading").show();
    $("#goodsList1").hide();

    $("#goodsList1").Jload({
        url: "getGoodsListByBrandId",
        method: "POST",
        msgImg: "/sisweb/JLoad/img/loading_cart.gif",
        data: data,
        noneTemplete: "<div style='text-align:center;'>没有数据</div>",// 没有数据模版
        isArtTemplete: true,
        Templete: $("#brandDetailTemplate").html(),
        error: function(){
            jBox.tip("系统繁忙,请稍微再试");
        },
    },function(){
        $("#loading").hide();
        $("#goodsList1").show();
    });
}