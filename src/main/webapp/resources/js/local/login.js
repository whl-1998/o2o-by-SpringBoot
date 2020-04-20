$(function() {
	var loginUrl = '/o2o/local/logincheck';// 登录验证的controller url
	var usertype = getQueryString('usertype');
	var loginCount = 0;// 登录次数，累积登录三次失败之后自动弹出验证码要求输入
	$('#submit').click(function() {
		var username = $('#username').val();// 获取输入的帐号
		var password = $('#psw').val();// 获取输入的密码
		var verifyCodeActual = $('#j_captcha').val();// 获取验证码信息
		var needVerify = false;// 是否需要验证码验证，默认为false,即不需要
		if (loginCount >= 3) {// 如果登录三次都失败 那么就需要验证码校验了
			if (!verifyCodeActual) {
				$.toast('请输入验证码！');
				return;
			} else {
				needVerify = true;
			}
		}
		// 访问后台进行登录验证
		$.ajax({
			url : loginUrl,
			async : false,
			cache : false,
			type : "post",
			dataType : 'json',
			data : {
				username : username,
				password : password,
				verifyCodeActual : verifyCodeActual,
				//是否需要做验证码校验
				needVerify : needVerify
			},
			success : function(data) {
				if (data.success) {
					$.toast('登录成功！');
					if (usertype == 2) {
						window.location.href = '/o2o/shopadmin/shoplist';// 若用户在前端展示系统页面则自动链接到前端展示系统首页
					} else {
                        window.location.href = '/o2o/frontend/index';// 若用户是在店家管理系统页面则自动链接到店铺列表页中
					}
				} else {
					$.toast('登录失败！' + data.errMsg);
					loginCount++;
					if (loginCount >= 3) {
						// 登录失败三次，需要做验证码校验
						$('#verifyPart').show();
					}
				}
			}
		});
	});
});