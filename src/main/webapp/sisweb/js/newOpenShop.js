$(document).on("click", "#show-actions", function() {
    if(userId==null){
        $.alert("您还没有登录，请先登录", function() {
            //点击确认后的回调函数
            var backUrl=window.location.href;
            var encodeUrl=htmlEncodeJQ(backUrl);
            var url="/sisweb/mallAccredit?customerId="+customerId+"&backUrl="+encodeUrl;
            if(gduid!=null){
                url=url+"&gduId="+gduid;
            }
            window.location.href=url;
        });
        return;
    }

    var tab=$.cookie('saveDataTab');
    if(tab!=null){
        buyLevel();
    }else {
        $.modal({
            title: '<span style="font-size: 12px;color: #8b8b8b">为了保障您的权益，请填写真实信息</span>',
            text:
            '<input type="text" class="weui_input weui-prompt-input" id="weui-prompt-realName" ' +
            'placeholder="请输入真实姓名" />'+
            '<input type="text" class="weui_input weui-prompt-input" id="weui-prompt-IDNo"' +
            'placeholder="请输入身份证号" />' ,
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
    }



    function htmlEncodeJQ (str) {
        return escape(str);
    }

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
            $.cookie('saveDataTab', 'tab', { expires: 1 });
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
                    //$.alert("您选择了购买："+val.levelTitle);
                    submitform(customerId,val.goodsId);
//
                }
            };
            objs[i]=obj;
        })
        $.actions({
            title:"请选择店铺等级",
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