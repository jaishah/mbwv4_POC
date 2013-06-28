<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>

<html>
	<head>
		<jsp:useBean id="jspBean"  class="com.poc.iriscb.IrisCBTestJSPBean" scope="session"/>
		<jsp:include page="commonHead.jsp"/>
		
		<style>
			.row {
				padding-bottom:20px;
			}
			.smallRow {
				padding-bottom:10px;
			}
		</style>
		<title>Iris CourseBooker POC</title>
	</head>
	<body>
		
		<table cellspacing="0" cellpadding="0" width="100%" border="0">
			<tr>
				<td style="padding-left:22px;">
					<table cellspacing="0" cellpadding="0" width="70%" border="0" style="position:relative;top:30px;left:0px">
						
						<form Name="standardForm" action="IrisCBTest" method="post">
						
						<tr>
							<td style="padding-top:10px;" class="row">
			  	  				<table class="normal" border="0" cellpadding="0" cellspacing="0">
									<tr>
								      		<td class="normal">Search:</td>
								      		<td class="normal" style="padding-left:10px; padding-right:10px;"><%= jspBean.renderHTMLInput("term", "", 20, 255) %></td>
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

