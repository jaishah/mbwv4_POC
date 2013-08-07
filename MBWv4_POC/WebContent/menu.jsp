<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="java.text.*" %>
<%@ page import="com.flowbuilder.utils.Globals"%>
<%@ page import="com.flowbuilder.utils.Configuration" %>
<%@ page import="java.io.File"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Main Menu</title>
</head>
<body>
<h2>Main Menu</h2>
<%
Configuration cfg=Globals.cfg;   
File file = new File(cfg.getProperty("log.file.dir"));
if(file.exists()){
    //System.out.println("File Exists");
}else{
    boolean wasDirecotyMade = file.mkdirs();
    //if(wasDirecotyMade)System.out.println("Direcoty Created");
    //else System.out.println("Sorry could not create directory");
}
%>
<p>1. <a href="OpenSRSDomainSearch" >Domain Search</a></p>
<p>2. <a href="DomainSettings" >Domain Security</a></p>
<p>3. <a href="DNSsettings" >DNS settings</a></p>
<p>4. <a href="OpenSRSMailTest" >MailBox creation</a></p>
<p>5. <a href="OpenSRSRevokeDomain" >Cancel domain</a></p>
</body>
</html>