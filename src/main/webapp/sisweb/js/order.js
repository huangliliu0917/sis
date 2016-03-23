$(function () {
    getSisOrderList();

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
    //
    //function onBridgeReady() {
    //    WeixinJSBridge.call('hideOptionMenu');
    //}
})

function getSisOrderList() {
    $("#orderList").Jload({
        url: "getSisOrderList",
        method: "POST",
        msgImg: "../sisweb/JLoad/img/loading_cart.gif",
        data: {page: 1, pageSize: 10,customerId:customerId},
        noneTemplete: "<div class='fk_hracf noshop'><img src='../sisweb/images/blank.png' width='100%' />" +
        "<p class='tit_rem_big'>暂无订单</p></div></div>",// 没有数据模版
        isArtTemplete: true,
        Templete: $("#orderTemplate").html()
    });
}