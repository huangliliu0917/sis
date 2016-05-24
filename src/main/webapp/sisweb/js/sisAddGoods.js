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

var sisAddGoodsProvider = {};

var categoryId = null;
var sortOption = null;

var goodsPageType = null;

sisAddGoodsProvider.init = function (pageType) {
    goodsPageType = pageType;
    //sisAddGoodsProvider.getGoods(null);
    //sisAddGoodsProvider.initCategoryList();
    var goodsKeywords = $("#goodsKeywords").val();
    sisAddGoodsProvider.getGoods(goodsKeywords, 1);
}
sisAddGoodsProvider.initCategoryList = function () {
    $.ajax({
        url: "getCategoryList",
        type: 'GET',
        data:{customerId:customerId},
        success: function (result) {
            if (result.length > 0) {
                var length = result.length;
                var avgLength = result.length / 2;
                var str = '<div class="hr_word">';
                str += '<p class="xxk">' + '<a href="javascript:void(0)" onclick="sisAddGoodsProvider.getCategoryId(this)" categoryId="-1">全部商品</a></p>';
                for (var i = 0; i < avgLength; i++) {
                    str += '<p class="xxk">';
                    if (2 * i < length) {
                        str += '<a href="javascript:void(0)" onclick="sisAddGoodsProvider.getCategoryId(this)" categoryId="' + result[2 * i].sisId + '">' + result[2 * i].sisName + '</a>';
                    } else {
                        str += '<a href="javascript:void(0)"></a>';
                    }
                    if (2 * i + 1 < length) {
                        str += '<a href="javascript:void(0)" onclick="sisAddGoodsProvider.getCategoryId(this)" categoryId="' + result[2 * i + 1].sisId + '">' + result[2 * i + 1].sisName + '</a>';
                    } else {
                        str += '<a href="javascript:void(0)"></a>';
                    }
                    str += '</p>';
                }
                str += '<p></p><p style="border-bottom:1px solid #DDDDDD"></p>';
                str += '</div>';
                $("#categoryList").append(str);


                $("#pageTypes").show();
                $("#mask").show();
                $("#categoryLi").attr("class", "hot-keywords  border-none");
                $("#conditionLi").attr("class", "keynomal");
                $("#categoryA").attr("class", "keyword");
                $("#conditionA").attr("class", "keyword2");
                $("#categoryList").show();
                $("#sortList").hide();
                $("#categoryList").slideDown();
            }
        }
    })
}
//点击“全部”
sisAddGoodsProvider.getAllGoods = function (obj) {
    goodsPageType = 0;
    $("#categoryList").hide();
    $("#sortList").hide();
    $("#mask").hide();
    //
    $("#catgeory li").removeClass("on state_switch");
    $(obj).addClass("on state_switch");
    //
    $(".xxk a").attr("style", "color:none");
    //
    var goodsKeywords = $("#goodsKeywords").val();
    categoryId = null;
    sortOption = null;
    sisAddGoodsProvider.getGoods(goodsKeywords);
}
//点击“分类”
sisAddGoodsProvider.openOrHideCategoryWin = function (obj) {
    goodsPageType = 1;
    $("#categoryLi").attr("class", "hot-keywords  border-none");
    $("#conditionLi").attr("class", "keynomal");
    $("#sortList").hide();
    $("#mask").show();
    $("#categoryList").slideDown();
    $("#categoryA").attr("class", "keyword");
    $("#conditionA").attr("class", "keyword2");
}
sisAddGoodsProvider.hideHref = function (obj) {
    $("#goodsList a").each(function () {
        $(this).attr("href", "javascript:void(0)");
    });
}
sisAddGoodsProvider.showHref = function (obj) {
    $("#goodsList a").each(function () {
        var test = $(this).attr("test");
        $(this).attr("href", test);
    });
}

//点击“品牌”
sisAddGoodsProvider.getAllBrands = function (obj) {
    location = "getBrandListPage?customerId="+customerId;
}
//点击“条件”
sisAddGoodsProvider.openOrHideSortWin = function (obj) {
    goodsPageType = 2;
    $("#categoryList").hide();
    $("#mask").show();
    $("#sortList").show();
    $("#sortList").slideDown();
    $("#categoryLi").attr("class", "keynomal");
    $("#conditionLi").attr("class", "hot-keywords  border-none");
    $("#categoryA").attr("class", "keyword2");
    $("#conditionA").attr("class", "keyword");
}

//点击“筛选”
sisAddGoodsProvider.openOrHidePageTypes = function () {
    if ($("#pageTypes").is(":hidden")) {
        $("#pageTypes").show();
        $("#mask").show();
        $("#categoryLi").attr("class", "hot-keywords  border-none");
        $("#conditionLi").attr("class", "keynomal");
        $("#categoryA").attr("class", "keyword");
        $("#conditionA").attr("class", "keyword2");
        $("#categoryList").show();
        $("#sortList").hide();
        $("#categoryList").slideDown();
    } else {
        $("#pageTypes").hide();
        $("#mask").hide();
        $("#categoryList").hide();
        $("#sortList").hide();
    }
}

/**
 *
 * @param name
 * @param cId
 * @param sortOption    0:直推返利
 *                      1:商品人气
 */
sisAddGoodsProvider.getGoods = function (keywords, searchFlag) {
    var data = {
        page: 1,
        pageSize: 10,
        isAjax: 1,
        customerId:customerId
    }
    if (null != keywords && 'undefined' != typeof(keywords)) {
        data.keywords = keywords;
    }
    if (null != categoryId && 'undefined' != typeof(categoryId) && categoryId != -1) {
        data.categoryId = categoryId;
    }
    if (null != sortOption && 'undefined' != typeof(sortOption)) {
        data.sortOption = sortOption;
    }

    //$("#loading").show();
    $("#resultMsg").text("加载中...");
    $("#resultDescription").show();
    $("#goodsList").hide();
    $("#pageTypes").hide();
    $("#mask").hide();

    $("#goodsList").Jload({
        url: "getGoodsList",
        method: "POST",
        msgImg: "/sisweb/JLoad/img/loading_cart.gif",
        data: data,
        noneTemplete: "<div style='text-align:center;'>没有数据</div>",// 没有数据模版
        isArtTemplete: true,
        Templete: $("#goodsTemplate").html()
    }, function () {
        //$("#loading").hide();
        $("#resultDescription").hide();
        $("#goodsList").show();
    });
}

sisAddGoodsProvider.getGoodsByNameAndCategory = function () {
    var goodsKeywords = $("#goodsKeywords").val();
    $(window).unbind("scroll");//解除下拉条事件，防止下拉后，商品list组件重复刷新
    sisAddGoodsProvider.getGoods(goodsKeywords);
}

/**
 *
 * @param goodsId
 * @param obj
 * @param operType 上架：1
 *                  下架：0
 */
sisAddGoodsProvider.operateGoods = function (goodsId, obj, operType) {
    if ($("#categoryList").is(":visible") || $("#sortList").is(":visible")) {
        return;
    }
    var goodsCount = $("#goodsCount").val();
    $("#resultMsg").text("操作中...");
    $("#resultDescription").show();

    $.ajax({
        url: 'operGoods',
        type: 'POST',
        data: {
            "goodsId": goodsId,
            "operType": operType,
            customerId:customerId
        },
        dataType: "json",
        success: function (result) {
            $("#resultDescription").hide();
            if (result.success) {
                if (operType == 1) {
                    $(obj).attr("onclick", "sisAddGoodsProvider.operateGoods(" + goodsId + ",this,0)");
                    $('img', obj).attr("src", "images/a06.png");
                    goodsCount++;
                    $("#goodsCount").val(goodsCount);
                    $("#goodsMsg").text("上架成功");
                    $("#goodsNum").text("已上架商品"+goodsCount);
                    $("#goodsOperDescprition").show();
                    setTimeout('$("#goodsOperDescprition").hide()',1000);
                }
                else if (operType == 0) {
                    //$(obj).parent("p").hide();
                    $(obj).attr("onclick", "sisAddGoodsProvider.operateGoods(" + goodsId + ",this,1)");
                    $('img', obj).attr("src", "images/a03.png");
                    goodsCount--;
                    $("#goodsCount").val(goodsCount);
                    $("#goodsMsg").text("下架成功");
                    $("#goodsNum").text("已上架商品"+goodsCount);
                    $("#goodsOperDescprition").show();
                    setTimeout('$("#goodsOperDescprition").hide()',1000);
                }
            } else {
                //jBox.alert(result.msg);
                $("#goodsMsg").text(result.msg);
                $("#goodsNum").text("");
                $("#goodsOperDescprition").show();
                setTimeout('$("#goodsOperDescprition").hide()',1000);
            }
        },
        error:function(){
            $("#resultDescription").hide();
            $("#goodsMsg").text("系统繁忙，请稍后再试");
            $("#goodsNum").text("");
            $("#goodsOperDescprition").show();
            setTimeout('$("#goodsOperDescprition").hide()',1000);
        }
    });
}

/**
 *
 * @param brandId
 * @param obj
 * @param operType 上架：1
 *                  下架：0
 */
//sisAddGoodsProvider.operateBrands = function (brandId, obj, operType) {
//    //下架时确认框
//    if (operType == 0) {  //TODO
//
//    }
//
//    jBox.tip("正在操作...", "loading");
//    $.ajax({
//        url: 'operBrand',
//        type: 'POST',
//        data: {
//            "brandId": brandId,
//            "operType": operType,
//            customerId:customerId
//        },
//        dataType: "json",
//        success: function (result) {
//            if (result.success) {
//                if (operType == 1) {
//                    $(obj).attr("onclick", "sisAddGoodsProvider.operateBrands(" + brandId + ",this,0)");
//                    $(obj.children[0]).removeClass("git-a");
//                    $(obj.children[0]).addClass("git-a git-pos");
//                    jBox.tip("上架成功");
//                }
//                else if (operType == 0) {
//                    //$(obj).parent("p").hide();
//                    $(obj).attr("onclick", "sisAddGoodsProvider.operateBrands(" + brandId + ",this,1)");
//                    $(obj.children[0]).removeClass("git-a git-pos");
//                    $(obj.children[0]).addClass("git-a");
//                    jBox.tip("下架成功");
//                }
//            } else {
//                jBox.tip(result.msg);
//            }
//        }
//    });
//}

sisAddGoodsProvider.getCategoryId = function (obj) {
    categoryId = $(obj).attr("categoryId");
    var goodsKeywords = $("#goodsKeywords").val();
    sisAddGoodsProvider.getGoods(goodsKeywords, 1);
    $("#categoryList").hide();
    $("#mask").hide();
    $(".xxk a").attr("style", "color:none");
    $(obj).attr("style", "color:red");

    /*$("#updown").attr("class", "updown");*/
}

sisAddGoodsProvider.sortOperation = function (obj) {
    sortOption = $(obj).attr("sortOption");
    var goodsKeywords = $("#goodsKeywords").val();
    sisAddGoodsProvider.getGoods(goodsKeywords, 1);
    $("#sortList").hide();
    $("#mask").hide();
    $(".xxk2 a").attr("style", "color:none");
    $(obj).attr("style", "color:red");

    /*$("#updown").attr("class", "updown");*/
}

sisAddGoodsProvider.hideOperation = function () {
    $(".fk_hracf,input").click(function (event) {
        if ($("#categoryList").is(":visible") || $("#sortList").is(":visible")) {
            $("#categoryList").slideUp();
            $("#sortList").slideUp();
            $("#mask").hide();
            event.preventDefault(); //阻止事件冒泡
        }
    });
}