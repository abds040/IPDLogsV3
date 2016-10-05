/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wwag.iped.ipdlogsv3;
import de.wwag.iped.ipdlogsv3.tools.DBBean;
import de.wwag.iped.ipdlogsv3.tools.FileBean;
import de.wwag.iped.ipdlogsv3.tools.HTTPBean;
import de.wwag.iped.ipdlogsv3.tools.JsonTransformer;
import de.wwag.iped.ipdlogsv3.tools.Logr;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import spark.ModelAndView;
import spark.Request;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.before;
import static spark.Spark.port;

import static spark.Spark.staticFiles;
import spark.route.RouteOverview;
import spark.template.velocity.VelocityTemplateEngine;
/**
 *
 * @author di3sdn
 */
public class MockServer {

    public static final String VERSION = "v3.01";
    /**
     * @param args the command line arguments
     */
 public static void main(String[] args) {
        port (9400);
        
        before("/*", (request, response) -> {
            System.out.println("---------------------------------------------------");
            
        });
        
        RouteOverview.enableRouteOverview();
        
           
        get("/norman", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            Map<String, Object> user = new HashMap<>();
            model.put("hau","ab");
            
            //model.put("raw",request.raw());
            response.status(200);
            response.type("application/json");
            JsonTransformer json = new JsonTransformer();
            System.out.println(model);
            return json.render(model);
        });
        
        
        get("/*", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            Map<String, Object> user = new HashMap<>();
            model.put("hallo","welt");
            model.put("method","post");
            model.put("headers", request.headers());  
            model.put("host",request.host());
            model.put("ip",request.ip());
            model.put("port",request.port());
            model.put("protocol",request.protocol());
            model.put("params",request.params());
            model.put("attrs",request.attributes());
            System.out.println("BODY:"+request.body());
            System.out.println("RAW:"+request.raw());
            
            //model.put("raw",request.raw());
            response.status(200);
            response.type("application/json");
            JsonTransformer json = new JsonTransformer();
            System.out.println(model);
            return json.render(model);
        });
        
        post("/*", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            Map<String, Object> user = new HashMap<>();
            model.put("hallo","welt");
            model.put("method","post");
            model.put("headers", request.headers());  
            model.put("host",request.host());
            model.put("ip",request.ip());
            model.put("port",request.port());
            model.put("protocol",request.protocol());
            model.put("params",request.params());
            model.put("attrs",request.attributes());
            System.out.println("BODY:"+request.body());
            System.out.println("RAW:"+request.raw());
            //model.put("raw",request.raw());
            

            response.status(200);
            response.type("application/json");
            JsonTransformer json = new JsonTransformer();
            System.out.println(model);
            return json.render(model);
        });
        
        

    }
    
     private static boolean shouldReturnHtml(Request request) {
        String accept = request.headers("Accept");
        return accept != null && accept.contains("text/html");
    }
    
    private static Map<String,Object> helperJSONObject(int rc, String msg) {
        try {
            Map<String,Object> obj = new HashMap<String,Object>();
            obj.put("rc", rc);
            obj.put("msg", msg);
            return obj;
        } catch (Exception e) {
            return null;
        }
    }
    private static Map<String,Object> helperJSONString(int rc, String msg, String key1, String value1) {
        try {
            Map<String,Object> obj = new HashMap<String,Object>();
            obj.put("rc", rc);
            obj.put("msg", msg);
            obj.put(key1, value1);
            return obj;
        } catch (Exception e) {
            return null;//"{\"rc\":2,\"msg\",\""+msg+"\"}";
        }
    }
     
}
