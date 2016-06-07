/**
 * Created by Administrator xhl 2015/12/21.
 */
define(function (require, exports, module) {

    //TODO:初始化加载模型列表
   var ModelGrid= $("#js-ModelList").Grid({
        method: 'POST',//提交方式GET|POST
        pageSize: 10,
        height:'auto',
        showNumber: false,
        pageSize: 20,
        pagerCount: 10,
       doubleLine:true,
        pageDetail: true,
        url: '/model/getModelList',//数据来源Url|通过mobel自定义属性配置
        rows: [
            {
                width: '20%', field: 'name', title: '模型名称', align: 'center'
            },
            { width: '40%', field: 'description', title: '描述', align: 'center' },
            { width: '15%', field: 'orderWeight', title: '排序权重', align: 'center' },
            {
                width: '15%', field: 'createTime', title: '创建时间', align: 'center',
                formatter: function (value, rowData) {
                    if(value!=null)
                    {
                        return value.year+"-"+value.monthValue+"-"+value.dayOfMonth+" "+value.hour+":"+value.minute;
                    }
                    return "";
                }
            },
            { width: '10%', field: 'title', title: '操作', align: 'center',
                formatter: function (value, rowData) {
                    return "<a href='javascript:' class='js-hot-modelDelete' data-id='"+rowData.id+"' style='margin-right:10px; color:blue;'>删除</a>" +
                        "<a href='/model/updateModel?id="+rowData.id+"' target='content' style='margin-right:10px; color: blue'>修改</a>"
                }
            },

        ]
    },function(){
       deleteModel();
   });

    //TODO:搜索
    $("#jq-cms-search").click(function(){
        var option={
            dataParam:{
                name:$("#modelName").val()
            }
        };
        ModelGrid.Refresh(option);
    })

    //TODO:显示所有
    $("#jq-cms-searchAll").click(function(){
        var option={
            dataParam:{
                name:""
            }
        };
        ModelGrid.Refresh(option);
    })

    //TODO:删除
    function deleteModel(){
        var obj=$(".js-hot-modelDelete");
        $.each(obj,function(item,dom){
            $(dom).click(function(){//绑定删除事件
                var id=$(this).attr("data-id");//Html5可以使用$(this).data('id')方式来写;
                var layer=require("layer");
                layer.confirm('您确定要删除该条模型记录吗？', {
                    btn: ['确定','取消'] //按钮
                }, function() {
                    $.ajax({
                        url: "/model/deleteModel",
                        data: {
                            id:id
                        },
                        type: "POST",
                        dataType: 'json',
                        success: function (data) {
                            if(data!=null)
                            {
                                var code=parseInt(data.code);
                                switch(code)
                                {
                                    case 200:
                                        layer.msg("删除成功",{time: 2000});
                                        ModelGrid.reLoad();
                                        break;
                                    case 202:
                                        layer.msg("对不起,您没有删除权限",{time: 2000});
                                        break;
                                    default :
                                        layer.msg("系统繁忙,请稍后再试...",{time: 2000});
                                        break;
                                }
                            }
                        }
                    });
                });
            })
        })
    }
});