$(function () {
    //getSisGoodsList();
    getSisBrandList();
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

function getSisBrandList() {
    //$("#loading").show();
    //$("#resultMsg").text("加载中...");
    //$("#resultDescription").show();
    var loadGoods=layer.load(0, {shade: false});
    $("#brandList").hide();
    $("#brandList").Jload({
            url: "getSisBrandList",
            method: "POST",
            msgImg: "../sisweb/JLoad/img/loading_cart.gif",
            //scorllBox:"brandList",
            data: {page: 1, pageSize: 8, selected: true,customerId:customerId},
            noneTemplete: "<div class='fk_hracf noshop'><img src='../sisweb/images/blank.png' width='100%' />" +
            "<p class='tit_rem_big'>暂无品牌</p></div></div>",// 没有数据模版
            isArtTemplete: true,
            Templete: $("#brandTemplate").html()
        },function(){
            layer.close(loadGoods);
            //$("#resultDescription").hide();
            //$("#loading").hide();
            $("#brandList").show();
        }
    );
}

function changeGoodsOrBrand(status){

    if(status==1){
        $("#goodsBanner").addClass("on");
        $("#brandBanner").removeClass("on");
        $("#goodsList").show();
        $("#brandList").hide();
    }else if(status==2){
        $("#goodsBanner").removeClass("on");
        $("#brandBanner").addClass("on");
        $("#goodsList").hide();
        $("#brandList").show();
    }
}

function operationBrand(brandId,type){
    //jBox.tip("正在操作...", "loading");
    if(type!=2){
        var modify=layer.load(0, {shade: false});
        //$("#resultMsg").text("操作中...");
        //$("#resultDescription").show();
    }

    $.ajax({
        url: 'operBrand',
        type: 'POST',
        data: {"operType": type, "brandId": brandId,customerId:customerId},
        dataType: "json",
        success: function (result) {
            layer.close(modify);
            //$("#resultDescription").hide();
            if (result.success) {
                if (type == 1)
                    //jBox.alert("上架成功!");
                    layer.msg("上架成功！");
                else if (type == 0){
                    $("#sisBrand_"+brandId).hide();
                    $("#goodsMsg").text("下架成功");
                    $("#goodsOperDescprition").show();
                    setTimeout('$("#goodsOperDescprition").hide()',1000);
                }
                else if (type == 2){
                    getSisBrandList();
                    //jBox.tip("置顶成功!");
                }
            } else {
                //jBox.alert(result.msg);
                layer.msg(result.msg);
            }
        }
    });
}