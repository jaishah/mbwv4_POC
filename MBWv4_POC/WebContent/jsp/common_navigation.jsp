<%@ page import="com.flowbuilder.utils.*" %>
<%@ page import="com.flowbuilder.datastructures.*" %>
   
<%
	// get the nav object
	NavigationControl nc = (NavigationControl)session.getAttribute("NAVIGATION_CONTROL");

	if (nc!=null) {
		// the bar
				 		 		

		String backHTML = null;
		String nextHTML = null;

		if (nc!=null) {
				backHTML = "<a href=\"Back\"><img src=\"/static/images/back_button.gif\" border=\"0\"/></a>";
		}
		if (nc.backType==1) {
			 backHTML = "<a href=\"" + (nc.backURL) + "\"><img src=\"/static/images/back_button.gif\" border=\"0\"/></a>";
		}	
		if (nc.backType==2) {
			 backHTML = "<input type=\"image\" name=\"prev\" src=\"/static/images/back_button.gif\"/>";
		}

		if (nc.nextType==1) {
			 backHTML = "<a href=\"" + (nc.nextURL) + "\"><img src=\"images/next_button.gif\" border=\"0\"/></a>";
		}
		if (nc.nextType==2) {
			 backHTML = "<input type=\"image\" name=\"next\" src=\"/static/images/next_button.gif\"/>";
		}
%>

		<!-- FOR FULL SPAN NAV BAR - CHANGE WIDTH OF THIS TABLE TO EQUAL 100% -->
		<table border="0" cellpadding="0" cellspacing="0" width="66%">
			<tr>
				<td style="padding-left:0px; padding-top:15px;padding-bottom:5px;" valign="middle">
					<table border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td width="5%" class="cnav" height="20px">&#160;</td>	
							<% if ((nc!=null) || (nc.backType==1)  || (nc.backType==2)) { %>
								<td class="cnav">
									<table border="0" cellpadding="0" cellspacing="0">
										<tr>
											<td><%= backHTML %></td>
										</tr>
									</table>
								</td>
							<% } %>
							<% if ((nc.nextType==1) || (nc.nextType==2)) { %>
								<td vspace="0" hspace="0" valign="middle" class="cnav" style="padding-left:3px;">
									<table border="0" cellpadding="0" cellspacing="0">
										<tr>
											<td><%= nextHTML %></td>
										</tr>
									</table>
								</td>
							<% } %>
							<!-- FOR FULL SPAN NAV BAR - CHANGE WIDTH OF THIS TD & IMAGE TO EQUAL 100% -->
							<td class="cnav" height="22px" width="85%">&#160;</td>
							<td><img src="images/cnav_slant.gif"/></td>
						</tr>
					</table>
				</td>
			</tr>
		</table>

<%}%>