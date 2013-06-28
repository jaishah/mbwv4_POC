<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 3.2//EN">
<html>
	<head>
		<%@ page import="com.flowbuilder.utils.*" %>
		<%@ page import="com.flowbuilder.datastructures.*" %>
		<%@ page import="com.flowbuilder.ejbs.entities.org.*" %>		
		<jsp:useBean id="jspBean" class="com.flowbuilder.jspbeans.ErrorSupportForwardingBean" scope="session"/>
		<jsp:include page="commonHead.jsp"/>
		<title>Generic Support</title>
	</head>
	<body>
	<% if (!jspBean.getFocusField().equals("")) {	%>
	<%= jspBean.renderJSSetFocus("mainForm", false) %>
	<% } %>
	<%@ page import="com.flowbuilder.frameworks.*" %>
	<%= jspBean.renderRequestNo() %>

	<div align="left">
	<table style="padding-top:20px;" class="position" border="0" cellpadding="0" cellspacing="0" topmargin="0" width="65%">
		<tr>
			<td width="65%" style="padding-left:22px;">
				<table border="0" cellpadding="0" cellspacing="0" topmargin="0" width="100%">
					<!--##################### ERROR PAGE BIT ####################################-->
					<%if(jspBean.getMode().equals("error")) {%>
					<tr>
						<td>
							<table cellpadding="0" cellspacing="0" topmargin="0" border="0" width="100%">
								<tr>
									<td class="title2">support</td>
								</tr>
								<tr>
									<td>
										<p class="normal">A problem has been encountered whilst attempting to process your request. The problem has been logged and will be investigated by the support team. Please accept our sincere apologies for any inconvenience caused.</p>
										<p class="normal">It would be helpful if you could supply us with any further information that you may think helpful. What were you attempting to do? Have you had this problem before? Did you notice anything strange beforehand?</p>
										<p class="normal">Please use the form below to provide us with any such information. Whatever you can provide, will be useful to us. Please once again accept our apologies.</p>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<%}%>
					<!--##################### END ERROR PAGE BIT ################################-->					
				</table>
			</td>
		</tr>
		</table>
		<br/>
		<br/>

		<table cellpadding="0" cellspacing="0" border="0" class="position_nav" width="100%">
			<tr>
				<td>
					<jsp:include page="common_navigation.jsp"/>
				</td>
			</tr>
		</table>
	</div>
</body>
</html>