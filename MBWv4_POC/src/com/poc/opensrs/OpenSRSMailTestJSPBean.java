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
public class OpenSRSMailTestJSPBean extends JSPBean {
    
    private String _term;
    private String _mode;
    private String _resp;
    private String _step;
    private String _user;
    private String _name;
    private String _password;
    private String _aliases;
    private String _forward;
    
    public void setMode(String val) {_mode=val;}
    public String getMode() {return _mode;}
    
    public void setTerm(String val) {_term=val;}
    public String getTerm() {return _term;}
    
    public void setResp(String val) {_resp=val;}
    public String getResp() {return _resp;}

    public void setUser(String val) {_user=val;}
    public String getUser() {return _user;}    
    
    public void setStep(String val) {_step=val;}
    public String getStep() {return _step;}
	public String getName() {
		return _name;
	}
	public void setName(String _name) {
		this._name = _name;
	}
	public String getPassword() {
		return _password;
	}
	public void setPassword(String _password) {
		this._password = _password;
	}
	public String getAliases() {
		return _aliases;
	}
	public void setAliases(String _aliases) {
		this._aliases = _aliases;
	}
	public String getForward() {
		return _forward;
	}
	public void setForward(String _forward) {
		this._forward = _forward;
	}    
}
