<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>

<html>
	<head>
		<jsp:useBean id="jspBean"  class="com.poc.opensrs.OpenSRSDomainSearchJSPBean" scope="session"/>
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
						
						<form Name="standardForm" action="OpenSRSDomainSearch" method="post">
						<input type="hidden" name="mode" value="<%=jspBean.getMode()%>"/>
						<input type="hidden" name="step" value="<%=jspBean.getStep()%>"/>
						<tr>
							<td style="padding-top:10px;" class="row">
			  	  				<table class="normal" border="0" cellpadding="0" cellspacing="0">
									<tr>
								      		<td class="normal">Search:</td>
								      		<td class="normal" style="padding-left:10px; padding-right:10px;"><%= jspBean.renderHTMLInput("term", "", 20, 255) %></td>
								      		<td class="normal">
								      			<%if (!jspBean.getStep().equals("end")) {%>
								      				<input name="=jspBean.getStep()" border="0" src="static/images/<%=jspBean.getStep()%>_button.gif" type="image"></input>
								      			<%}%>
										</td>
			        					</tr>
				 				 </table>
							</td>
						</tr>						
						<tr>
							<td class="row"><p class="error"><%if (jspBean.getXml()!=null) {out.print(jspBean.getXml());} %></p></td>
						</tr>

						<tr>
							<td class="row"><p class="error"><%= jspBean.renderHTMLLabel("error", null) %></p></td>
						</tr>

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
<p><a href="menu.jsp">Back</a></p>
	</body>
</html>

