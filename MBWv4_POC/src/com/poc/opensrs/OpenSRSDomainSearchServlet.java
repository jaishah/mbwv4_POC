/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poc.opensrs;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.flowbuilder.frameworks.JSPBean;
import com.flowbuilder.frameworks.JSPServlet;
import com.flowbuilder.utils.Configuration;
import com.flowbuilder.utils.Globals;
import com.flowbuilder.utils.Log;

/**
 *
 * @author Administrator
 */
public class OpenSRSDomainSearchServlet extends JSPServlet {
	public static List<DomainNameActionForm> domainExact = new ArrayList<DomainNameActionForm>();
	public static List<DomainNameActionForm> domainSuggested = new ArrayList<DomainNameActionForm>();
	public static List<String> subDomains = new ArrayList<String>();
	public static String orderId="";
	public static boolean cnf_success=false;
	public static boolean is_success=false;
	public static boolean is_available = true;
	public static String filePath ;
    protected String get(HttpServletRequest request, HttpServletResponse response,
			    HttpSession sess, HashMap urlParams, JSPBean jspBean) throws Exception
    {
        Configuration cfg=Globals.cfg;                
        Log log=Globals.log;                
        OpenSRSDomainSearchJSPBean oBean = (OpenSRSDomainSearchJSPBean)jspBean;                
        oBean.setMode("stg");//(String)urlParams.get("mode"));
        oBean.setStep("search");    
        if ((String)urlParams.get("step")!=null) oBean.setStep((String)urlParams.get("step"));
        
        oBean.setTerm((String)urlParams.get("term"));    
        
        log.write(getLogLevel(), this.getClass().getName()+":get() ");

        return null;
    }

    protected String post(HttpServletRequest request, HttpServletResponse response,
			    HttpSession sess2, HashMap urlParams, JSPBean jspBean) throws Exception
    {
    	response.setContentType("text/html");
		response.setHeader("Cache-Control", 
			"no-cache, no-store, must-revalidate, pre-check=0, post-check=0, max-age=0, private,  must-revalidate"); // HTTP 1.1.
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
		response.setDateHeader("Expires", -1); // Proxies.
        Configuration cfg=Globals.cfg;                
        Log log=Globals.log;                
        filePath = getServletContext().getRealPath("\\opensrs\\");
        log.write(getLogLevel(), this.getClass().getName()+":post()");
        
        OpenSRSDomainSearchJSPBean oBean = (OpenSRSDomainSearchJSPBean)jspBean;                
        
        String xml=null;
        String step=null;
        PrintWriter out = response.getWriter();
        if (request.getParameter("event") != null
				&& request.getParameter("event").equalsIgnoreCase("register")) {
			if (request.getParameter("domainName") != null) {
				String domainName = request.getParameter("domainName");
				processXmlRegister(domainName);
				if(is_success){
					String ordId=orderId.trim();
					processXmlCnfRegistration(ordId,domainName);
				if(cnf_success){
					xml=domainName+" created successfully.";
					cnf_success=false;
					step="search";
					}
				else if(cnf_success==false){
					xml=domainName+" was not created.";
					cnf_success=false;
					step="search";
				}
				orderId = "";
				is_success=false;
				}
			}
			oBean.setXml(xml);
	        oBean.setStep(step);
			return null;
		} 
       if (oBean.getStep().equals("search")) {
            
            //send name_suggest xml        
          //  xml = readXMLFile("c:\\bep\\opensrs\\name_suggest.xml");            
            //substitute variables
            xml = processXmlSearch(oBean,oBean.getTerm());//replaceEx(xml, "{$term$}", oBean.getTerm());
            step = "next";
        }
        else {
            if (oBean.getStep().equals("next")) {            
            //send sw_register xml               
            xml = readXMLFile(filePath+"\\sw_register.xml");            
            //substitute variables
            xml = replaceEx(xml, "{$term$}", oBean.getTerm());
            step = "register";
            } 
            else {
                if (oBean.getStep().equals("register")) {
                    //send process_pending xml               
                    xml = readXMLFile(filePath+"\\process_pending.xml");            
                    //substitute variables
                    xml = replaceEx(xml, "{$term$}", oBean.getTerm());                    
                    step = "finalise";                                    
                }
                else {
                    if (oBean.getStep().equals("finalise")) {
                        //send create_dns_zone xml               
                        xml = readXMLFile(filePath+"\\create_dns_zone.xml");            
                        //substitute variables
                        xml = replaceEx(xml, "{$term$}", oBean.getTerm());
                        oBean.setDomain(oBean.getTerm());
                        step = "confirm";                                    
                    }    
                    else {
                        if (oBean.getStep().equals("confirm")) {
                            //send get_dns_zone xml to retrieve existing configuration               
                            xml = readXMLFile(filePath+"\\get_dns_zone.xml");            
                            //substitute variables
                            xml = replaceEx(xml, "{$term$}", Globals.cfg.getProperty("opensrs.hosting.domain"));                            
                            callOSRS(oBean, log, xml);                            
                            //now get returned XML and build new XML for call to set_dns_zone
                            log.write(getLogLevel(), oBean.getXml());                            
                            xml = processXMLSubDomain(oBean.getXml(), oBean.getTerm());                            
                            step = "end";                                    
                        }                      
                    }
                                                      
                }                
            
            }
        }
        
        log.write(getLogLevel(), "xml : "+xml);        
        //callOSRS(oBean, log, xml);oBean
        oBean.setXml(xml);
        oBean.setStep(step);
        
        return null;
    }
private String processXmlSearch(OpenSRSDomainSearchJSPBean oBean,String name){
	Log log=Globals.log;  
	log.write(getLogLevel(), this.getClass().getName()+":post()");
	filePath = getServletContext().getRealPath("\\opensrs\\");
		try{
		String xml=readXMLFile(filePath+"\\name_suggest.xml");
		xml=replaceEx(xml, "{$term$}", name);
		callOSRS(oBean, log, xml);
		SAXParserFactory factory = SAXParserFactory.newInstance();
		
		try {
			SAXParser parser = factory.newSAXParser();
			InputSource is =new InputSource(new StringReader(oBean.getXml()));
			is.setSystemId(filePath+"/opensrs");
			parser.parse(is, new OpenSRSHandler());
		} catch (ParserConfigurationException e) {
			log.write(getLogLevel(), this.getClass().getName()+" ParserConfig error");
		} catch (SAXException e) {
			log.write(getLogLevel(), this.getClass().getName()+" SAXException : xml not well formed");
		} catch (IOException e) {
			log.write(getLogLevel(), this.getClass().getName()+" IO error");
		}
		}
		catch(Exception e){
			log.write(getLogLevel(), this.getClass().getName()+"Exception:" + e.getMessage());
		}
		StringBuffer sbuff = new StringBuffer();
		if(is_success){
		
		sbuff.append("<h3>Exact Matches</h3>" +
				"<table width=\"100%\" >");
		Iterator<DomainNameActionForm> exact  = domainExact.iterator();
		Iterator<DomainNameActionForm> suggest  = domainSuggested.iterator();
		int i=0;
		if(!domainExact.isEmpty()){
			while(exact.hasNext()){
				DomainNameActionForm key = exact.next();
				i++;
				sbuff.append("<tr height=\"40\"><td width=\"430\" id=\"");
						sbuff.append("exactName"+i+"\">");
				sbuff.append(key.getDomainName()+"</td>");
				if(key.getStatus().equalsIgnoreCase("available")){
				sbuff.append("<td align=\"right\" width=\"122\">" +
						"<span >Available</span></td> <td align=\"right\">" +
						"<a  href=\"#\" id='' onclick=\"fnregister('");
				sbuff.append(key.getDomainName()+"')\">Register</a></td></tr>");}
				else if(key.getStatus().equalsIgnoreCase("taken"))
					sbuff.append("<td align=\"right\" width=\"122\">" +
							"<span >Not Available</span></td><td align=\"right\">");
			}}
		else{
			sbuff.append("<tr><td><p id='srchError' >Your search returned no exact matches. Please try the suggested domain names.</p></td></tr>");
		}
		i=0;
		sbuff.append("</table><br/>");
		sbuff.append("<h3>Suggested Alternatives</h3><table width=\"100%\" >");
		if(!domainSuggested.isEmpty()){
		while (suggest.hasNext()){
			DomainNameActionForm key = (DomainNameActionForm)suggest.next(); 
			i++;
			sbuff.append("<tr height=\"40\"><td width=\"430\" id=\"");
			sbuff.append("suggestedName"+i+"\">");
			sbuff.append(key.getDomainName()+"</td>");
			if(key.getStatus().equalsIgnoreCase("available")){
				sbuff.append("<td align=\"right\" width=\"122\">" +
						"<span >Available</span></td><td align=\"right\">" +
						"<a  href=\"#\" id='' onclick=\"fnregister('");
				sbuff.append(key.getDomainName()+"')\">Register</a></td></tr>");}
				else if(key.getStatus().equalsIgnoreCase("taken"))
					sbuff.append("<td align=\"right\" width=\"122\">" +
							"<span>Not Available</span></td><td align=\"right\">");
		}}
		else{
			sbuff.append("<tr><td><p id='srchErrorsuggest' >Your search returned no suggested matches. Please try again.</p></td></tr>");
		}
		sbuff.append("</table>");
		
		}
		if((domainExact.isEmpty()&&domainSuggested.isEmpty())){
			sbuff.setLength(0);
			sbuff.append("<p id='srchError' >Your search returned no results. Please try again.</p>");
		}
		domainExact.clear();
		domainSuggested.clear();
		is_success=false;
		return sbuff.toString();
	}
    public static String processXMLSubDomain(String xml, String subDomain) throws Exception {
        
        Document doc=XMLtoDOM(xml);
        
        //Remove unwanted response tags or change for re-submission
        
        NodeList nl=doc.getElementsByTagName("item");
        
        ArrayList nodesToProcess=new ArrayList();
        
        for (int i=0;i<nl.getLength();i++) {
            Element el=(Element)nl.item(i);            
            nodesToProcess.add(el);
        }
        
        //now process arraylist
        Iterator it = nodesToProcess.iterator();
        while (it.hasNext()) {            
            Element el=(Element)it.next();
            if (el.getAttribute("key")!=null && el.getAttribute("key").equalsIgnoreCase("response_text")) {
                el.getParentNode().removeChild(el);
            }
            if (el.getAttribute("key")!=null && el.getAttribute("key").equalsIgnoreCase("action")) {                
                Element action = doc.createElement("item");
                action.setAttribute("key", "action");
                action.appendChild(doc.createTextNode("SET_DNS_ZONE"));
                el.getParentNode().appendChild(action);
                el.getParentNode().removeChild(el);
            }
            if (el.getAttribute("key")!=null && el.getAttribute("key").equalsIgnoreCase("nameservers_ok")) {
                el.getParentNode().removeChild(el);
            }
            if (el.getAttribute("key")!=null && el.getAttribute("key").equalsIgnoreCase("response_code")) {
                el.getParentNode().removeChild(el);
            }
            if (el.getAttribute("key")!=null && el.getAttribute("key").equalsIgnoreCase("is_success")) {
                el.getParentNode().removeChild(el);
            }
            if (el.getAttribute("key")!=null && el.getAttribute("key").equalsIgnoreCase("records")) {
                //add domain record beforehand
                Element domain = doc.createElement("item");
                domain.setAttribute("key", "domain");
                domain.appendChild(doc.createTextNode(Globals.cfg.getProperty("opensrs.hosting.domain")));
                el.getParentNode().appendChild(domain);
            }
            //check for 'A' record - if found then process new subdomain
            if (el.getAttribute("key")!=null && el.getAttribute("key").equalsIgnoreCase("A")) {
                processARecord(el, subDomain);
            }                    
        }   
                       
        return DToS(doc);
        
    }
    
    public static void processARecord(Element aRecord, String subDomain) {
        
        //Get existing sub-domain items
        Element dtArray = (Element)aRecord.getElementsByTagName("dt_array").item(0);                
        //Add new subdomain record
        Element newEl = aRecord.getOwnerDocument().createElement("item");        
        int idx=0;
        //work out next index number
        for (int i=0;i<dtArray.getChildNodes().getLength();i++) {
            try {
                Element item = (Element)dtArray.getChildNodes().item(i);
                if (item.getNodeName().equalsIgnoreCase("item")) {
                    int itemIdx = new Integer(item.getAttribute("key")).intValue();
                    if (itemIdx>idx) idx=itemIdx;
                }
            }
            catch (Exception e) {}
        }        
            
        newEl.setAttribute("key", ""+(idx+1));        
        Element dtAssoc = aRecord.getOwnerDocument().createElement("dt_assoc");       
        Element sd = aRecord.getOwnerDocument().createElement("item");
        sd.setAttribute("key", "subdomain");
        sd.appendChild(aRecord.getOwnerDocument().createTextNode(subDomain));
        dtAssoc.appendChild(sd);
        Element ip = aRecord.getOwnerDocument().createElement("item");
        ip.setAttribute("key", "ip_address");
        ip.appendChild(aRecord.getOwnerDocument().createTextNode(Globals.cfg.getProperty("opensrs.hosting.ip")));
        dtAssoc.appendChild(ip);        
        newEl.appendChild(dtAssoc);
        dtArray.appendChild(newEl);
    
    }
    
    private void callOSRS(OpenSRSDomainSearchJSPBean oBean, Log log, String xml ) {
        
        String propPrefix="prod";
             
        if (oBean.getMode().equalsIgnoreCase("stg")) propPrefix="stage";

        String privateKey = Globals.cfg.getProperty("opensrs."+propPrefix+".key");
        String userName = Globals.cfg.getProperty("opensrs."+propPrefix+".user");
        String host=Globals.cfg.getProperty("opensrs."+propPrefix+".server");
        int port = new Integer(Globals.cfg.getProperty("opensrs."+propPrefix+".port")).intValue();
        
        log.write(getLogLevel(), "host : "+host);
        
        OpenSRSHttpClient sslclient = new OpenSRSHttpClient(host,port,userName,privateKey);
        try {
            String resp = sslclient.sendRequest(xml);            
            log.write(getLogLevel(), "resp : "+resp);
            oBean.setXml(resp);            
        }
        catch (Exception e) {
            log.printStackTrace(e);
        }   
    }
    
	// isPageAllowed - returns a boolean that indicates whether the user is allowed
    // to use this page without being logged in
    protected boolean isPageAllowed() {
      return true;
    }

    // getJSPBeanClass - returns an instance of the JSPBean class required
    protected Class getJSPBeanClass() throws ClassNotFoundException {
      return Class.forName("com.poc.opensrs.OpenSRSDomainSearchJSPBean");
    }

    // getJSPName - returns the name of the jsp file for this servlet
    protected String getJSPName() {
      return "/osrsdomainsearch";
    }

	//====================================================================================
	//              HELPER METHODS
	//====================================================================================

	// get the log level for this ejb
	protected int getLogLevel() {
	  return new Integer(Globals.cfg.getProperty("debug.com.poc.opensrs.OpenSRSDomainSearchServlet")).intValue();
	}

  private static String readXMLFile(String path) throws Exception {
        
        FileInputStream fis=new FileInputStream(path);

        byte[] byts=new byte[fis.available()];
        fis.read(byts);                                
        return new String(byts);                                
   
    }
    
    public static Document XMLtoDOM(String xmlStream) throws IOException, SAXException {
        // create a new input source based around a character stream
        // of the xml to be parsed
        InputSource is =
            new InputSource(
            new BufferedReader(new CharArrayReader(xmlStream.toCharArray())));
        is.setSystemId(filePath+"/opensrs");//C:\\MBW v3.2 and CodeCleanup\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp0\\wtpwebapps\\Business_Hub\\xml\\opensrs\\");
        // create new DOM parser
        org.apache.xerces.parsers.DOMParser parser = new org.apache.xerces.parsers.DOMParser();
        // parse the xml
        //parser.setExternalSchemaLocation(schema);
//        parser.setIncludeIgnorableWhitespace(false);
        //parser.setFeature("http://xml.org/sax/features/validation", true);
        parser.parse(is);
        return parser.getDocument();
    }

    public static StringBuffer DOMtoXML(Element elem, StringBuffer xmlStr) {

                int i;
                int j;
                NodeList nlist;

                // check for child node
                if (elem.hasChildNodes()) {

                        // get list of child nodes and process each one
                        nlist = elem.getChildNodes();
                        for (i=0; i<nlist.getLength(); i++) {

                                Node nd = nlist.item(i);

                                // deal with elements
                                if (nd.getNodeType()==Node.ELEMENT_NODE) {

                                        // open the tag and add the name
                                        xmlStr.append("<"+nd.getNodeName());

                                        // write out attributes for this element
                                        for (j=0; j<nd.getAttributes().getLength(); j++) {
                                                xmlStr.append(" "+nd.getAttributes().item(j).getNodeName());
                                                xmlStr.append("=\""+replaceEx(nd.getAttributes().item(j).getNodeValue(), "\"", "&quot;"));
                                                xmlStr.append("\"");
                                        }

                                        // close the tag
                                        xmlStr.append(">");

                                        // deal with self node
                                        DOMtoXML((Element)nd, xmlStr);

                                        // add closing tag
                                        xmlStr.append("</"+nd.getNodeName()+">");
                                }

                                // deal with text nodes
                                else if (nd.getNodeType()==Node.TEXT_NODE) {

                                        xmlStr.append(normalize(nd.getTextContent()));
                                }
                        }
                }
                return xmlStr;
        }
    public static String normalize(String s) {
            StringBuffer b = new StringBuffer();
            if (s==null) return s;
            int len = s.length();
            for (int i = 0; i < len; i++) {
                char ch = s.charAt(i);
                switch (ch) {
                case '<': {
                    b.append("&lt;");
                    break;
                }
                case '>': {
                    b.append("&gt;");
                    break;
                }
                case '&': {
                    b.append("&amp;");
                    break;
                }
                case '"': {
                    b.append("&quot;");
                    break;
                }
                default: {
                    b.append(ch);
                }
                }
            }
            return b.toString();
        }

    public static final String replaceEx(String within ,String find, String replace) {

      int len = find.length();
      int found = within.indexOf(find);
      int nxtPos = 0;
      if (found==-1) return within;
      StringBuffer newStr = new StringBuffer();
      while (found > -1) {
          newStr.append(within.substring(nxtPos,found));
          newStr.append(replace);
          nxtPos = found + len;
          found = within.indexOf(find, nxtPos);
      }
      newStr.append(within.substring(nxtPos,within.length()));
      return newStr.toString();
    }
    
    public static String DToS(Document doc) {
        String xmlString;
        StringWriter stringOut = new StringWriter();
        if(doc!=null){
        OutputFormat opfrmt = new OutputFormat(doc, "UTF-8", true);
        opfrmt.setIndenting(true);
        opfrmt.setPreserveSpace(false);
        opfrmt.setLineWidth(500);
        XMLSerializer serial = new XMLSerializer(stringOut, opfrmt);
        try{
        serial.asDOMSerializer();
        serial.serialize( doc );
        xmlString = stringOut.toString();
        } catch(java.io.IOException ioe){
        xmlString=null;
        }
        } else xmlString=null;
        return xmlString;        
        
    }        
	private void processXmlRegister(String name){
		 Log log=Globals.log;  
		String user = Globals.cfg.getProperty("opensrs.reg.username");
		String pwd= Globals.cfg.getProperty("opensrs.reg.password");
		try{
			String xml=OpenSRSXMLUtil.readXMLFile(filePath+"\\sw_register.xml");
		xml=OpenSRSXMLUtil.replaceEx(xml, "{$term$}", name);
		xml=OpenSRSXMLUtil.replaceEx(xml, "{$user$}", user);
		xml=OpenSRSXMLUtil.replaceEx(xml, "{$pwd$}", pwd);
		String resp=OpenSRSXMLUtil.callOSRS(xml);
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser parser = factory.newSAXParser();
			InputSource is =new InputSource(new StringReader(resp));
			is.setSystemId(filePath+"/opensrs");
			parser.parse(is, new OpenSRSHandler());
		} catch (ParserConfigurationException e) {
			log.printStackTrace(e);
		} catch (SAXException e) {
			log.printStackTrace(e);
		} catch (IOException e) {
			log.printStackTrace(e);
		}
		}
		catch(Exception e){
			log.printStackTrace(e);
		}
		}
	
	private void processXmlCnfRegistration(String ordId,String domainName){
		Log log=Globals.log;  
		try{
		String pending_xml=OpenSRSXMLUtil.readXMLFile(filePath+"\\process_pending.xml");
		pending_xml=OpenSRSXMLUtil.replaceEx(pending_xml, "{$term$}", ordId);
		String resp=OpenSRSXMLUtil.callOSRS(pending_xml);
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser parser = factory.newSAXParser();
			InputSource is =new InputSource(new StringReader(resp));
			is.setSystemId(filePath+"/opensrs");
			parser.parse(is, new OpenSRSHandler());
			if(cnf_success){
				processCreateDNS(domainName);
			}
			
		} catch (ParserConfigurationException e) {
			log.printStackTrace(e);
		} catch (SAXException e) {
			log.printStackTrace(e);
		} catch (IOException e) {
			log.printStackTrace(e);
		}}
		catch(Exception e){
			log.printStackTrace(e);
		}
		if(is_success){
			is_success=false;
		}
	}
	
	private void processCreateDNS(String domainName){
		Log log=Globals.log;  
		try{
		String create_dns_xml=OpenSRSXMLUtil.readXMLFile(filePath+"\\create_dns_zone.xml");
		create_dns_xml=OpenSRSXMLUtil.replaceEx(create_dns_xml, "{$term$}", domainName);
		String resp=OpenSRSXMLUtil.callOSRS(create_dns_xml);
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser parser = factory.newSAXParser();
			InputSource is =new InputSource(new StringReader(resp));
			is.setSystemId(filePath+"/opensrs");
			parser.parse(is, new OpenSRSHandler());
		} catch (ParserConfigurationException e) {
			log.printStackTrace(e);
		} catch (SAXException e) {
			log.printStackTrace(e);
		} catch (IOException e) {
			log.printStackTrace(e);
		}}
		catch(Exception e){
			log.printStackTrace(e);
		}
	}
	
	
}