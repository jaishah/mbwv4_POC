<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<jsp:useBean id="domain" scope="session"
             class="com.poc.opensrs.DomainNameActionForm"/>
<title>DNS Settings</title>
<style>
		.tdipclass {
			padding-left:20px;
		}
		.borderright {
			border-right: 1px dotted  #000
		}
		.closeimg {
			padding-left:5px;
			background:url(static/images/closed.png) no-repeat top left;
			display: inline-block;
			height: 16px;
			width: 16px;
		}
		.dmntable {
			border-bottom:1px solid;
		}
		.txtrecord , .aaarecord, .fwdrecord, .arecord, .mxrecord{
			display:table-row;
		}
	</style>
	<script type="text/javascript" src="javascript/jquery-1.7.1.min.js"></script>
	<script type="text/javascript" src="javascript/dns.js"></script>
	 <%
String res ="";
if(request.getAttribute("success")!=null){
res = (String)request.getAttribute("success");} %> 
</head>
<body>

<div style="height:40px;background-color:#282C36"> 
		<table width="100%" style="color:#FFF">
			<tr>
				<td> DNS </td>
				<td style="float:right"> Help </td>
			</tr>
		</table>
	</div>
	<br />
	<br />
	<br />
	<hr>
	<form action="DNSsettings" method="post" id="dns">
	<input type="hidden" id="event" name="event" value="savedns" />
	<table  border="0" style="padding:10px 0;" class="dmntable">
		<tr>
			<td class="borderright"> 
				<table width="360" height="110" border="0" cellpadding="10" >
					<tr>
						<td> 
							<b> Domain : </b>  <br />
							${domain.domainName}
						</td>
					</tr>
				</table>
			</td>
			<td>
			
			   <table  width="930" cellpadding="10" cellspacing="10" style="margin-left:30px;">
				   <tr class="txtrecord">
						<td class="borderright"> 
							<b> Record: </b>  <br >
							<span class="closeimg"></span> TXT Record
						</td>
						<td class="tdipclass">
							<b> Text: </b>  <br >
							<input type="text" id="txt" name="txt" value="${domain.text}"/>
						</td>
				    </tr>
				    <tr class="fwdrecord">
						 <td class="borderright"> 
							<b> Record: </b>  <br >
							<span class="closeimg"></span> Forward 
						 </td>
						<td class="tdipclass">
							<b> Destination URL: </b>  <br >
							<input type="text" />
						</td>
				    </tr>
				    <tr class="aaarecord">
						 <td class="borderright"> 
							<b> Record: </b>  <br >
							<span class="closeimg"> </span> AAAA Record
						 </td>
						<td class="tdipclass">
							<b> IP Address: </b>  <br >
						
							<input type="text" id="ipv6" name="ipv6" value="${domain.ipv6}"/>
						</td>
				    </tr>
				    <tr class="mxrecord">
						 <td class="borderright"> 
							<b> Record: </b>  <br >
							<span class="closeimg"> </span> MX Record
						 </td>
						<td class="tdipclass">
							<table>
								<tr>
									<td> 
										<b> IP Address: </b>  <br >
											<input type="text" id="mx_host" name="mx_host" value="${domain.mx_hostname}"/>
									</td>
									<td>
										<b> Priority: </b>  <br >
										<input type="text" id="mx_prity" name="mx_prity" value="${domain.mx_priority}"/>
									</td>
								</tr>
							</table>
						</td>
				    </tr>
				    <tr class="arecord">
						<td class="borderright"> 
							<b> Record: </b>  <br >
							<span class="closeimg"> </span> A Record
						</td>
						<td class="tdipclass">
							<b> IP Address: </b>  <br >
							<input type="text" id="a_ip" name="a_ip"  value=""/>
						</td>
				   </tr>
				   <tr>
						<td colspan="2"  style="padding-top:20px;border-top: 1px dotted  #000;">
							<select class="recordselect">
								 <option value="Add New Record">Add New Record </option>
								  <option value="AAAA Record">AAAA Record</option>
								  <option value="MX Record">MX Record </option>
								  <option value="TXT Record">TXT Record </option>
								  <option value="Forward">Forward</option>
								  <option value="Masked Forward">Masked Forward</option>
							</select>
						</td>
				   </tr>
			   </table>
		  </td>
		</tr>
	</table>
	<table  border="0" style="padding:20px 0;float:right">
		<tr>
			<td> 
				<input type="button" id="clear" value="CLEAR" />
			</td>
			<td> 
				<input type="submit" value="SAVE DNS SETTINGS" />
			</td>
		</tr>
	</table></form>
	<hr>
	<table  border="0" style="padding:20px 0;">
		<form action="DNSsettings" method="post">
		<tr>
		<td> 
				<input type="submit" value="ADD SUBDOMAIN" />
			</td>
			<td> 
				<input name="subdomain" id="subdomain" type="text" />
			</td>
			<td> 
				.${domain.domainName}
			</td><td><h3><%=res%></h3></td>
		</tr></form>
	</table>

<p><a href="menu.jsp">Back</a></p>	
</body>
</html>