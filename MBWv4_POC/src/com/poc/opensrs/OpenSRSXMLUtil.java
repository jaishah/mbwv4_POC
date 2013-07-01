package com.poc.opensrs;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.flowbuilder.utils.Configuration;
import com.flowbuilder.utils.Globals;
import com.flowbuilder.utils.Log;
public class OpenSRSXMLUtil {
              
	  public static String readXMLFile(String path) throws Exception {
	        
	        FileInputStream fis=new FileInputStream(path);

	        byte[] byts=new byte[fis.available()];
	        fis.read(byts);                                
	        return new String(byts);                                
	   
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
	  
	  public static final String processXMLSubDomain(String xml, String subDomain,String filepath) throws Exception {
	        
	        Document doc=XMLtoDOM(xml,filepath);
	        
	        //Remove unwanted response tags or change for re-submission
	        String propPrefix="PROD";
			String envStage = "stage";//configManager.getProperty(SaaSConfigProperties.PROP_PROV_ENV); 
			 if (envStage.equalsIgnoreCase("stage")) 
				 propPrefix=envStage.toUpperCase();
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
	                domain.appendChild(doc.createTextNode("opensrs.hosting.domain"));
	                el.getParentNode().appendChild(domain);
	            }
	            //check for 'A' record - if found then process new subdomain
	            if (el.getAttribute("key")!=null && el.getAttribute("key").equalsIgnoreCase("A")) {
	                processARecord(el, subDomain);
	            }                    
	        }   
	                       
	        return DToS(doc);
	        
	    }
	  
	  private static void processARecord(Element aRecord, String subDomain) {
		  String propPrefix="PROD";
			String envStage = "stage";//configManager.getProperty(SaaSConfigProperties.PROP_PROV_ENV); 
			 if (envStage.equalsIgnoreCase("stage")) 
				 propPrefix=envStage.toUpperCase();
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
	  public static final String processXMLSubDomainErase(String xml, String subDomain,String filepath) throws Exception {
	        
	        Document doc=XMLtoDOM(xml,filepath);
	        
	        //Remove unwanted response tags or change for re-submission
	        String propPrefix="PROD";
	        String envStage = "stage";//configManager.getProperty(SaaSConfigProperties.PROP_PROV_ENV);
			 if (envStage.equalsIgnoreCase("stage")) 
				 propPrefix=envStage.toUpperCase();
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
	            	processARecordtoErase(el, subDomain);
	            }                    
	        }   
	                       
	        return DToS(doc);
	        
	    }
	  private static void processARecordtoErase(Element aRecord, String subDomain) {
		  String propPrefix="PROD";
		  String envStage = "stage";//configManager.getProperty(SaaSConfigProperties.PROP_PROV_ENV); 
			 Configuration cfg=Globals.cfg;  
		  Log log=Globals.log;
			 if (envStage.equalsIgnoreCase("stage")) 
				 propPrefix=envStage.toUpperCase();
	        //Get existing sub-domain items
	        Element dtArray = (Element)aRecord.getElementsByTagName("dt_array").item(0);                
	        //Add new subdomain record
	        Element newEl = aRecord.getOwnerDocument().createElement("item");        
	        boolean flag = false;
	        int idx=0;
	        //work out next index number
	        for (int i=0;i<dtArray.getChildNodes().getLength();i++) {
	            try {
	                Element item = (Element)dtArray.getChildNodes().item(i);
	                Element itemSubRoot = (Element)item.getChildNodes().item(1);
	                Element  itemSubDomain =(Element) itemSubRoot.getChildNodes().item(1);
	               
	                
	                if (itemSubDomain.getNodeName().equalsIgnoreCase("item")) {
	                	if(itemSubDomain.getAttribute("key").equalsIgnoreCase("subdomain")){
                			String xmlSubdomain = itemSubDomain.getTextContent()+".businesshubweb.com";
                			if(xmlSubdomain.equalsIgnoreCase(subDomain)){
                				System.out.println("subDomain to Erase: "+xmlSubdomain);
                				flag=true;
                				
                			}
                			
                		}
	                }
	                if (item.getNodeName().equalsIgnoreCase("item")&&flag) {
	                    int itemIdx = new Integer(item.getAttribute("key")).intValue();
	                    idx=itemIdx;
	                    flag=false;
	                    dtArray.removeChild(item);
	                 
	                   /* newEl.setAttribute("key", ""+(idx));        
        		        Element dtAssoc = aRecord.getOwnerDocument().createElement("dt_assoc");       
        		        Element sd = aRecord.getOwnerDocument().createElement("item");
        		        sd.setAttribute("key", "subdomain");
        		        sd.appendChild(aRecord.getOwnerDocument().createTextNode(""));
        		        dtAssoc.appendChild(sd);
        		        Element ip = aRecord.getOwnerDocument().createElement("item");
        		        ip.setAttribute("key", "ip_address");
        		        ip.appendChild(aRecord.getOwnerDocument().createTextNode(configManager.getProperty(propPrefix+".opensrs.hosting.ip")));
        		        dtAssoc.appendChild(ip);        
        		        newEl.appendChild(dtAssoc);
        		       // dtArray.appendChild(newEl);
	                    dtArray.replaceChild(newEl,item);*/
	                }
	                
	             
	                
	            }
	            catch (Exception e) {System.out.println("XML Error: " + e.getMessage());}
	        }      
	        for(int j=0;j<dtArray.getChildNodes().getLength();j++){
	        	try {
        		Element itemWhichIndexChanges = (Element)dtArray.getChildNodes().item(j);
	                if(itemWhichIndexChanges.getNodeName().equalsIgnoreCase("item")){
	                	int prevIdx = new Integer(itemWhichIndexChanges.getAttribute("key")).intValue();
	                	if(prevIdx==(idx+1)){
	                		itemWhichIndexChanges.setAttribute("key", Integer.toString(idx));
	                	idx=prevIdx;}
	                }
                
                }
	        	 catch (Exception e) {System.out.println("New Index error: " + e.getMessage());}
        }
	    
	    } 

	  public static Document XMLtoDOM(String xmlStream,String filepath) throws IOException, SAXException {

	        // create a new input source based around a character stream
	        // of the xml to be parsed
		  
		  InputSource is  =
	            new InputSource(
	            new BufferedReader(new CharArrayReader(xmlStream.toCharArray())));
	        // create new DOM parser
		  is.setSystemId(filepath+"/opensrs");
	        org.apache.xerces.parsers.DOMParser parser = new org.apache.xerces.parsers.DOMParser();
	        //parse the xml
	        //parser.setExternalSchemaLocation(schema);
	        //parser.setIncludeIgnorableWhitespace(false);
	        //parser.setFeature("http://xml.org/sax/features/validation", true);
	        parser.parse(is);
	        return parser.getDocument();
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
	  public static String callOSRS(String xml) {
			 Configuration cfg=Globals.cfg;                
		        Log log=Globals.log;
		  String envStage = "stage";//configManager.getProperty(SaaSConfigProperties.PROP_PROV_ENV); 
			  String propPrefix="PROD";
			        String response="";     
			       if (envStage.equalsIgnoreCase("stage")) propPrefix=envStage;

			       String privateKey = Globals.cfg.getProperty("opensrs."+propPrefix+".key");
			        String userName = Globals.cfg.getProperty("opensrs."+propPrefix+".user");
			        String host=Globals.cfg.getProperty("opensrs."+propPrefix+".server");
			        int port = new Integer(Globals.cfg.getProperty("opensrs."+propPrefix+".port")).intValue();
			        
			        System.out.println("host : "+host);
			        
			        OpenSRSHttpClient sslclient = new OpenSRSHttpClient(host,port,userName,privateKey);
			        try {
			             response = sslclient.sendRequest(xml);            
			             log.write(getLogLevel(),"resp : "+response);
			        }
			        catch (Exception e) {
			        	System.out.println("Exception:" + e.getMessage());
			        }   
			        return response;
			    }
	  /*public static String deleteParameter(String xml, String keyName, String filePath) {
		    // <item>
		  Document doc = null;
		  	try {
				doc = XMLtoDOM(xml,filePath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			 NodeList nodes = doc.getElementsByTagName("dt_assoc");
			 String pName="";
			    for (int i = 0; i < nodes.getLength(); i++) {
			      Element person = (Element)nodes.item(i);
			      // <name>
			      NodeList items = person.getElementsByTagName("item");
			      for (int j = 0; j < items.getLength(); j++) {
			    	  Element item = (Element)items.item(j);
			    	   if(item!=null)
					      pName = item.getAttribute("key");
					      if (pName!=null&&pName.equals(keyName)) {
					    	  item.getParentNode().removeChild(item);
					      }
			      }
			    }
			return OpenSRSXMLUtil.DToS(doc);
		  }*/
	  public static final String processSaveDNS(String xml, String data,String keyName,String filepath) throws Exception {
	        
	        Document doc=XMLtoDOM(xml,filepath);
	        
	        //Remove unwanted response tags or change for re-submission
	        String propPrefix="PROD";
			String envStage = "stage";//configManager.getProperty(SaaSConfigProperties.PROP_PROV_ENV); 
			 if (envStage.equalsIgnoreCase("stage")) 
				 propPrefix=envStage.toUpperCase();
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
	            if (el.getAttribute("key")!=null && el.getAttribute("key").equalsIgnoreCase("domain")) {
	                el.getParentNode().removeChild(el);
	            }
	            if (el.getAttribute("key")!=null && el.getAttribute("key").equalsIgnoreCase("records")) {
	                //add domain record beforehand
	                Element domain = doc.createElement("item");
	                domain.setAttribute("key", "domain");
	                domain.appendChild(doc.createTextNode(Globals.cfg.getProperty("opensrs.hosting.domain")));
	                el.getParentNode().appendChild(domain);
	            }
	            
	            //check for records - if found then process new DNS record
			if (keyName.equalsIgnoreCase("A")) {
				if (el.getAttribute("key") != null
						&& el.getAttribute("key").equalsIgnoreCase("A")) {
					processARecord(el, data);
				}
			} else if (keyName.equalsIgnoreCase("TXT")) {

				if (el.getAttribute("key") != null
						&& el.getAttribute("key").equalsIgnoreCase("TXT")) {
					processTxtRecord(el, data);
				}
			} else if (keyName.equalsIgnoreCase("MX")) {
				if (el.getAttribute("key") != null
						&& el.getAttribute("key").equalsIgnoreCase("MX")) {
					processMXRecord(el, data);
				}
			} else if (keyName.equalsIgnoreCase("AAAA")) {
				if (el.getAttribute("key") != null
						&& el.getAttribute("key").equalsIgnoreCase("AAAA")) {
					processAAARecord(el, data);
				}
			}
			else if (keyName.equalsIgnoreCase("CNAME")) {
				if (el.getAttribute("key") != null
						&& el.getAttribute("key").equalsIgnoreCase("CNAME")) {
					processCNameRecord(el, data);
				}
			}
	          /*  if (el.getAttribute("key")!=null && el.getAttribute("key").equalsIgnoreCase("CNAME")) {
	                processCNameRecord(el, data);
	            } 
	             
	            if (el.getAttribute("key")!=null && el.getAttribute("key").equalsIgnoreCase("SRV")) {
	                processSrvRecord(el, data);
	            } */
	        
	        }   
	                       
	        return DToS(doc);
	        
	    }
	  private static void processTxtRecord(Element aRecord, String content) {
		  String propPrefix="PROD";
			String envStage = "stage";//configManager.getProperty(SaaSConfigProperties.PROP_PROV_ENV); 
			 if (envStage.equalsIgnoreCase("stage")) 
				 propPrefix=envStage.toUpperCase();
			 String pName="";
			 NodeList dtArray = aRecord.getElementsByTagName("dt_array");//.item(0);                
	       
			 for (int i = 0; i < dtArray.getLength(); i++) {
			      Element person = (Element)dtArray.item(i);
			      NodeList items = person.getElementsByTagName("item");
			      for (int j = 0; j < items.getLength(); j++) {
			    	  Element item = (Element)items.item(j);
			    	   if(item!=null)
					      pName = item.getAttribute("key");
					      if (pName!=null&&pName.equals("text")) {
					    	  item.setTextContent(content);
					      }  
			      }
			      }
	   	        }  

	private static void processMXRecord(Element aRecord, String content) {
		String propPrefix = "PROD";
		String envStage = "stage";// configManager.getProperty(SaaSConfigProperties.PROP_PROV_ENV);
		if (envStage.equalsIgnoreCase("stage"))
			propPrefix = envStage.toUpperCase();
		String pName = "";
		NodeList dtArray = aRecord.getElementsByTagName("dt_array");// .item(0);
		for (int i = 0; i < dtArray.getLength(); i++) {
			Element person = (Element) dtArray.item(i);
			// <name>
			NodeList items = person.getElementsByTagName("item");
			for (int j = 0; j < items.getLength(); j++) {
				Element item = (Element) items.item(j);
				if (item != null)
					pName = item.getAttribute("key");
				if (content.startsWith("P")) {
					if (pName != null && pName.equalsIgnoreCase("priority")) {
						content = content.substring(1);
						item.setTextContent(content);
					}
				} else if (content.startsWith("H")) {
					if (pName != null && pName.equalsIgnoreCase("hostname")) {
						content = content.substring(1);
						item.setTextContent(content);
					}
				}
			}
		}

	}

	private static void processAAARecord(Element aRecord, String content) {
		String propPrefix = "PROD";
		String envStage = "stage";// configManager.getProperty(SaaSConfigProperties.PROP_PROV_ENV);
		if (envStage.equalsIgnoreCase("stage"))
			propPrefix = envStage.toUpperCase();
		String pName = "";
		NodeList dtArray = aRecord.getElementsByTagName("dt_array");// .item(0);
		for (int i = 0; i < dtArray.getLength(); i++) {
			Element person = (Element) dtArray.item(i);
			NodeList items = person.getElementsByTagName("item");
			for (int j = 0; j < items.getLength(); j++) {
				Element item = (Element) items.item(j);
				if (item != null)
					pName = item.getAttribute("key");
				if (pName != null && pName.equalsIgnoreCase("ipv6_address")) {
					item.setTextContent(content);
				}
			}
		}

	}
	
	private static void processCNameRecord(Element aRecord, String content) {
		String propPrefix = "PROD";
		String envStage = "stage";// configManager.getProperty(SaaSConfigProperties.PROP_PROV_ENV);
		if (envStage.equalsIgnoreCase("stage"))
			propPrefix = envStage.toUpperCase();
		String pName = "";
		NodeList dtArray = aRecord.getElementsByTagName("dt_array");// .item(0);
		for (int i = 0; i < dtArray.getLength(); i++) {
			Element person = (Element) dtArray.item(i);
			NodeList items = person.getElementsByTagName("item");
			for (int j = 0; j < items.getLength(); j++) {
				Element item = (Element) items.item(j);
				if (item != null)
					pName = item.getAttribute("key");
				if (pName != null && pName.equalsIgnoreCase("ipv6_address")) {
					item.setTextContent(content);
				}
			}
		}

	}
		protected static int getLogLevel() {
			  return new Integer(Globals.cfg.getProperty("debug.com.poc.opensrs.OpenSRSXMLUtil")).intValue();
			}
		
}
