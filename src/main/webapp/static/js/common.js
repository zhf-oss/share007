function showLogin(){
	layer.open({
	  type: 2,
	  title: '用户登录',
	  area: ['480px', '500px'],
	  content: 'login.html' //iframe的url
	}); 
}

function reloadPage(){
	window.location.reload();
}

function showRegister(){
	layer.open({
		  type: 2,
		  title: '用户注册',
		  area: ['480px', '500px'],
		  content: 'register.html'
		}); 
}

function showModifyPassword(){
	layer.open({
		  type: 2,
		  title: '修改用户密码',
		  area: ['480px', '500px'],
		  content: 'modifyPassword.html' //iframe的url
		}); 
}

function showModifyUserImage(){
	layer.open({
		  type: 2,
		  title: '修改用户头像',
		  area: ['480px', '600px'],
		  content: 'modifyUserImage.html' //iframe的url
		}); 
}


function showFindPassword(){
	layer.closeAll('iframe');
	layer.open({
		  type: 2,
		  title: '找回用户密码',
		  area: ['480px', '600px'],
		  content: 'findPassword.html' //iframe的url
		}); 
}


function IsURL (str_url) { 
	var strRegex = '^((https|http|ftp|rtsp|mms)?://)'
	+ '?(([0-9a-z_!~*\'().&=+$%-]+: )?[0-9a-z_!~*\'().&=+$%-]+@)?' //ftp的user@ 
	+ '(([0-9]{1,3}.){3}[0-9]{1,3}' // IP形式的URL- 199.194.52.184 
	+ '|' // 允许IP和DOMAIN（域名） 
	+ '([0-9a-z_!~*\'()-]+.)*' // 域名- www. 
	+ '([0-9a-z][0-9a-z-]{0,61})?[0-9a-z].' // 二级域名 
	+ '[a-z]{2,6})' // first level domain- .com or .museum 
	+ '(:[0-9]{1,4})?' // 端口- :80 
	+ '((/?)|' // a slash isn't required if there is no file name 
	+ '(/[0-9a-z_!~*\'().;?:@&=+$,%#-]+)+/?)$'; 
	var re=new RegExp(strRegex); 
	//re.test() 
	if (re.test(str_url)) { 
	return (true); 
	} else { 
	return (false); 
	} 
}

function ResizeImages()
{
   var myimg,oldwidth,oldheight;
   var maxwidth=800;
   var maxheight=1000
   var imgs = document.getElementById('content').getElementsByTagName('img');   //如果你定义的id不是article，请修改此处


   for(i=0;i<imgs.length;i++){
     myimg = imgs[i];


     if(myimg.width > myimg.height)
     {
         if(myimg.width > maxwidth)
         {
            oldwidth = myimg.width;
            myimg.height = myimg.height * (maxwidth/oldwidth);
            myimg.width = maxwidth;
         }
     }else{
         if(myimg.height > maxheight)
         {
            oldheight = myimg.height;
            myimg.width = myimg.width * (maxheight/oldheight);
            myimg.height = maxheight;
         }
     }
   }
}

function sign(){
	$.get("/user/sign",{},function(result){
		if(!result.success){
			alert(result.errorInfo);
		}else{
			alert("签到成功!");
			window.location.reload();
		}
	},"json");
}

/*<![CDATA[*/
layui.use(['element','form','table'], function(){
	var form=layui.form;
	var element = layui.element; //导航的hover效果、二级菜单等功能，需要依赖element模块
	$ = layui.jquery; // 使用jquery
	table = layui.table;

	table.render({
		elem: '#articleListTable'
		,url:'/user/article/list'
		,cols: [[
			{type:'checkbox'}
			,{field:'name', width:350, title: '资源名称',templet:formatName}
			,{field:'arcType', width:100, title: '所属类别',templet:formatArcTypeName}
			,{field:'points', width:60, title: '积分',align:'center'}
			,{field:'publishDate', width:120, title: '发布日期', sort: true,align:'center'}
			,{field:'state', width:100, title: '审核状态',align:'center',templet:formatState}
			,{field:'action', width:150, title: '操作',align:'center',templet:formatAction}
		]]
		,page: true
	});

});

function search(){
	var s_name=$("#s_name").val();
	var s_state=$("#s_state").val();
	table.reload("articleListTable",{
		page:{
			curr:1
		}
		,where:{
			name:s_name,
			state:s_state
		}
	});
}

function formatName(d){
	if(d.state==2){
		return "<a href='/article/"+d.id+"' target='_blank'>"+d.name+"</a>";
	}else{
		return d.name;
	}
}

function formatArcTypeName(d){
	if(d.arcType==null){
		return "";
	}else{
		return d.arcType.name;
	}
}

function formatState(d){
	if(d.state==1){
		return "未审核";
	}else if(d.state==2){
		return "<font color=blue>审核通过</font>";
	}else if(d.state==3){
		return "<a href='#' title='审核未通过原因："+d.reason+"''><font color=red>审核未通过</font></a><br/>";
	}
}

function modifyArticle(id){
	window.location.href="/user/article/toModifyArticlePage/"+id;
}

function deleteSelected(){
	var checkStatus=table.checkStatus('articleListTable');
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
		$.post("/user/article/deleteSelected",{"ids":ids},function(result){
			if(result.success){
				layer.msg("删除成功！");
				table.reload("articleListTable",{});
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
		$.post("/user/article/delete",{"id":id},function(result){
			if(result.success){
				layer.msg("删除成功！");
				table.reload("articleListTable",{});
			}else{
				layer.msg("删除失败，请联系管理员！");
			}
		},"json");
	}, function(){

	});
}

function formatAction(d){
	return "<button class='layui-btn layui-btn-normal layui-btn-xs' onclick='modifyArticle("+d.id+")'><i class='layui-icon layui-icon-edit'></i>编辑</button><button class='layui-btn layui-btn-warm layui-btn-xs' onclick='deleteOne("+d.id+")'><i class='layui-icon layui-icon-delete' ></i>删除</button>";
}
