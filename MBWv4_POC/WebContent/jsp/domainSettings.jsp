<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Domain Settings</title>
</head>
<jsp:useBean id="domain" scope="request"
             class="com.poc.opensrs.DomainNameActionForm"/>
<body>
<div>
<form method="post" action="DomainSettings">
<input type="hidden" name="event" id="event"value="info" />
Domain name:<input type="text" name="name" id="name"/><br><br>
<input type="submit" value="Get domain info">
</form></div>
<br><br>
<div>
<form method="post" action="DomainSettings">
<input type="hidden" name="event" id="event" value="view_sec" />
Domain name:<input type="text" name="name" id="name"/><br><br>
<input type="submit" value="View domain security">
</form>

<%if(domain.getDomainName()!=null){ %>
<div>
<br>
<p>Security settings of ${domain.domainName} </p>
<form method="post" action="DomainSettings">
<input type="hidden" value="${domain.domainName}" name="name" id="name"/>
<p><input type="hidden" name="event" id="event" value="modify" />
<% if(domain.getStatus_sec().equalsIgnoreCase("1")){%>
			&nbsp;<input checked id="lock" name="lock" type="checkbox" value="lock"/>Domain lock</p>
			<%}else{ %>&nbsp;<input id="lock" name="lock" type="checkbox" value="lock"/>Domain lock</p><%} %>
<% if(domain.getWhois().equalsIgnoreCase("enabled")){%>
		<p>
			&nbsp;<input checked id="privacy" name="privacy" type="checkbox" value="private"/>Contact privacy</p>
			<%}else{ %>
			&nbsp;<input id="privacy" name="privacy" type="checkbox" value="private"/>Contact privacy</p><%} %>
			<input type="submit" value="Modify domain info"></form></div>
</div>
<%}else{ %><p>Domain name should not be blank.</p><%} %>
<p><a href="menu.jsp">Back</a></p>
</body>
</html>