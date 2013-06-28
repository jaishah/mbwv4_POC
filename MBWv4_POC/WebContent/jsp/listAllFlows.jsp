<html>
	</head>
		<%@ page import="com.flowbuilder.utils.*" %>
		<jsp:useBean id="jspBean" class="com.flowbuilder.jspbeans.FlowListBean" scope="session"/>
		<jsp:include page="commonHead.jsp"/> 		
		<title>Sessions</title>
	</head>

	<%@ page import="com.flowbuilder.frameworks.*"%>
	<%@ page import="java.util.*" %>

	<body>

	<table align="left" style="padding-left:0px;" border="0" cellpadding="0" cellspacing="0" topmargin="0" width="600px">
		<tr>
			<td valign="top" width="80%" style="padding-bottom:45px;">
				<table style="position:relative;top:30px;left:22px" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td >
							<p class="title2">Session list</p>
				   </td>
					</tr>
				  <tr>
					<td >
							<table cellpadding="0" cellspacing="0" border="0">
									<tr>
										<td colspan="2">
											<p class="title2" style="color:6666FF">Author sessions</p>
										</td>
									</tr>
									<%ArrayList au = jspBean.getAuthorUrls();
							  ArrayList ahl = jspBean.getAuthorHyperlinks();

							  for(int x=0; x<au.size(); x++) {
								out.print("<tr>");
								out.print("<td style=\"padding-left:5px; padding-right:30px;\" class=\"primary\">");
										out.print(x+1);
										out.print("</td>");
										out.print("<td>");
								out.print("<a href=\""+au.get(x).toString()+"\" class=\"link_normal\">"+ahl.get(x).toString()+"</a>");
								out.print("</td>");
								out.print("</tr>");
							  }
							%>
									<tr>
										<td colspan="2">
											<p class="title2" style="color:6666FF; padding-top:15px;">Advisor sessions</p>
										</td>
									</tr>
									<%ArrayList adu = jspBean.getAdvisorUrls();
							  ArrayList adhl = jspBean.getAdvisorHyperlinks();

							  for(int x=0; x<adu.size(); x++) {
							  out.print("<tr>");
								out.print("<td style=\"padding-left:5px; padding-right:30px;\" class=\"primary\">");
										out.print(x+1);
										out.print("</td>");
										out.print("<td>");
								out.print("<a href=\""+adu.get(x).toString()+"\" class=\"link_normal\">"+adhl.get(x).toString()+"</a>");
								out.print("</td>");
								out.print("</tr>");
							  }
							%>
								</table>
					</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	<br>
	</body>
</html>
