<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>

<html>
	<head>
		<jsp:useBean id="jspBean"  class="com.poc.opensrs.OpenSRSMailTestJSPBean" scope="session"/>
		<jsp:include page="commonHead.jsp"/>
		
		<style>
			.row {
				padding-bottom:20px;
			}
			.smallRow {
				padding-bottom:10px;
			}
		</style>
		<title>OpenSRS Mail Test</title>
		<%if (jspBean.getStep().equals("SSO")) {%>
			<script type="text/javascript">
			   window.location="<%=jspBean.getResp()%>";
			</script>
		<%}%>		
	</head>
	<body>
		
		<table cellspacing="0" cellpadding="0" width="100%" border="0">
			<tr>
				<td style="padding-left:22px;">
					<table cellspacing="0" cellpadding="0" width="70%" border="0" style="position:relative;top:30px;left:0px">
						
						<form Name="standardForm" action="OpenSRSMailTest" method="post">
						<input type="hidden" name="mode" value="<%=jspBean.getMode()%>"/>
						<input type="hidden" name="step" value="<%=jspBean.getStep()%>"/>
						<%if (jspBean.getStep().equals("finalise")) {%>
						<h2>MailBox creation with Aliases</h2> <%} %>
						<tr>
							<td style="padding-top:10px;" class="row">
							<%if (jspBean.getStep().equals("start")) {%><h2>Domain Sign-in</h2><%} %>
			  	  				<table class="normal" border="0" cellpadding="0" cellspacing="0">
									<tr>
								      		<td class="normal"><%if (jspBean.getStep().equals("start")) {%>Domain:<%}else{%>User:<%}%></td>
								      		<%if (jspBean.getStep().equals("start")) {%>
								      			<td class="normal" style="padding-left:10px; padding-right:10px;"><%= jspBean.renderHTMLInput("term", "", 20, 255) %></td><td>available right now is 'bcsg.adm'</td>
								      		<%}else{%>
								      			<input type="hidden" name="term" value="<%=jspBean.getTerm()%>"/>
								      			<td class="normal" style="padding-left:10px; padding-right:10px;"><%= jspBean.renderHTMLInput("user", "", 20, 255) %>@<%=jspBean.getTerm()%></td>
								      		<%}%>
								      		
			        					</tr>
			        					<tr>
			        					<%if (jspBean.getStep().equals("finalise")) {%> 
										<td class="normal">Name:</td><td class="normal" style="padding-left:10px; padding-right:10px;"><%= jspBean.renderHTMLInput("name", "", 20, 255) %></td>
										</tr><tr>
										<td class="normal">Password:</td><td class="normal" style="padding-left:10px; padding-right:10px;"><%= jspBean.renderHTMLInput("password", "", 20, 255) %></td>
										</tr>
										<tr>
										<td class="normal">Aliases:</td><td class="normal" style="padding-left:10px; padding-right:10px;"><%= jspBean.renderHTMLInput("aliases", "", 20, 255) %>------Please enter comma separated emailIds</td>
										</tr>
										<tr>
										<td class="normal">Email Forwards:</td><td class="normal" style="padding-left:10px; padding-right:10px;"><%= jspBean.renderHTMLInput("forward", "", 20, 255) %>------Please enter comma separated emailIds</td>
										</tr><%} %>
									
										<tr><td class="normal">
								      			<%if (!jspBean.getStep().equals("end")) {%>
								    <br><br>  				<input name="<%=jspBean.getStep()%>" border="0" alt="Submit" src="static/images/<%=jspBean.getStep()%>_button.gif" type="image"></input>
								      			<%}%>
										</td></tr>
				 				 </table>
							</td>
						</tr>						
						<tr>
							<td class="row"><p class="error"><%if (jspBean.getResp()!=null) {out.print(jspBean.getResp());} %></p></td>
						</tr>

						<tr>
							<td class="row"><p class="error"><%= jspBean.renderHTMLLabel("error", null) %></p></td>
						</tr>
						</form>
					</table>
				</td>
			</tr>
			<%if (jspBean.getStep().equals("finalise")) {%> 
			<tr>
				<td style="padding-left:22px;">
					<table cellspacing="0" cellpadding="0" width="70%" border="0" style="position:relative;top:30px;left:0px">
						
						<form Name="standardForm" action="OpenSRSMailTest" method="post">
						<input type="hidden" name="mode" value="<%=jspBean.getMode()%>"/>
						<input type="hidden" name="step" value="<%=jspBean.getStep()%>"/>
						<tr>
							<td style="padding-top:10px;" class="row">
			  	  				<table class="normal" border="0" cellpadding="0" cellspacing="0">
			  	  				<tr><h2>Webmail</h2></tr>
									<tr>
								      		<td class="normal">Sign in as: </td>
								      		<td class="normal" style="padding-left:10px; padding-right:10px;"><%= jspBean.renderHTMLInput("user", "", 20, 255) %></td>
								      		<td class="normal">
								      				<input name="SSO" border="0" src="static/images/login_button.gif" type="image"></input>
										</td>
			        					</tr>
				 				 </table>
							</td>
						</tr>						
						</form>
					</table>
				</td>
			</tr>		<%} %>
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

