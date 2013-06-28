<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html><head>
<jsp:include page="commonHead.jsp"/>
<%@ page import="com.flowbuilder.utils.*" %>
<%	String newUrl = (String)session.getAttribute("GENERATE_DOCS_REFRESH_URL"); %>
<meta http-equiv="refresh" content="0;URL='<% out.print(newUrl); %>'"/>
<title>Generate Docs</title>
<style>
	tr.tablePadding {
		padding-bottom:10px;
	}
</style>
</head>
<body bgcolor="#ffffff" topmargin="20" marginwidth="0" marginheight="0">

<br/>
<table border="0" cellpadding="0" cellspacing="0" topmargin="0">
	<tr>
 		<td valign="top" width="65%" style="padding-left:22px;">
			<table border="0" cellpadding="0" cellspacing="0" topmargin="0">
				<tr>
					<td><p class="title2" style="padding-bottom:10px;">producing documents...</p></td>
				</tr>
				<tr>
					<td valign="top" width="65%">
					<table border="0" cellpadding="0" cellspacing="0" topmargin="0">
					<tr>
					<td width="13">&nbsp;</td>
					<td>
						<p class="normal">Please wait whilst we produce your documents.</p>
						<p class="normal">This should only take a short time. If the page has not refreshed itself after a few minutes, please contact <a class="link_normal" href="ErrorSupportForwardingServlet?mode=tech_support
">technical&nbsp;support.</a></p>
						<p class="primary_blue">Thank you for your patience.</p>
					</td>
					</tr>
					</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
</body>
</html>
