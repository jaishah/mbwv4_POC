package com.poc.opensrs;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.flowbuilder.utils.Configuration;
import com.flowbuilder.utils.Globals;
import com.flowbuilder.utils.Log;

/**
 * Servlet implementation class DomainSettings
 */
@WebServlet("/DomainSettings")
public class DomainSettings extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static String createDate="";
	public static String expiryDate="";
	public static String lock_state="";
	public static String whois="";
	public static String filePath;
	public static String parkKp="";   
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DomainSettings() {
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
	        RequestDispatcher rd = request.getRequestDispatcher("jsp/domainSettings.jsp");
        	rd.forward(request, response);
	       	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		filePath = getServletContext().getRealPath("\\opensrs\\");
		 Configuration cfg=Globals.cfg;                
	        Log log=Globals.log;
	        String result="";
	        response.setContentType("text/html");
			response.setHeader("Cache-Control", 
				"no-cache, no-store, must-revalidate, pre-check=0, post-check=0, max-age=0, private,  must-revalidate"); // HTTP 1.1.
			response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
			response.setDateHeader("Expires", -1); // Proxies.
			PrintWriter out = response.getWriter();
			String name = request.getParameter("name");
			DomainNameActionForm d = new DomainNameActionForm();
			if(name!=null&&name.trim()!=""){
	        if(request.getParameter("event").equalsIgnoreCase("info")){
	        	result = processInfo(name);
				out.print(result);
	        }else if(request.getParameter("event").equalsIgnoreCase("modify")){
	        	System.out.println(request.getParameter("lock"));
	        	System.out.println(request.getParameter("privacy"));
	        	String lock_chk =request.getParameter("lock")!=null?"1":"0";
	        	String privacy_chk = request.getParameter("privacy")!=null?"enable":"disable";
	        	
	        	d = processModify(name,lock_chk,privacy_chk);
	        	d.setDomainName(name);
	        	request.setAttribute("domain", d);
	        	RequestDispatcher rd = request.getRequestDispatcher("jsp/domainSettings.jsp");
	        	rd.forward(request, response);
				//out.print(result);
	        }
	        else if (request.getParameter("event").equalsIgnoreCase("view_sec")){
	       d = processView(name);
	        	//request.setAttribute("domain", d);
	        	d.setDomainName(name);
	        	request.setAttribute("domain", d);
	        	RequestDispatcher rd = request.getRequestDispatcher("jsp/domainSettings.jsp");
	        	rd.forward(request, response);
	        }
			}else{
				request.setAttribute("domain", d);
				RequestDispatcher rd = request.getRequestDispatcher("jsp/domainSettings.jsp");
	        	rd.forward(request, response);
			}
	        
	}

/**
 * Display security settings of a domain	
 * @param name
 * @return
 */
	private DomainNameActionForm processView(String name){
		 Configuration cfg=Globals.cfg;                
	        Log log=Globals.log;
		//String name = request.getParameter("name");
     log.write(getLogLevel(), this.getClass().getName()+":processView()");
     DomainNameActionForm d = new DomainNameActionForm();
     String xml_whois="";
     String xml_status="";
		try {
			xml_whois = OpenSRSXMLUtil.readXMLFile(filePath + "\\view_whois.xml");
			xml_status = OpenSRSXMLUtil.readXMLFile(filePath + "\\view_status.xml");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		xml_whois=OpenSRSXMLUtil.replaceEx(xml_whois, "{$term$}", name);
		xml_status=OpenSRSXMLUtil.replaceEx(xml_status, "{$term$}", name);
    String resp_whois = OpenSRSXMLUtil.callOSRS(xml_whois);
    String resp_status = OpenSRSXMLUtil.callOSRS(xml_status);
    SAXParserFactory factory = SAXParserFactory.newInstance();
		
		try {
			SAXParser parser = factory.newSAXParser();
			InputSource is =new InputSource(new StringReader(resp_whois));
			is.setSystemId(filePath + "/opensrs");
			parser.parse(is, new OpenSRSHandler());
			is.setCharacterStream(new StringReader(resp_status));
			parser.parse(is, new OpenSRSHandler());
		} catch (ParserConfigurationException e) {
			log.write(getLogLevel(), this.getClass().getName()+"ParserConfig error");
		} catch (SAXException e) {
			log.write(getLogLevel(), this.getClass().getName()+"SAXException : xml not well formed");
		} catch (IOException e) {
			log.write(getLogLevel(), this.getClass().getName()+"IO error");
		}
		d.setStatus_sec(lock_state);
		d.setWhois(whois);
		
		return d;
	}

/**
 * Display creation,expiry dates and domain status 	
 * @param name
 * @return
 */
	private String processInfo(String name){
		 Configuration cfg=Globals.cfg;                
	        Log log=Globals.log;
		//String name = request.getParameter("name");
        log.write(getLogLevel(), this.getClass().getName()+":processInfo()");
        String xml="";
        String xml_status="";
        String status = "";
		try {
			xml = OpenSRSXMLUtil.readXMLFile(filePath + "\\get_domain.xml");
			xml_status = OpenSRSXMLUtil.readXMLFile(filePath + "\\view_status.xml");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		xml=OpenSRSXMLUtil.replaceEx(xml, "{$term$}", name);
       String resp = OpenSRSXMLUtil.callOSRS(xml);
       SAXParserFactory factory = SAXParserFactory.newInstance();
  		xml_status=OpenSRSXMLUtil.replaceEx(xml_status, "{$term$}", name);
      String resp_status = OpenSRSXMLUtil.callOSRS(xml_status);
		try {
			SAXParser parser = factory.newSAXParser();
			InputSource is =new InputSource(new StringReader(resp));
			is.setSystemId(filePath + "/opensrs");
			parser.parse(is, new OpenSRSHandler());
			is.setCharacterStream(new StringReader(resp_status));
			parser.parse(is, new OpenSRSHandler());
		} catch (ParserConfigurationException e) {
			log.write(getLogLevel(), this.getClass().getName()+"ParserConfig error");
		} catch (SAXException e) {
			log.write(getLogLevel(), this.getClass().getName()+"SAXException : xml not well formed");
		} catch (IOException e) {
			log.write(getLogLevel(), this.getClass().getName()+"IO error");
		}
		
       if(lock_state.equalsIgnoreCase("1")){
    	   status="Locked";
       }else{
    	   status="Un-Locked";
       }
		StringBuffer sbuff = new StringBuffer();
		sbuff.append("<table width=\"100%\"><tr height=\"40\"><td>Created Date</td><td>Lock State</td></tr>");
		sbuff.append("<tr height=\"40\"><td>"+createDate+"</td><td>"+status+"</td></tr>");
		sbuff.append("<tr height=\"40\"><td>Expiry Date</td><td>Go to site</td></tr>");
		sbuff.append("<tr height=\"40\"><td>"+expiryDate+"</td><td><a href=\"http://www.");
		sbuff.append(name+"\">"+name+"</a></td></tr></table>");
		sbuff.append("<br><a href=\"DomainSettings\">Back</a>");
		
		createDate="";expiryDate="";lock_state="";
return sbuff.toString();
	}

/**
 * Modify contact privacy and lock state of domains
 * @param name
 * @param lock
 * @param privacy
 * @return
 */
	private DomainNameActionForm processModify(String name,String lock,String privacy){
		 Configuration cfg=Globals.cfg;                
	        Log log=Globals.log;
		//String name = request.getParameter("name");
     log.write(getLogLevel(), this.getClass().getName()+":processModify()");
     DomainNameActionForm d = new DomainNameActionForm();
     String xml="";
     String xml_status="";
     String resp_status="";
     String resp_contact="";
		try {
			if(!(lock_state.equalsIgnoreCase(lock))){
				xml_status = OpenSRSXMLUtil.readXMLFile(filePath+"\\modify_status.xml");
				xml_status=OpenSRSXMLUtil.replaceEx(xml_status, "{$term$}", name);
				xml_status=OpenSRSXMLUtil.replaceEx(xml_status, "{$lock$}", lock);
				resp_status = OpenSRSXMLUtil.callOSRS(xml_status);
			}
			if(!(whois.equalsIgnoreCase(privacy+"d"))){
				xml = OpenSRSXMLUtil.readXMLFile(filePath+"\\modify_domain.xml");
				xml=OpenSRSXMLUtil.replaceEx(xml, "{$term$}", name);
				xml=OpenSRSXMLUtil.replaceEx(xml, "{$state$}", privacy);
				resp_contact = OpenSRSXMLUtil.callOSRS(xml);
			}
			if(!resp_status.equalsIgnoreCase("")||!resp_contact.equalsIgnoreCase("")){
				SAXParserFactory factory = SAXParserFactory.newInstance();
				
				try {
					SAXParser parser = factory.newSAXParser();
					InputSource is =new InputSource(new StringReader(resp_contact));
					is.setSystemId(filePath + "/opensrs");
					if(!resp_contact.equalsIgnoreCase(""))
					parser.parse(is, new OpenSRSHandler());
					if(!resp_status.equalsIgnoreCase("")){
					is.setCharacterStream(new StringReader(resp_status));
					parser.parse(is, new OpenSRSHandler());}
				} catch (ParserConfigurationException e) {
					log.write(getLogLevel(), this.getClass().getName()+"ParserConfig error");
				} catch (SAXException e) {
					log.write(getLogLevel(), this.getClass().getName()+"SAXException : xml not well formed");
				} catch (IOException e) {
					log.write(getLogLevel(), this.getClass().getName()+"IO error");
				}
			}
					d.setStatus_sec(lock_state);
					d.setWhois(whois);
					//lock_state="";whois="";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//return "Error";
		}
				return d;
	}
	protected int getLogLevel() {
		  return new Integer(Globals.cfg.getProperty("debug.com.poc.opensrs.DomainSettings")).intValue();
		}
}
