/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poc.opensrs;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import com.flowbuilder.frameworks.JSPBean;
import com.flowbuilder.frameworks.JSPServlet;
import com.flowbuilder.utils.Configuration;
import com.flowbuilder.utils.Globals;
import com.flowbuilder.utils.Log;

/**
 *
 * @author Administrator
 */
public class OpenSRSMailTestServlet extends JSPServlet {

    public static void main(String[] args) {
        JSONObject credentials = new JSONObject();
        JSONObject children = new JSONObject();
        
        children.element("user", "admin@bcsg.adm");
        children.element("password", "abc123!");                
        credentials.element("credentials", children);    
        //credentials.element("domain", "testbcsg.com");        
        JSONObject attributes = new JSONObject();
        //attributes.element("workgroup", "development");        
        //credentials.element("attributes", attributes);
        credentials.element("user", "philb@pmbtesting1.com");        
        credentials.element("reason", "testing SSO");        
        credentials.element("type", "sso");        
        credentials.element("duration", "1");        
        
        
        System.out.println(credentials.toString());

        //OpenSRSHttpClient sslclient = new OpenSRSHttpClient("admin.test.hostedemail.com/api/change_domain",80,"admin@bcsg.adm","abc123!");
        OpenSRSHttpClient sslclient = new OpenSRSHttpClient("admin.test.hostedemail.com/api/generate_token",80,"admin@bcsg.adm","abc123!");
        try {
            String resp = sslclient.sendJSONRequest(credentials);
            JSONObject jResp = (JSONObject) JSONSerializer.toJSON( resp );
            String token = jResp.getString("token");
            
            System.out.println(parseJSON(resp));
                    
        }
        catch (Exception e) {
            e.printStackTrace();
        }      
        
    }
    
    @Override
    protected String post(HttpServletRequest request, HttpServletResponse response, HttpSession sess, HashMap urlParams, JSPBean jspBean) throws Exception {
        
        Configuration cfg=Globals.cfg;                
        Log log=Globals.log;                

        log.write(getLogLevel(), this.getClass().getName()+":post()");
        
        OpenSRSMailTestJSPBean oBean = (OpenSRSMailTestJSPBean)jspBean;                

        String propPrefix="stage";
        if (oBean.getMode().equalsIgnoreCase("stg")) propPrefix="stage";

        String pwd = cfg.getProperty("opensrs."+propPrefix+".mail.pwd");
        String userName = cfg.getProperty("opensrs."+propPrefix+".mail.user");
        String host=cfg.getProperty("opensrs."+propPrefix+".mail.server");
        int port = new Integer(cfg.getProperty("opensrs."+propPrefix+".mail.port")).intValue();
                
        //JSON Stuff
        //try and create a domain
        JSONObject credentials = new JSONObject();
        JSONObject children = new JSONObject();
        
        children.element("user", userName);
        children.element("password", pwd);                
        credentials.element("credentials", children);    
        
        if (oBean.wasButtonPressed("SSO")) {
            String username = oBean.getUser()+"@"+oBean.getTerm();
            credentials.element("user",username);        
            credentials.element("reason", "testing SSO");        
            credentials.element("type", "sso");        
            credentials.element("duration", "1");        
            
            System.out.println(credentials.toString());
            //perform generate_token
            host+="/generate_token";
            
            OpenSRSHttpClient sslclient = new OpenSRSHttpClient(host,port,userName,pwd);
            try {
                String resp = sslclient.sendJSONRequest(credentials);
                System.out.println(resp);
                oBean.setResp(parseJSON(resp));
                
                JSONObject jResp = (JSONObject) JSONSerializer.toJSON( resp );
                String token = jResp.getString("token");
                
                //response.sendRedirect("https://mail.test.hostedemail.com/mail?user="+oBean.getTerm()+"&pass="+token);
                //RequestDispatcher rd = getServletContext().getRequestDispatcher("https://mail.test.hostedemail.com/mail?user="+oBean.getTerm()+"&pass="+token);
                //rd.forward(request, response);
                oBean.setResp("https://mail.test.hostedemail.com/mail?user="+username+"&pass="+token);
                oBean.setStep("SSO");
            }
            catch (Exception e) {
                log.printStackTrace(e);
            }                
            
            
            
            
            return null;
            
        }
        else {
            if (oBean.getStep().equals("start")) {
        
                //change domain JSON request - creates a domain
                credentials.element("domain", oBean.getTerm());        
                JSONObject attributes = new JSONObject();
                attributes.element("workgroup", "development");        
                credentials.element("attributes", attributes);
                host+="/change_domain";
               
            }
            else {
                if (oBean.getStep().equals("finalise")) {
                    //change user JSON request - creates a user and mailbox
                    credentials.element("user", oBean.getUser()+"@"+oBean.getTerm());
                    String aliases = oBean.getAliases();
                    String[] array =aliases.split(",");
                    String forward = oBean.getForward();
                    String[] fwd = forward.split(",");
                    JSONObject attributes = new JSONObject();
                    attributes.element("name", oBean.getName());        
                    attributes.element("password", oBean.getPassword());      
                    attributes.element("timezone", "Europe/London");
                    if((array.length==1&&!(array[0].trim().equalsIgnoreCase("")))||array.length>1)
                    attributes.element("aliases", array);
                    if((fwd.length==1&&!(fwd[0].trim().equalsIgnoreCase("")))||fwd.length>1)
                    attributes.element("forward_recipients",fwd);
                    credentials.element("attributes", attributes);
                    host+="/change_user";
                }
               
            }
            System.out.println(credentials.toString());


            OpenSRSHttpClient sslclient = new OpenSRSHttpClient(host,port,userName,pwd);
            try {
                String resp = sslclient.sendJSONRequest(credentials);
                String result = parseJSON(resp);
                if(result.equalsIgnoreCase("true")){
                	if(oBean.getStep().equals("finalise")){
                		oBean.setName("");
                		oBean.setPassword("");
                		oBean.setForward("");
                		oBean.setAliases("");
                		oBean.setResp("MailBox "+oBean.getUser()+"@"+oBean.getTerm()+" created successfully.");
                		oBean.setUser("");
                		
                	}
                	else if (oBean.getStep().equals("start")){
                		oBean.setUser("");
                		oBean.setName("");
                		oBean.setPassword("");
                		oBean.setForward("");
                		oBean.setAliases("");
                		oBean.setResp("Signed into domain "+oBean.getTerm()+" successfully");
                		 oBean.setStep("finalise");
                	}
                		
                }
                else{
                	oBean.setUser("");
            		oBean.setName("");
            		oBean.setPassword("");
            		oBean.setForward("");
            		oBean.setAliases("");
                	oBean.setResp("Wrong format or bad command");
                }
                

            }
            catch (Exception e) {
                log.printStackTrace(e);
            }                

            return null;            
        }
        
    }

    @Override
    protected String get(HttpServletRequest request, HttpServletResponse response, HttpSession sess, HashMap urlParams, JSPBean jspBean) throws Exception {
                        
        Configuration cfg=Globals.cfg;                
        Log log=Globals.log;                
        log.write(getLogLevel(), this.getClass().getName()+":get()");
        
        OpenSRSMailTestJSPBean oBean = (OpenSRSMailTestJSPBean)jspBean;   
        
        oBean.setMode("stg");//(String)urlParams.get("mode"));
        oBean.setStep("start");      
        String propPrefix="stage";
        if (oBean.getMode().equalsIgnoreCase("stg")) propPrefix="stage";

        String pwd = cfg.getProperty("opensrs."+propPrefix+".mail.pwd");
        String userName = cfg.getProperty("opensrs."+propPrefix+".mail.user");
        String host=cfg.getProperty("opensrs."+propPrefix+".mail.server");
        int port = new Integer(cfg.getProperty("opensrs."+propPrefix+".mail.port")).intValue();
                
        //JSON Stuff
       JSONObject credentials = new JSONObject();
       JSONObject pass = new JSONObject();
       pass.element("user", userName);
       pass.element("password", pwd);                
       credentials.element("credentials", pass); 
        JSONObject children = new JSONObject();
        String[] domain = {"domain"};
        children.element("company", "Business Centric Services Group");
        children.element("match", "*");
        children.element("type", domain);
    credentials.element("criteria", children);
        JSONObject range = new JSONObject();
        range.element("first", "");
        range.element("limit", "");
        JSONObject sort = new JSONObject();
        sort.element("by", "");
        sort.element("direction", "");
        credentials.element("range", range);
        credentials.element("sort", sort);
        host+="/search_domains";
        OpenSRSHttpClient sslclient = new OpenSRSHttpClient(host,port,userName,pwd);
      
        try {
            String resp = sslclient.sendJSONRequest(credentials);
            oBean.setResp(parseJSONdomains(resp));

        }
        catch (Exception e) {
            log.printStackTrace(e);
        }        
        
        return null;
                            
    }

 	// isPageAllowed - returns a boolean that indicates whether the user is allowed
    // to use this page without being logged in
    protected boolean isPageAllowed() {
      return true;
    }

    // getJSPBeanClass - returns an instance of the JSPBean class required
    protected Class getJSPBeanClass() throws ClassNotFoundException {
      return Class.forName("com.poc.opensrs.OpenSRSMailTestJSPBean");
    }

    // getJSPName - returns the name of the jsp file for this servlet
    protected String getJSPName() {
      return "/osrsmailtest";
    }

	//====================================================================================
	//              HELPER METHODS
	//====================================================================================

	// get the log level for this ejb
	protected int getLogLevel() {
	  return new Integer(Globals.cfg.getProperty("debug.com.poc.opensrs.OpenSRSMailTestServlet")).intValue();
	}
        
        public static String parseJSON (String jsonTxt) throws Exception {
            
            JSONObject json = (JSONObject) JSONSerializer.toJSON( jsonTxt );
            String success = json.getString("success");
            return success;
                        
            
        }
        
 public static String parseJSONdomains (String jsonTxt) throws Exception {
            
            JSONObject json = (JSONObject) JSONSerializer.toJSON( jsonTxt );
            JSONObject attributes = json.getJSONObject("domains");
            JSONObject list = attributes.getJSONObject("domain");
            return json.toString();
                        
            
        }
}
