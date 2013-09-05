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
 * Servlet to revoke domains
 * Servlet implementation class RevokeDomain
 */
@WebServlet("/RevokeDomain")
public class RevokeDomain extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static boolean cnf_success=false;
	public static String msg="";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RevokeDomain() {
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
        RequestDispatcher rd = request.getRequestDispatcher("jsp/cancelDomain.jsp");
    	rd.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String filePath = getServletContext().getRealPath("\\opensrs\\");
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
	        	result = processCancel(name,filePath);
				out.print(result);
			}
	}
/**
 * Use this method to cancel domains
 * @param name
 * @param filePath
 * @return
 */
	private String processCancel(String name, String filePath) {
		Configuration cfg = Globals.cfg;
		Log log = Globals.log;
		// String name = request.getParameter("name");
		log.write(getLogLevel(), this.getClass().getName() + ":processCancel()");
		String xml = "";
		String status = "";
		try {
			xml = OpenSRSXMLUtil.readXMLFile(filePath + "\\revoke.xml");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		xml = OpenSRSXMLUtil.replaceEx(xml, "{$term$}", name);
		String resp = OpenSRSXMLUtil.callOSRS(xml);
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser parser = factory.newSAXParser();
			InputSource is = new InputSource(new StringReader(resp));
			is.setSystemId(filePath + "/opensrs");
			parser.parse(is, new OpenSRSHandler());
		} catch (ParserConfigurationException e) {
			log.write(getLogLevel(), this.getClass().getName()
					+ "ParserConfig error");
		} catch (SAXException e) {
			log.write(getLogLevel(), this.getClass().getName()
					+ "SAXException : xml not well formed");
		} catch (IOException e) {
			log.write(getLogLevel(), this.getClass().getName() + "IO error");
		}

		StringBuffer sbuff = new StringBuffer();
		if (cnf_success) {
			sbuff.append("Domain " + name + " revoked successfully.");
		} else {
			sbuff.append("Domain cannot be revoked.");
		}

		sbuff.append("<br><br><a href=\"menu.jsp\">Back</a>");
		cnf_success = false;
		return sbuff.toString();
	}
	protected int getLogLevel() {
		  return new Integer(Globals.cfg.getProperty("debug.com.poc.opensrs.RevokeDomain")).intValue();
		}
}
