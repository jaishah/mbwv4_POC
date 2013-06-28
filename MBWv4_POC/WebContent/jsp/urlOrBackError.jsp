<html>
	<head>
		<%@ page import="com.flowbuilder.utils.*" %>
		<%@ page import="com.flowbuilder.datastructures.*" %>
		<jsp:include page="commonHead.jsp"/>
		<%

			BrowserDS bds = (BrowserDS)session.getAttribute("BROWSER_OBJECT");

			if (bds==null) {

				bds = BEPROUtils.setBrowser(request);

			}
			

		%>

		<script language="JavaScript" type="text/javascript" src="javascript/<%=bds.getMenuJS(BEPROUtils.getWebApp())%>"/></script>
		<title>URL Overtype/Back button Error</title>
	</head>

	<body onmouseover="checkMenu(<%=bds.getEventDefinitionString()%>);">
		<div align="center">			
			<%@ page import="com.flowbuilder.frameworks.*" %>
			<table align="center" class="position" border="0" cellPadding="0" cellSpacing="0" width="65%">
				<tr>
					<td style="padding-left:0px;"/>
						<table border="0" cellPadding="0" cellSpacing="0" width="100%">
							<tr>
								<td class="title">Important</td>
							</tr>
							<tr>
								<td style="padding-bottom:20px;">
									<table border="0" cellPadding="5" cellSpacing="0">
										<tr style="padding-bottom:10px;">
											<td class="normal" colspan="2">bepro has detected one of the following conditions:-</td>
										</tr>
										<tr>
											<td width="6"><img src="images/bullet.gif"></td>
											<td class="normal">The browser's 'back' button has been used</td>
										</tr>
										<tr>
											<td><img src="images/bullet.gif"></td>
											<td class="normal">The browser's URL line has been over-typed</td>
										</tr>
										<tr>
											<td><img src="images/bullet.gif"></td>
											<td class="normal">A hyperlink or button has been double-clicked</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td style="padding-bottom:30px;">
									<p class="error">bepro has restricted these facilities for security reasons and you should refrain from attempting to use them.</p>
									<p class="normal">You can navigate fully throughout the system with the menus and buttons provided on the page.</p>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
			<br/>
			<br/>
			<jsp:include page="common_navigation.jsp"/>
		</div>
	</body>
</html>
