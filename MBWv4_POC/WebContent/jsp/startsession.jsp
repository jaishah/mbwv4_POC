<%@ page import="com.flowbuilder.processes.startsession.*" %>
<%@ page import="com.flowbuilder.frameworks.*" %>
<%@ page import="com.flowbuilder.utils.*" %>
<%@ page import="com.flowbuilder.ejbs.entities.org.*" %>
<%@ page import="com.flowbuilder.ejbs.entities.user.*" %>
<%@ page import="com.flowbuilder.datastructures.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>

<html>
	<head>
		<jsp:useBean id="jspBean"  class="com.flowbuilder.processes.startsession.StartSessionJSPBean" scope="session"/>
		<jsp:include page="commonHead.jsp"/>
		
		<style>
			.row {
				padding-bottom:20px;
			}
			.smallRow {
				padding-bottom:10px;
			}
		</style>
		<title>Session Start Page</title>
	</head>
	<body>
		
		<table cellspacing="0" cellpadding="0" width="100%" border="0">
			<tr>
				<td style="padding-left:22px;">
					<table cellspacing="0" cellpadding="0" width="70%" border="0" style="position:relative;top:30px;left:0px">
						<tr>
							<td class="title2"><%= jspBean.getFlowTitle() %></td>
						<tr>
						<tr>
							<td class="row">
								<font class="normal">
								<% if (!isValidFlowVersion(jspBean)) {
									out.print("<font class=\"error\">There is no current version of this session available. Please <a href=\"Feedback\" class=\"link_normal\" target=\"_Blank\">contact&nbsp;us</a> if you require access to this session.</font>");
								}
								else {%>
									You will now be asked a series of simple questions to create your documents.
								<% } %>
								</font>
							</td>
						</tr>
						<% if (isValidFlowVersion(jspBean)) { 
							String nameLabel = "";
						%>
						<form Name="standardForm" action="StartSession" method="post">
						<tr>
				      			<td class="row" style="padding-top:0px;"><p class="normal">To <b>start a new session</b>, enter a name for the document in the field below and click '<b>start</b>'.</p></td>
						</tr>
						<tr>
							<td style="padding-top:10px;" class="row">
			  	  				<table class="normal" border="0" cellpadding="0" cellspacing="0">
									<tr>
								      		<td class="normal"><%=nameLabel%></td>
								      		<td class="normal" style="padding-left:10px; padding-right:10px;"><%= jspBean.renderHTMLInput("title", "", 20, 255) %></td>
								      		<td class="normal">
								      			<input name="startNew" border="0" src="/static/images/start_button.gif" type="image"></input>
											
										</td>
			        					</tr>
				 				 </table>
							</td>
						</tr>						
						<tr>
							<td class="row"><p class="error"><%= jspBean.renderHTMLLabel("error", null) %></p></td>
						</tr>

					<% } %>
					</table>
				</td>
			</tr>
			<tr>
				<td class="row" style="padding-top:30px;">
					<jsp:include page="common_navigation.jsp"/>
				</td>
			</tr>
		</table>
		</form>

	</body>
</html>

<%!
private boolean isValidFlowVersion(StartSessionJSPBean jspBean) {
	return jspBean.getFlowVersion()!=0;
}
%>
