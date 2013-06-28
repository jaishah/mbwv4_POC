<html>
<head>
  <%@ page import="com.flowbuilder.processes.startsession.*" %>
  <%@ page import="com.flowbuilder.frameworks.*" %>
  <%@ page import="com.flowbuilder.utils.*" %>
  <%@ page import="java.util.*" %>
  <%@ page import="java.text.*" %>
  <jsp:useBean id="jspBean"  class="com.flowbuilder.processes.startsession.EditExistingJSPBean" scope="session"/>
  <jsp:include page="commonHead.jsp"/>
	<% StartSessionProcessBean pb = (StartSessionProcessBean)session.getAttribute("PROCESS_BEAN_START_SESSION"); %>
	<script language="JavaScript" type="text/javascript" src="javascript/<%=jspBean.getBrowser().getMenuJS(BEPROUtils.getWebApp())%>"/></script>
  <% if (pb.getMode().equalsIgnoreCase("clone")) out.print(jspBean.renderJSSetFocus("mainForm", false)); %>
  <title>beprofessional</title>
</head>

<% if (pb.getMode().equalsIgnoreCase("clone")) { %>
	<body rightmargin="0" onmouseover="checkMenu(<%=jspBean.getBrowser().getEventDefinitionString()%>);" onload="setFocus()">
<% } else { %>
	<body rightmargin="0" onmouseover="checkMenu(<%=jspBean.getBrowser().getEventDefinitionString()%>);">
<% } %>

<jsp:include page="productmenu.jsp"/>

<table class="position" cellspacing="0" cellpadding="0" width="95%" border="0">
	<tr>
    <td style="padding-bottom:15px;">
    <% if (pb.getMode().equalsIgnoreCase("edit")) {
    	out.print("<p class=\"title2\">edit existing session</p>");
       }
       else {
    	out.print("<p class=\"title2\">copy existing session</p>");
       }
    %>
    </td>
	</tr>
	<tr>
		<td>
      <form name="mainForm" action="EditExisting" method="post">
      	<table cellPadding="0" cellSpacing="1" width="65%">
      		<tr>
      			<td style="padding-bottom:20px;">
        		<% if (pb.getMode().equalsIgnoreCase("edit")) { %>
        			<p class="normal">Here is a list of all the sessions you have run.
        			To edit any of the following simply click on the title of your session
        			and you will be taken back into it exactly where you left off.  Scroll through any others you may
        			have created with the previous and next links at the bottom of the page.
        			To sort by a particular column, simply click on the column header.</p>
        		<%}%>
        		<% if (pb.getMode().equalsIgnoreCase("clone")) { %>
        			<p class="normal">Here is a list of all the sessions you have finalised.
        			To copy one, simply provide a new name and click on the title of the session
        			you wish to copy. You will be taken into your new session with all answers taken from the
        			copied session.  Scroll through any others you may
        			have created with the previous and next links at the bottom of the page.
        			To sort by a particular column, simply click on the column header.</p>
        		<%}%>
      		</td>
      	</tr>
				<tr>
      		<td class="error"><%= jspBean.renderHTMLLabel("error", null) %>&nbsp;</td>
      	</tr>
				<tr>
					<td>
        		<%= jspBean.renderHTMLPagingTable(jspBean.getTable(),null,"JSPPagingTableColHeader",0,"mainForm",null,null,null,null,new int[]{0}, false) %>
      		</td>
      	</tr>
      	<% if (pb.getMode().equalsIgnoreCase("clone")) {

				  	String nameLabel = "";
				  	if (jspBean.isFlowNeedingKey()) {
						nameLabel="Name";
					%>
						<tr><td class="error" style="padding-top:20px;">
						This session requires entry of an authorisation key.
						If you have been issued with such a key, paste it into the box below, then proceed as normal.
						If you do not have an authorisation key, please contact your reseller or the <b>be</b>professional helpdesk.
						</td></tr>
						<tr>
							<td class="primary_blue" style="padding-top:5px">Key</td>
						</tr>
						<tr>
							<td class="normal"><%= jspBean.renderHTMLInput("flowKey", "", 70, 255) %></td>
						</tr>
				    <% }

					out.print("<tr><td style=\"padding-top:15px;\">");
					out.print("<p class=\"primary_blue\">New Session Name</p>");
					out.print("</td></tr>");
					out.print("<tr><td>");
					out.print(jspBean.renderHTMLInput("title", "", 20, 255));
					out.print("</td></tr>");
               }
            %>
            <%if (jspBean.getMoreThanMaxRows()==true) {
            	out.print("<tr><td><p style=\"padding-top: 18px;\" class=\"error\">");
            	out.print("You have created more than "+jspBean.getMaxPageSize()+" sessions, shown here are the latest. Please use my<b>be</b>professional if you wish to search for earlier sessions.");
      	out.print("</p></td></tr>");

            }
            %>
      	</table>
			</form>
		</td>
	</tr>
</table>
<br/>
<br/>
<br/>
<table cellpadding="0" cellspacing="0" border="0" class="position_nav" valign="bottom" width="100%" height="22">
	<tr>
		<td>
			<jsp:include page="common_navigation.jsp"/>
		</td>
	</tr>
</table>
</body>
</html>
