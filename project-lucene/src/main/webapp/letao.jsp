<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script type="text/javascript" src="js/jquery-1.6.2.js"></script>
<script type="text/javascript">
	$(function(){
	
		$("a").click(function(){
		
			var left=$(this).offset().left;
			var top=$(this).offset().top;
			
		
			$("#message").css("margin-left",left+150+"px");
			$("#message").toggle();
			$("#message").animate({"margin-left":"0px"},2000)
			window.setTimeout(function(){
			$("#message").toggle();
			},2000)
		})
	
	
	})

</script>

<style type="text/css">
	#message{
		width:200px;
		border: 1px solid red;
	}

</style>

</head>
<body>

<a href="javascript:void(0);">最畅销</a>
<a href="javascript:void(0);">最便宜</a>
<a href="javascript:void(0);">最实惠</a>
<a href="javascript:void(0);">最牛b</a>
<div id="message" style="display: none;">
<div>正在加载中....</div>
</div>


</body>
</html>