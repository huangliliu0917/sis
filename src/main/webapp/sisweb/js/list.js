$(function () {
    getGoods(null, null);

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
    //    WeixinJSBridge.call('hideOptionMenu');
    //}

    $.ajax({
        url: "getCategoryList",
        type: 'GET',
        success: function (result) {
            if (result.length > 0) {
                var length = result.length;
                var avgLength = result.length / 4;
                var str = "";
                for (var i = 0; i < avgLength; i++) {
                    str += '<div class="custom-paixu nextlist">';
                    if (4 * i < length) {
                        str += '<a href="javascript:void(0)" onclick="getCategoryId(this)" categoryId="' + result[4 * i].sisId + '">' + result[4 * i].sisName + '</a>';
                    } else {
                        str += '<a href="javascript:void(0)"></a>';
                    }
                    if (4 * i + 1 < length) {
                        str += '<a href="javascript:void(0)" onclick="getCategoryId(this)" categoryId="' + result[4 * i + 1].sisId + '">' + result[4 * i + 1].sisName + '</a>';
                    } else {
                        str += '<a href="javascript:void(0)"></a>';
                    }
                    if (4 * i + 2 < length) {
                        str += '<a href="javascript:void(0)" onclick="getCategoryId(this)" categoryId="' + result[4 * i + 2].sisId + '">' + result[4 * i + 2].sisName + '</a>';
                    } else {
                        str += '<a href="javascript:void(0)"></a>';
                    }
                    if (4 * i + 3 < length) {
                        str += '<a href="javascript:void(0)" onclick="getCategoryId(this)" categoryId="' + result[4 * i + 3].sisId + '">' + result[4 * i + 3].sisName + '</a>';
                    } else {
                        str += '<a href="javascript:void(0)"></a>';
                    }
                    str += '</div>';
                }
                $("#categoryList").append(str);
            }
        }
    })
})

var categoryId = null;

function getGoods(name, cId) {
    var data = {
        page: 1,
        pageSize: 10
    }
    if (null != name) {
        data.name = name;
    }
    if (null != cId) {
        data.categoryId = cId;
    }
    $("#goodsList").Jload({
        url: "getGoodsList",
        method: "POST",
        msgImg: "../sisweb/JLoad/img/loading_cart.gif",
        data: data,
        noneTemplete: "<div style='text-align:center;'>没有数据</div>",// 没有数据模版
        isArtTemplete: true,
        Templete: $("#goodsTemplate").html()
    });
}

function getGoodsByNameAndCategory() {
    var name = $("#goodsName").val();
    getGoods(name, categoryId);
}

function getGoodsByName() {
    var name = $("#goodsName").val();
    categoryId = null;
    getGoods(name, categoryId);
}

function openOrHideCategoryWin() {
    if ($("#categoryList").is(":hidden")) {
        $("#categoryList").show();
        $("#updown").attr("class", "updown-a");
    } else {
        $("#categoryList").hide();
        $("#updown").attr("class", "updown");
    }
}

function operationGoods(goodsId, obj) {
    jBox.tip("正在操作...", "loading");
    $.ajax({
        url: 'operGoods',
        type: 'POST',
        data: {"operType": 1, "goodsId": goodsId},
        dataType: "json",
        success: function (result) {
            if (result.success) {
                $(obj).parent("p").hide();
                jBox.tip("上架成功");
            } else {
                jBox.tip(result.msg);
            }
        }
    });
}

function getCategoryId(obj) {
    var id = $(obj).attr("categoryId");
    var name = $("#goodsName").val();
    getGoods(name, id);
    $("#categoryList").hide();
    $("#updown").attr("class", "updown");
}