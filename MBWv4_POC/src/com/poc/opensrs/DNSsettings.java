package com.poc.opensrs;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.flowbuilder.utils.Configuration;
import com.flowbuilder.utils.Globals;
import com.flowbuilder.utils.Log;

/**
 * Servlet implementation class DNSsettings
 */
@WebServlet("/DNSsettings")
public class DNSsettings extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static String filePath;
	public static ArrayList<String> subDomains = new ArrayList<String>();
	public static String ipv6_rec ;
	public static String host_name ;
	public static String srv_hostname;
	public static String text;
	public static String mx_hostname;
	public static String mx_priority;
	public static boolean is_success = false;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DNSsettings() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		 Configuration cfg=Globals.cfg;                
	        Log log=Globals.log;                
	        log.write(getLogLevel(), this.getClass().getName()+":get()");
	        DomainNameActionForm d = new DomainNameActionForm();
	        String xml="";
	        filePath = getServletContext().getRealPath("\\opensrs\\");
	        HttpSession session= request.getSession();
	        try {
				xml = OpenSRSXMLUtil.readXMLFile(filePath+"\\get_dns_zone.xml");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}            
            //substitute variables
			d.setDomainName(Globals.cfg.getProperty("opensrs.hosting.domain"));
            xml = OpenSRSXMLUtil.replaceEx(xml, "{$term$}", Globals.cfg.getProperty("opensrs.hosting.domain"));                            
          xml = OpenSRSXMLUtil.callOSRS(xml);                            
           // log.write(getLogLevel(),xml);   
            SAXParserFactory factory = SAXParserFactory.newInstance();
    		
    		try {
    			SAXParser parser = factory.newSAXParser();
    			InputSource is =new InputSource(new StringReader(xml));
    			is.setSystemId(filePath + "/opensrs");
    			parser.parse(is, new GetDNSHandler());
    		} catch (ParserConfigurationException e) {
    			log.write(getLogLevel(), this.getClass().getName()+"ParserConfig error");
    		} catch (SAXException e) {
    			log.write(getLogLevel(), this.getClass().getName()+"SAXException : xml not well formed");
    		} catch (IOException e) {
    			log.write(getLogLevel(), this.getClass().getName()+"IO error");
    		}
    		log.write(getLogLevel(),"AAAA: "+ipv6_rec);   
    		log.write(getLogLevel(),"C_NAME: "+host_name);   
    		log.write(getLogLevel(),"SRV: "+srv_hostname);   
    		log.write(getLogLevel(),"TXT: "+text);   
    		log.write(getLogLevel(),"MX host: "+mx_hostname);   
    		log.write(getLogLevel(),"MX priority: "+mx_priority);   
    		d.setIpv6(ipv6_rec);
    		d.setHostname(host_name);
    		d.setSrv_hostname(srv_hostname);
    		d.setText(text);
    		d.setMx_hostname(mx_hostname);
    		d.setMx_priority(mx_priority);
    		session.setAttribute("domain", d);
    		session.setAttribute("get_dns", xml);
	        RequestDispatcher rd = request.getRequestDispatcher("jsp/DNS_settings.jsp");
     	rd.forward(request, response);
	       	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession();
		String get_dns_res = (String)session.getAttribute("get_dns");
		String event = (String) request.getParameter("event");
		RequestDispatcher rd = request
		.getRequestDispatcher("jsp/DNS_settings.jsp");
		if (event != null && event.equalsIgnoreCase("savedns")) {
			processSaveDns(request,response,get_dns_res);
			rd.forward(request, response);
		} else {
			Configuration cfg = Globals.cfg;
			Log log = Globals.log;
			log.write(getLogLevel(), this.getClass().getName() + ":get()");
			DomainNameActionForm d = new DomainNameActionForm();
			OpenSRSDomainSearchServlet.filePath = getServletContext()
					.getRealPath("\\opensrs\\");
			String set_dns_req = "";
			String set_dns_res = "";
			log.write(getLogLevel(), get_dns_res);
			try {
				set_dns_req = OpenSRSDomainSearchServlet.processXMLSubDomain(
						get_dns_res, request.getParameter("subdomain"));
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			PrintWriter print = response.getWriter();
			set_dns_res = OpenSRSXMLUtil.callOSRS(set_dns_req);
			SAXParserFactory factory = SAXParserFactory.newInstance();

			try {
				SAXParser parser = factory.newSAXParser();
				InputSource is = new InputSource(new StringReader(set_dns_res));
				is.setSystemId(filePath + "/opensrs");
				parser.parse(is, new GetDNSHandler());
			} catch (ParserConfigurationException e) {
				log.write(getLogLevel(), this.getClass().getName()
						+ "ParserConfig error");
			} catch (SAXException e) {
				log.write(getLogLevel(), this.getClass().getName()
						+ "SAXException : xml not well formed");
			} catch (IOException e) {
				log.write(getLogLevel(), this.getClass().getName() + "IO error");
			}
			
			if (DNSsettings.is_success) {
				request.setAttribute("success",
						request.getParameter("subdomain")
								+ " subdomain added successfully");
				DNSsettings.is_success = false;
			}
			rd.forward(request, response);
		}
	}

	protected void processSaveDns(HttpServletRequest request,
			HttpServletResponse response, String xml) {
		HttpSession session = request.getSession();
		Configuration cfg = Globals.cfg;
		Log log = Globals.log;
		log.write(getLogLevel(), this.getClass().getName()
				+ ":processSaveDns()");
		DomainNameActionForm d = (DomainNameActionForm) session
				.getAttribute("domain");
		// String get_dns_res = (String) session.getAttribute("get_dns");
		/*
		 * filePath = getServletContext() .getRealPath("\\opensrs\\"); try { xml
		 * = OpenSRSXMLUtil.readXMLFile(filePath+"\\set_dns_zone.xml"); } catch
		 * (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 * //d.setDomainName(Globals.cfg.getProperty("opensrs.hosting.domain"));
		 * xml = OpenSRSXMLUtil.replaceEx(xml, "{$term$}",
		 * Globals.cfg.getProperty("opensrs.hosting.domain"));
		 */
		OpenSRSDomainSearchServlet.filePath = getServletContext().getRealPath(
				"\\opensrs\\");
		// log.write(getLogLevel(), xml);
		try {
			if (request.getParameter("txt") != null
					&& !(request.getParameter("txt").trim()
							.equalsIgnoreCase(""))) {
				xml = OpenSRSXMLUtil.processSaveDNS(xml,
						request.getParameter("txt"), "TXT", filePath);
				d.setText(request.getParameter("txt"));
			}
			if (request.getParameter("ipv6") != null
					&& !(request.getParameter("ipv6").trim()
							.equalsIgnoreCase(""))) {
				xml = OpenSRSXMLUtil.processSaveDNS(xml,
						request.getParameter("ipv6"), "AAAA", filePath);
				d.setIpv6(request.getParameter("ipv6"));
			}
			if (request.getParameter("mx_host") != null
					&& !(request.getParameter("mx_host").trim()
							.equalsIgnoreCase(""))) {
				xml = OpenSRSXMLUtil.processSaveDNS(xml,
						"H"+request.getParameter("mx_host"), "MX", filePath);
				d.setMx_hostname(request.getParameter("mx_host"));
			}
			if (request.getParameter("mx_prity") != null
					&& !(request.getParameter("mx_prity").trim()
							.equalsIgnoreCase(""))) {
				xml = OpenSRSXMLUtil.processSaveDNS(xml,
						"P"+request.getParameter("mx_prity"), "MX", filePath);
				d.setMx_priority(request.getParameter("mx_prity"));
			}
			if (request.getParameter("c_host") != null
					&& !(request.getParameter("c_host").trim()
							.equalsIgnoreCase(""))) {
				xml = OpenSRSXMLUtil.processSaveDNS(xml,
						request.getParameter("c_host"), "CNAME", filePath);
				d.setHostname(request.getParameter("c_host"));
			}
			log.write(getLogLevel(), xml);

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		/*
		 * if(request.getParameter("txt")==null||request.getParameter("txt").trim
		 * ().equalsIgnoreCase("")){ xml=OpenSRSXMLUtil.deleteParameter(xml,
		 * "TXT",filePath); }
		 * if(request.getParameter("ipv6")==null||request.getParameter
		 * ("ipv6").trim().equalsIgnoreCase("")){
		 * xml=OpenSRSXMLUtil.deleteParameter(xml, "AAAA",filePath); }
		 * if(request
		 * .getParameter("mx_host")==null||request.getParameter("mx_host"
		 * ).trim().equalsIgnoreCase("")){
		 * xml=OpenSRSXMLUtil.deleteParameter(xml, "MX",filePath); }
		 * if(request.getParameter
		 * ("mx_prity")==null||request.getParameter("mx_prity"
		 * ).trim().equalsIgnoreCase("")){
		 * xml=OpenSRSXMLUtil.deleteParameter(xml, "MX",filePath); }
		 * if(request.getParameter
		 * ("a_ip")==null||request.getParameter("a_ip").trim
		 * ().equalsIgnoreCase("")){ xml=OpenSRSXMLUtil.deleteParameter(xml,
		 * "A",filePath); }
		 */
session.setAttribute("domain", d);
		xml = OpenSRSXMLUtil.callOSRS(xml);
		// log.write(getLogLevel(), get_dns_res);
	}
	protected int getLogLevel() {
		return new Integer(
				Globals.cfg.getProperty("debug.com.poc.opensrs.DNSsettings"))
				.intValue();
	}
}
