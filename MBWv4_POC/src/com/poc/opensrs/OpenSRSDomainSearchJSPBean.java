/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poc.opensrs;

import com.flowbuilder.frameworks.JSPBean;

/**
 *
 * @author Administrator
 */
public class OpenSRSDomainSearchJSPBean extends JSPBean  {
 
    private String _term;
    private String _mode;
    private String _xml;
    private String _step;
    private String _domain;
    
    public void setMode(String val) {_mode=val;}
    public String getMode() {return _mode;}
    
    public void setTerm(String val) {_term=val;}
    public String getTerm() {return _term;}
    
    public void setXml(String val) {_xml=val;}
    public String getXml() {return _xml;}

    public void setStep(String val) {_step=val;}
    public String getStep() {return _step;}
    
    public void setDomain(String val) {_domain=val;}
    public String getDomain() {return _domain;}
    
    
}
