package com.poc.opensrs;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class OpenSRSHandler extends DefaultHandler{
	DomainNameActionForm domainTmpExact1 = new DomainNameActionForm();
	boolean bfname = false;
	boolean bfstatus = false;
	boolean orderId = false;
	boolean lookUp = false;
	boolean confirm_response =false;
	boolean success =false;
	boolean create = false;
	boolean expire = false;
	boolean lock = false;
	boolean whois = false;
	boolean park = false;
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		if (qName.startsWith("item")
				&& attributes.getValue("key").equalsIgnoreCase("lookup")) {
			lookUp = true;
		}
		if (qName.startsWith("item")
				&& attributes.getValue("key").equalsIgnoreCase("suggestion")) {
			lookUp = false;
		}
		if (qName.startsWith("item")
				&& attributes.getValue("key").equalsIgnoreCase("domain")) {
			bfname = true;
		}
		if (qName.startsWith("item")
				&& attributes.getValue("key").equalsIgnoreCase("status")) {
			bfstatus = true;
		}
		if (qName.startsWith("item")
				&& attributes.getValue("key").equalsIgnoreCase("id")) {
			orderId = true;
		}
		if (qName.startsWith("item")
				&& attributes.getValue("key").equalsIgnoreCase("response_text")) {
			confirm_response = true;
		}
		if (qName.startsWith("item")
				&& attributes.getValue("key").equalsIgnoreCase("is_success")) {
			success = true;
		}
		if (qName.startsWith("item")
				&& attributes.getValue("key").equalsIgnoreCase("registry_createdate")) {
			create = true;
		}
		if (qName.startsWith("item")
				&& attributes.getValue("key").equalsIgnoreCase("registry_expiredate")) {
			expire = true;
		}
		if (qName.startsWith("item")
				&& attributes.getValue("key").equalsIgnoreCase("lock_state")) {
			lock = true;
		}
		if (qName.startsWith("item")
				&& attributes.getValue("key").equalsIgnoreCase("state")) {
			whois = true;
		}
		if (qName.startsWith("item")
				&& attributes.getValue("key").equalsIgnoreCase("parkp_status")) {
			park = true;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (qName.startsWith("item") && domainTmpExact1.getDomainName() != null
				&& !domainTmpExact1.getDomainName().equalsIgnoreCase("")) {
			bfstatus = true;
		}
	}


	@Override
	public void characters(char ch[], int start, int length)
			throws SAXException {
		DomainNameActionForm domainTmpExact = new DomainNameActionForm();
		String tmpValue = "";
		if(whois){
			whois=false;
			tmpValue = new String(ch, start, length);
			DomainSettings.whois=tmpValue;
		}
		if(lock){
			lock=false;
			tmpValue = new String(ch, start, length);
			DomainSettings.lock_state=tmpValue;
		}
		if(create){
			create=false;
			tmpValue = new String(ch, start, length);
			DomainSettings.createDate=tmpValue;
		}
		if(expire){
			expire=false;
			tmpValue = new String(ch, start, length);
			DomainSettings.expiryDate=tmpValue;
		}
		if(orderId){
			tmpValue = new String(ch, start, length);
			OpenSRSDomainSearchServlet.orderId = tmpValue;
			orderId=false;
		}
		if (bfname) {
			domainTmpExact1.setDomainName(new String(ch, start, length));
			bfname = false;
		}

		if (bfstatus) {
			tmpValue = new String(ch, start, length);
			domainTmpExact1.setStatus(tmpValue);
			bfstatus = false;

		}
		if(park){
			park=false;
			tmpValue = new String(ch, start, length);
			DomainSettings.parkKp=tmpValue;
		}
		if (domainTmpExact1.getDomainName() != null
				&& !domainTmpExact1.getDomainName().equalsIgnoreCase("")) {
			if (domainTmpExact1.getStatus() != null
					&& !domainTmpExact1.getStatus().trim().equalsIgnoreCase("")) {
				domainTmpExact.setDomainName(domainTmpExact1.getDomainName());
				domainTmpExact.setStatus(domainTmpExact1.getStatus());
				if(lookUp)
					OpenSRSDomainSearchServlet.domainExact.add(domainTmpExact);
				else if(lookUp==false){
					if(OpenSRSDomainSearchServlet.domainSuggested.size()<10)
						OpenSRSDomainSearchServlet.domainSuggested.add(domainTmpExact);
				}
				domainTmpExact1.setDomainName("");
				domainTmpExact1.setStatus("");
			}
		}
		
		if(confirm_response){
			tmpValue = new String(ch, start, length);
			if(tmpValue.startsWith("Domain registration successfully"))
			OpenSRSDomainSearchServlet.cnf_success=true;
				if(tmpValue.contains("Lock enabled")){
					DomainSettings.lock_state="1";
				}else if(tmpValue.contains("Lock disabled")){
					DomainSettings.lock_state="0";
				}
				if(tmpValue.contains("successfully enabled")){
					DomainSettings.whois="enabled";
				}else if(tmpValue.contains("successfully disabled")){
					DomainSettings.whois="disabled";
				}
			confirm_response=false;
		}
		
	
		if(success){
			tmpValue = new String(ch, start, length);
			if(tmpValue.equalsIgnoreCase("1")){
				OpenSRSDomainSearchServlet.is_success=true;
				success=false;
			}
				
				
		}
		
	}

	@Override
	public void endDocument() throws SAXException {
		
	}
	
}
