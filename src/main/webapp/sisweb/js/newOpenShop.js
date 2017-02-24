$(document).on("click", "#show-actions", function() {
    if(userId==null){
        $.alert("您还没有登录，请先登录", function() {
            //点击确认后的回调函数
            var backUrl=window.location.href;
            var encodeUrl=htmlEncodeJQ(backUrl);
            var url=location.host+"/sisweb/mallAccredit?customerId="+customerId+"&backUrl="+encodeUrl+"&gduId="+gduid;
            window.location.href=url;
        });
        return;
    }
    $.modal({
        title: "为了保障您的权益，请填写真实信息",
        text:
        '<input type="text" class="weui_input weui-prompt-input" id="weui-prompt-IDNo"' +
        'placeholder="请输入身份证号" />' +
        '<input type="text" class="weui_input weui-prompt-input" id="weui-prompt-realName" ' +
        'placeholder="请输入真实姓名" />',
        buttons: [
            { text: "取消",className: "default", onClick: function(){
                $.closeModal();
            }},
            { text: "确定", onClick: function(){
                var reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
                IDNo=$("#weui-prompt-IDNo").val();
                IDName=$("#weui-prompt-realName").val();
                if(reg.test(IDNo) === false){
                    $.alert("身份证输入不合法");
                    return;
                }
                if(IDName.length==0||IDName.length>10){
                    $.alert("姓名输入不合法");
                    return;
                }
                saveUserInfo(IDNo,IDName);
            }},
        ]
    });

    function htmlEncodeJQ (str) {
        return escape(str);
    }
//        $.prompt("请输入身份证号","",function(text){
//            var reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
//            if(reg.test(text) === false){
//                $.alert("身份证输入不合法");
//                return;
//            }else {
//                IDNo=text;
//                $.prompt("请输入姓名","",function(text){
//                    if(text.length==0||text.length>10){
//                        $.alert("姓名输入不合法");
//                        return;
//                    }else {
//                        IDName=text;
//                        //保存信息
//                        saveUserInfo(IDNo,IDName);
////                        buyLevel();
//                    }
//
//                });
//
//            }
//
//        });

});

function saveUserInfo(IDNo,IDName){
    $.showLoading("正在保存数据...");
    $.ajax({
        type:'POST',
        url: 'saveUserInfo',
        dataType: 'json',
        data: {IDNo:IDNo,IDName:IDName,customerId:customerId},
        success:function(result){
            $.hideLoading();
            buyLevel();

        },
        error:function(e){
            $.hideLoading();
            $.alert("保存出错");
        }
    });

}


function buyLevel(){
    if(isFree==0){
        submitform(customerId);
    }else {
        var objs=[];
        if(openGoods==undefined||openGoods.length==0){
            $.alert("没有可以开店的等级");
            return;
        }
        $.each(openGoods,function(i,val){
            var obj={
                text:val.levelTitle,
                onClick:function(){
                    $.alert("您选择了购买："+val.levelTitle);
                    submitform(customerId,val.goodsId);
//
                }
            };
            objs[i]=obj;
        })
        $.actions({
            title:"请选择购买等级",
            actions: objs
        });
    }
}

//提交设置
function submitform(customerId,openGoodsId){
    var url="";
    if(isFree==1){
        if(openGoodsId==undefined||openGoodsId.length==0){
            $.alert("无商品，不开店！");
            return;
        }

        url="showOpenShopGoodsDetail?customerId="+customerId+"&goodsId="+openGoodsId;

    }
    if(isFree==0){
        openFreeShop(customerId);
        url="getSisCenter?customerId="+customerId;

    }
    window.location.href=url;
}

function openFreeShop(customerId) {
    $.showLoading("正在开店中...");
    $.ajax({
        type:'POST',
        url: 'openFreeShop',
        dataType: 'json',
        async:false,
        data: {customerId:customerId},
        success:function(result){
            $.hideLoading();
            if(result.code!=200){
                $.alert(result.message);
            }
        },
        error:function(e){
            $.hideLoading();
            $.alert("免费开店出错，请检查网络");
        }
    });
}