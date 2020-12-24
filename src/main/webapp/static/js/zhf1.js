function submitData(){
    if($("#userName").val().trim()==""){
        $("#errorInfo").text("请输入用户名！");
        return false;
    }

    if($("#password").val().trim()==""){
        $("#errorInfo").text("请输入密码！");
        return false;
    }

    if($("#password2").val().trim()==""){
        $("#errorInfo").text("请输入确认密码！");
        return false;
    }

    if($("#email").val().trim()==""){
        $("#errorInfo").text("请输入邮箱！");
        return false;
    }

    if($("#password").val().trim().length<6){
        $("#errorInfo").text("密码至少6位！");
        return false;
    }

    if($("#password").val().trim()!=$("#password2").val().trim()){
        $("#errorInfo").text("确认密码不对！");
        return false;
    }

    if(myForm.vaptcha_token.value==""){
        $("#errorInfo").text("请进行人机验证！");
        return false;
    }

    // 点击注册按钮时，一个加载状态，防止后台注册时间过长.
    var index = layer.load(0, {
        shade: [0.1,'#fff'] //0.1透明度的白色背景
    });
    // 防止用户多点击注册按钮多注册，禁止.
    $("#sbtn").attr('disabled',true);

    $.post("/user/register",{userName:$("#userName").val().trim(),password:$("#password").val().trim(),email:$("#email").val().trim(),vaptcha_token:myForm.vaptcha_token.value},function(result){
        // 关闭上面的加载状态.
        layer.close(index);
        // 注册按钮可操作.
        $("#sbtn").attr('disabled',false);
        if(result.success){
            alert("恭喜您，注册成功！");
            parent.reloadPage();
        }else{
            $("#errorInfo").text(result.errorInfo);
            if('人机验证失败！'==result.errorInfo){
                alert('人机验证失败！');
                window.location.reload();
            }
        }
    },"json");

}