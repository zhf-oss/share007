
/*<![CDATA[*/
layui.use(['element','form','table'], function(){
    var form=layui.form;
    var element = layui.element; //导航的hover效果、二级菜单等功能，需要依赖element模块
    $ = layui.jquery; // 使用jquery
    table = layui.table;

    table.render({
        elem: '#commentListTable'
        ,url:'/user/comment/list'
        ,cols: [[
            {type:'checkbox'}
            ,{field:'content', title: '评论内容'}
            ,{field:'article', width:200, title: '评论帖子',templet:formatArticleName}
            ,{field:'commentDate', width:200, title: '评论日期', sort: true,align:'center'}
            ,{field:'user', width:100, title: '评论用户',templet:formatUserName}
            ,{field:'action', width:100, title: '操作',align:'center',templet:formatAction}
        ]]
        ,page: true
    });

});

function formatArticleName(d){
    return "<a href='/article/"+d.article.id+"' target='_blank'>"+d.article.name+"</a>";
}

function formatUserName(d){
    return d.user.userName;
}

function formatAction(d){
    return "<button class='layui-btn layui-btn-warm layui-btn-xs' onclick='deleteOne("+d.id+")'><i class='layui-icon layui-icon-delete' ></i>删除</button>";
}

function deleteSelected(){
    var checkStatus=table.checkStatus('commentListTable');
    if(checkStatus.data.length==0){
        layer.msg("请选择要删除的数据！");
        return;
    }
    var strIds=[];
    var rows=checkStatus.data;
    for(var i=0;i<checkStatus.data.length;i++){
        strIds.push(rows[i].id);
    }
    var ids=strIds.join(",");
    layer.confirm("您确定要删除这<font color=red>"+checkStatus.data.length+"</font>条记录吗？", {
        title:"系统提示"
        ,btn: ['确定','取消'] //按钮
    }, function(){
        layer.closeAll('dialog');
        $.post("/user/comment/deleteSelected",{"ids":ids},function(result){
            if(result.success){
                layer.msg("删除成功！");
                table.reload("commentListTable",{});
            }else{
                layer.msg("删除失败，请联系管理员！");
            }
        },"json");
    }, function(){

    });
}

function deleteOne(id){
    layer.confirm('您确定要删除这条记录吗？', {
        title:"系统提示"
        ,btn: ['确定','取消'] //按钮
    }, function(){
        layer.closeAll('dialog');
        $.post("/user/comment/delete",{"id":id},function(result){
            if(result.success){
                layer.msg("删除成功！");
                table.reload("commentListTable",{});
            }else{
                layer.msg("删除失败，请联系管理员！");
            }
        },"json");
    }, function(){

    });
}
/*]]>*/