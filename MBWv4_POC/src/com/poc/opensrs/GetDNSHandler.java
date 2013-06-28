package com.poc.opensrs;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;


public class GetDNSHandler extends DefaultHandler {
	boolean sub_d = false;
	boolean a_rec = false;
	boolean success = false;
	boolean mx_rec = false;
	boolean srv_rec = false;
	boolean txt_rec = false;
	boolean cname_rec = false;
	boolean aaaa_rec = false;
	boolean ipv6_rec = false;
	boolean host_name = false;
	boolean srv_hostname = false;
	boolean text = false;
	boolean mx_hostname = false;
	boolean mx_priority = false;

	public GetDNSHandler() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) {
		if (qName.equalsIgnoreCase("item")
				&& attributes.getValue("key").equalsIgnoreCase("A")) {
			a_rec = true;
		}
		if (qName.equalsIgnoreCase("item")
				&& attributes.getValue("key").equalsIgnoreCase("MX")) {
			mx_rec = true;
		}
		if (qName.equalsIgnoreCase("item")
				&& attributes.getValue("key").equalsIgnoreCase("SRV")) {
			srv_rec = true;
		}
		if (qName.equalsIgnoreCase("item")
				&& attributes.getValue("key").equalsIgnoreCase("TXT")) {
			txt_rec = true;
		}
		if (qName.equalsIgnoreCase("item")
				&& attributes.getValue("key").equalsIgnoreCase("AAAA")) {
			aaaa_rec = true;
		}
		if (qName.equalsIgnoreCase("item")
				&& attributes.getValue("key").equalsIgnoreCase("CNAME")) {
			cname_rec = true;
		}
		if (a_rec) {
			if (qName.equalsIgnoreCase("item")
					&& attributes.getValue("key").equalsIgnoreCase("subdomain")) {
				sub_d = true;
			}
		}
		if (cname_rec) {
			if (qName.equalsIgnoreCase("item")
					&& attributes.getValue("key").equalsIgnoreCase("hostname")) {
				host_name = true;
				cname_rec=false;
			}
			
		}
		if (mx_rec) {
			if (qName.equalsIgnoreCase("item")
					&& attributes.getValue("key").equalsIgnoreCase("hostname")) {
				mx_hostname = true;
				mx_rec=false;
			}
			if (qName.equalsIgnoreCase("item")
					&& attributes.getValue("key").equalsIgnoreCase("priority")) {
				mx_priority = true;
			}
		}
		if (srv_rec) {
			if (qName.equalsIgnoreCase("item")
					&& attributes.getValue("key").equalsIgnoreCase("hostname")) {
				srv_hostname = true;
				srv_rec=false;
			}
			
		}
		if (txt_rec) {
			if (qName.equalsIgnoreCase("item")
					&& attributes.getValue("key").equalsIgnoreCase("text")) {
				text = true;
			}
		}
		if (aaaa_rec) {
			if (qName.equalsIgnoreCase("item")
					&& attributes.getValue("key").equalsIgnoreCase(
							"ipv6_address")) {
				ipv6_rec = true;
			}
		}
		if (qName.equalsIgnoreCase("item")
				&& attributes.getValue("key").equalsIgnoreCase("is_success")) {
			success = true;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) {
		if (sub_d) { //A record subdomains
			String tmpVal = new String(ch, start, length);
			DNSsettings.subDomains.add(tmpVal);
		}
		if (text) { //TXT record
			String tmpVal = new String(ch, start, length);
			DNSsettings.text=(tmpVal);
			text=false;
		}
		if (ipv6_rec) { //AAAA record
			String tmpVal = new String(ch, start, length);
			DNSsettings.ipv6_rec=(tmpVal);
			ipv6_rec=false;
		}
		if (srv_hostname) { //SRV record
			String tmpVal = new String(ch, start, length);
			DNSsettings.srv_hostname=(tmpVal);
			srv_hostname=false;
		}
		if (mx_priority) { //MX priority
			String tmpVal = new String(ch, start, length);
			DNSsettings.mx_priority=(tmpVal);
			mx_priority=false;
		}
		if (mx_hostname) { //MX hostname
			String tmpVal = new String(ch, start, length);
			DNSsettings.mx_hostname=(tmpVal);
			mx_hostname=false;
		}
		if (host_name) { //CNAME hostname
			String tmpVal = new String(ch, start, length);
			DNSsettings.host_name=(tmpVal);
			host_name=false;
		}
		
		if (success) {
			String tmpValue = new String(ch, start, length);
			if (tmpValue.equalsIgnoreCase("1")) {
				DNSsettings.is_success = true;
				success = false;
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) {
		if (qName.equalsIgnoreCase("item")) {
			sub_d = false;
		}
	}

	@Override
	public void endDocument() {
	}
}