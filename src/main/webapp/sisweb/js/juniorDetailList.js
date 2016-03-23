$(function () {
    $("#juniorDetailList").Jload({
            url: "juniorDetailListAjax",
            method: "POST",
            msgImg: "../sisweb/JLoad/img/loading_cart.gif",
            data: {page: 1, pageSize: 8, srcType: srcType, customerId:customerId},
            noneTemplete: "<div class='fk_hracf noshop'><img src='../sisweb/images/blank.png' width='100%' />" +
            "<p class='tit_rem_big'>无此列表</p></div></div>",// 没有数据模版
            isArtTemplete: true,
            Templete: $("#detailTemplate").html()
        }
    );
})

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
    //
    //function onBridgeReady() {
    //    WeixinJSBridge.call('hideOptionMenu');
    //}
})