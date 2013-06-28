<%@ page import="com.flowbuilder.datastructures.*" %>
<%@ page import="com.flowbuilder.frameworks.*" %>
<%@ page import="com.flowbuilder.utils.*" %>
<%@ page import="com.flowbuilder.ejbs.entities.org.*" %>

<html>
	<head>
		
		<jsp:useBean id="jspBean"  class="com.flowbuilder.processes.listdocs.ListGeneratedDocsJSPBean" scope="session"/>
		<jsp:include page="commonHead.jsp"/>
		<script language="javascript">
			function openPage(showPage)	{


			childWin=open("","child","screenX=512,screenY=384, height=150,width=400,menubar=no,resizable=no,location=no,titlebar=no,toolbar=no,scrollbars=no");
			childWinOpen = true;
			childWin.focus(showPage);
			childWin.document.write('<html>')
			childWin.document.write('<html>')
			childWin.document.write('<head>')
			childWin.document.write('<link href="/styles/bpstyle.css" rel="stylesheet" type="text/css"/>')
			childWin.document.write('<title>buy it now!</title>')
			childWin.document.write('</head>')



			childWin.document.write('<body rightmargin="0" leftmargin="0" topmargin="0" align="center" valign="center">')

			childWin.document.write('<table cellpadding="20" cellspacing="0"><tr><td><img src="/static/images/callNow.jpg" alt="call 0870 850 3824..."  title="call 0870 850 3824..." border="0" ></td></tr>')

			childWin.document.write('</table>')

			childWin.document.write('</body></html>')
			childWin.document.close
			childWin.focus();
			}

		</script>
		<style>
			.row {
				padding-bottom:18px;
			}
			.smallRow {
				padding-bottom:3px;
			}
			.docWindow {
				border-right: 1px solid #000000;
				border-bottom: 1px solid #000000;
				border-top: 1px solid #808080;
				border-left: 1px solid #808080;
			}
		</style>
		<title>Generated Documents</title>
	</head>

	<body >
		<table cellspacing="0" cellpadding="0" width="800px" height="95%" border="0">
			<tr>
				<td style="padding-left:22px;padding-top:20px" width="150px" height="100%" valign="top">
					<table cellspacing="0" cellpadding="0" width="150px" border="0" >
							<tr>
								<td class="title"><%= jspBean.getFlowTitle() %></td>
							</tr>							
							<tr>
								<td style="padding-left:22px;" valign="top">
      								<form name="mainForm" method="post" action="ListGeneratedDocs">
									<input name="sub" type="hidden" value="1">
      		       					<%= jspBean.renderRequestNo() %>
									<table border="0" cellPadding="0" cellSpacing="0" width="100%">
											<tr>
												<td valign="top">
            												<table class="JSPPagingTableBorder" cellPadding="0" cellSpacing="0">
								      						<%
																String docTarget="_blank";
																// get the web app

																// get the table model
																JSPPagingTable table = jspBean.getTable();

																//print column titles
																out.print("\n<tr>");
																out.print("<td onclick=\"document.mainForm.sub.name='col0'; document.mainForm.submit();\" onmouseover=\"doMOver('1',this);\" onmouseout=\"doMOut('1',this);\" class=\"JSPPagingTableColHeader\">document name</td>");
																String[] colNames = table.getColNames();
																			boolean[] colRequired = new boolean[colNames.length];
																		String mimeType[] = new String[colNames.length];

																for (int col=1; col<colNames.length-1; col++) {
																					colRequired[col] = true;
																					// get mime type
																					mimeType[col] = null;
																					if (colNames[col].startsWith("doctype")) {mimeType[col] = colNames[col].substring(7);}
																					else {mimeType[col] = colNames[col];}

																					// look up the title as env entry
																				 String colTitle=mimeType[col];
																					if (colTitle==null) colRequired[col] = false;
																					if (colRequired[col]) {
																		out.print("<td style=\"text-decoration:none;padding-left:5px;padding-right5px;\" align=\"center\" class=\"JSPPagingTableColHeader\">");
																		out.print(colTitle);
																		out.print("</td>");
																	}
																			}
																out.print("</tr>");
																String docURL="/static/html/blank.htm";
																//print table rows
																for (int i=0; i<10; i++) {
																	Object[] rowData  = table.getRow(i);
																	if (rowData !=null) {
																		out.print("<tr>");
																		//session title
																		out.print("<td class=\"JSPPagingCellString\">");
																		out.print(rowData[0]);
																		out.print("</td>");

																		// print document links
																		for (int col=1; col<colNames.length-1; col++) {
																			if (colRequired[col]) {
																				String linkURL = (String)rowData[col];

																				String image = "images/view_button.gif";
																				if (!linkURL.equalsIgnoreCase("NULL") && image!=null) {
																					// genuine link
																					if (i==0) docURL=linkURL;
																					out.print("<td class=\"JSPPagingCellImage\" align=\"center\"><a ");
																					if (!colNames[col].equalsIgnoreCase("doctypetext/html")) {out.print("target=\""+docTarget+"\" ");}
																					//out.print("target=\"docFrame\" ");
																					out.print("href=\"");
																					out.print(linkURL);
																					if (colNames[col].equalsIgnoreCase("doctypeapplication/pdf")) out.print("#view=fit");
																					out.print("\"><img src=\"/static/");
																					out.print(image);
																					out.print("\" border=\"0\" name=\"view\"/></a>");
																					out.print("</td>");
																				}
																				else {
																					// no link
																					out.print("<td style=\"border-bottom: 1px solid #111111; border-right: 1px solid #111111;\" align=\"center\">");
																					out.print("&nbsp;");
																					out.print("</td>");
																				}
																			}
																					}
																		out.print("</tr>");
																	}
																}
																//paging buttons
																if (table.getNoPages()!=1) {
																	out.print("<tr>");
																	out.print("<td colspan=\"3\" style=\"border-bottom:1px #111111 solid; border-right:1px #111111 solid;\">");
																	out.print("<table width=\"100%\" border=\"0\" cellPadding=\"0\" cellSpacing=\"0\">");
																	out.print("<tr>");
																	if (table.getPageNo()==1) {
																		out.print("\n<td style=\"text-align:right\" width=\"50%\" class=\"selector_na\">&lt;prev</td>");
																	} else {
																		out.print("<td onmouseover=\"doMOver('2',this);\" onmouseout=\"doMOut('2',this);\" onclick=\"document.mainForm.sub.name='prev'; document.mainForm.submit();\" class=\"selector\" width=\"50%\" style=\"text-align:right\">&lt;prev</td>");
																	}
																	String cssClass = null;
																	for (int k=1; k<=table.getNoPages(); k++) {
																		if (k!=table.getPageNo()) {cssClass = "selector";}
																		else	{cssClass = "selector_current";}
																		out.print("\n<td ");
																		if (k!=table.getPageNo()) {
																			out.print("onclick=\"document.mainForm.sub.name='page");
																			out.print(k);
																			out.print("'; document.mainForm.submit();\"");
																		}
																		out.print(" class=\"");
																		out.print(cssClass);
																		out.print("\" style=\"text-align:center\"");
																		if (k!=table.getPageNo())
																			out.print(" onmouseover=\"doMOver('2',this);\" onmouseout=\"doMOut('2',this);\"");
																		out.print(">");
																		out.print(k);
																		out.print("</td>");
																	}
																	if (table.getPageNo()==table.getNoPages()) {
																		out.print("\n<td width=\"50%\" style=\"text-align:left\" class=\"selector_na\">next&gt;</td>");
																	} else {
																		out.print("<td onmouseover=\"doMOver('2',this);\" onmouseout=\"doMOut('2',this);\" onclick=\"document.mainForm.sub.name='next'; document.mainForm.submit();\" class=\"selector\" width=\"50%\" style=\"text-align:left\">next&gt;</td>");
																	}
																	out.print("</tr>");
																	out.print("</table>");
																	out.print("</td>");
																	out.print("</tr>");
																}
															%>																																
      					  								</table>
      												</td>
													<td style="padding-left:0px;" valign="bottom" align="left">
														<table border="0" cellPadding="0" cellSpacing="0" width="100%">
															<%   String editUrl = jspBean.getEdit();
					    											if (jspBean.getFinalise()) {
					        											String csid = jspBean.getCsid(); %>
    															<tr>
      															<td style="padding-top:3px;" align="right">
					        										<%



					        											out.print("<a href=\"");
   																  out.print("GeneratingDocuments?url=ListGeneratedDocs?retainjsp=true&mode=finalise&csid=");
																		out.print(csid);
																			}
																			out.print("\">"); %>
	          															<img src="/static/images/finalise_button.gif" border="0" alt="finalise"/>
	        															</a>
	      															</td>
	    														</tr>
													</table>
											</td>
										</tr>
									</form>
									</table>
								</td>
							</tr>
							<tr>
								<td class="smallRow"><p class="error"><%= jspBean.renderHTMLLabel("error", null) %></p></td>
							</tr>
							<tr>
								<td class="smallRow" valign="bottom">
									<img src="images/hr.gif" width="100%" height="1px;"></img>
								</td>
							</tr>
							<!--<tr>
								<td colspan="2" class="smallRow" style="padding-left:5px;padding-top:5px;">
									<table cellpadding="0" cellspacing="0" border="0">
										<tr>
											<td width="95%">
												<p class="normal_small_grey">Remember, although this service allows you to produce tailored documents and guidance, you may wish to have these checked by a professional adviser to confirm they apply to your particular circumstances. For more information, please see the service <a class="link_normal" href="html/disclaimer.htm" target="body">disclaimer</a> or alternatively <a href="Feedback" target="_Blank" class="link_normal">contact us</a>.</p>
											</td>
											<td>&nbsp;</td>
										</tr>
									</table>
								</td>
							</tr>-->
						</table>
					</td>
					
				</tr>
 		 </table>
	</body>
</html>
