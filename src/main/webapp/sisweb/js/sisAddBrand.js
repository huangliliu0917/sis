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
var sisAddBrandProvider = {};

sisAddBrandProvider.init = function () {
    sisAddBrandProvider.getBrands(null);
}
//点击“全部/分类/排序”
sisAddBrandProvider.getGoodsList = function(type) {
    location = "goodsIndex?pageType="+type;
}
//点击“品牌”
sisAddBrandProvider.getBrandList = function(obj) {
    var brandKeywords = $("#brandKeywords").val();
    sisAddBrandProvider.getBrands(brandKeywords);
}

sisAddBrandProvider.getBrands = function(keywords) {
    var data = {
        page: 1,
        pageSize: 10,
        isAjax: 1,
        customerId:customerId
    }
    if (null != keywords && typeof(keywords) != "undefined") {
        data.keywords = keywords;
    }
    //$("#loading").show();
    $("#brandsList").hide();
    $("#resultMsg").text("加载中...");
    $("#resultDescription").show();

    $("#brandsList").Jload({
        url: "getBrandList",
        method: "POST",
        msgImg: "/sisweb/JLoad/img/loading_cart.gif",
        data: data,
        noneTemplete: "<div style='text-align:center;'>没有数据</div>",// 没有数据模版
        isArtTemplete: true,
        Templete: $("#brandsTemplate").html()
    },function(){
        //$("#loading").hide();
        $("#resultDescription").hide();
        $("#brandsList").show();
    });

}

sisAddBrandProvider.searchBrand = function() {
    var brandKeywords = $("#brandKeywords").val();
    $(window).unbind("scroll"); //解除下拉条事件，防止下拉后，品牌list组件重复刷新
    sisAddBrandProvider.getBrands(brandKeywords);
}

/**
 *
 * @param brandId
 * @param obj
 * @param operType 上架：1
 *                  下架：0
 */
sisAddBrandProvider.operateBrands = function(brandId, obj, operType) {

    var brandCount = $("#brandCount").val();

    //jBox.tip("正在操作...", "loading");
    $("#resultMsg").text("操作中...");
    $("#resultDescription").show();
    $.ajax({
        url: 'operBrand',
        type: 'POST',
        data: { "brandId": brandId,
            "operType": operType,customerId:customerId },
        dataType: "json",
        success: function (result) {
            $("#resultDescription").hide();
            if (result.success) {
                if (operType==1) {
                    $(obj).attr("onclick","sisAddBrandProvider.operateBrands(" +brandId+ ",this,0)");
                    $('img',obj).attr("src","images/a06.png");
                    brandCount++;
                    $("#brandCount").val(brandCount);
                    $("#brandMsg").text("上架成功");
                    $("#brandNum").text("已上架品牌"+brandCount);
                    $("#brandOperDescprition").show();
                    setTimeout('$("#brandOperDescprition").hide()',1000);
                }
                else if (operType==0) {
                    //$(obj).parent("p").hide();
                    $(obj).attr("onclick","sisAddBrandProvider.operateBrands(" +brandId+ ",this,1)");
                    $('img',obj).attr("src","images/a03.png");
                    //jBox.tip("下架成功");
                    brandCount--;
                    $("#brandCount").val(brandCount);
                    $("#brandMsg").text("下架成功");
                    $("#brandNum").text("已上架品牌"+brandCount);
                    $("#brandOperDescprition").show();
                    setTimeout('$("#brandOperDescprition").hide()',1000);
                }
            } else {
                //$("#loading2").hide();
                $("#brandMsg").text(result.msg);
                $("#brandNum").text("");
                $("#brandOperDescprition").show();
                setTimeout('$("#brandOperDescprition").hide()',1000);
            }
        },error:function(){
            $("#resultDescription").hide();
            $("#brandMsg").text("系统繁忙");
            $("#brandNum").text("");
            $("#brandOperDescprition").show();
            setTimeout('$("#brandOperDescprition").hide()',1000);
        }
    });
}
