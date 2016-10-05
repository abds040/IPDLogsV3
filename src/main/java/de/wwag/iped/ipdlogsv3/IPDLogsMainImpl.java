/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wwag.iped.ipdlogsv3;

import static de.wwag.iped.ipdlogsv3.IPDLogsMain.VERSION;
import de.wwag.iped.ipdlogsv3.tools.DBBean;
import de.wwag.iped.ipdlogsv3.tools.FileBean;
import de.wwag.iped.ipdlogsv3.tools.JsonTransformer;
import de.wwag.iped.ipdlogsv3.tools.Logr;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.velocity.VelocityTemplateEngine;

/**
 *
 * @author di3sdn
 */
public class IPDLogsMainImpl {
    //TODO Exception abfangen
    public static void before(Request request, Response response) {
        String raw_user = "dummy";
        try { raw_user = request.raw().getUserPrincipal().getName(); } catch (Exception e) { raw_user="dummy"; }
        //request.attribute("version", VERSION);
        request.attribute("userid", raw_user);
        if ("di3sdn".equalsIgnoreCase(request.attribute("userid"))) { request.attribute("admin", "true");}
        if ("di3vzm".equalsIgnoreCase(request.attribute("userid"))) { request.attribute("admin", "true");}
        System.out.println("------------------------------");
        String path = "";
        if (request.splat().length>0) {
            path = request.splat()[0];
        }
        System.out.println("CheckToken:"+path);
        if ("login".equalsIgnoreCase(path)) {
            System.out.println("CheckToken skipped for Login");
        } else {
            System.out.println("CheckToken checking the Token in Header");
            boolean authenticated = false;
            // ... check if authenticated
            if (!authenticated) {
                //halt(401, "You are not welcome here");

            }
        }
    }
    public static Object root(Request request, Response response) throws Exception {
        //Init
        Map<String, Object> model = new HashMap<>();
        model.put("userid", request.attribute("userid"));
        model.put("admin", request.attribute("admin"));
        model.put("version", VERSION);
        //Main
        Stack<Properties> grouplist = DBBean.getGroupList();
        System.out.println("DEBUGXXX:groups:" + grouplist);
        Stack<Properties> hostlist = DBBean.getHostList();
        System.out.println("DEBUGXXX:Hosts:" + hostlist);

        //user.put("name", request.params("data"));
        //model.put("email", "testemail");
        //user.setId(124124l);
        //model.put("user",model);
        model.put("groups", grouplist);
        model.put("hosts", hostlist);

        if (shouldReturnHtml(request)) {
            response.status(200);
            response.type("text/html");
            Map<String, Object> attributes = new HashMap<>();
            //attributes.put("posts", model.getAllPosts());
            VelocityTemplateEngine engine = new VelocityTemplateEngine();
            return engine.render(new ModelAndView(model, "index.html"));
        } else {
            response.status(200);
            response.type("application/json");
            JsonTransformer json = new JsonTransformer();
            return json.render(model);
        }
    }
    public static Object groups(Request request, Response response) throws Exception {
        //Init
        Map<String, Object> model = new HashMap<>();
        model.put("userid", request.attribute("userid"));
        model.put("admin", request.attribute("admin"));
        model.put("version", VERSION);
        //Main
        Stack<Properties> grouplist = DBBean.getGroupList();

        //user.put("name", request.params("data"));
        model.put("grouplist", grouplist);
        //user.setId(124124l);
        model.put("user", model);

        if (shouldReturnHtml(request)) {
            response.status(200);
            response.type("text/html");
            Map<String, Object> attributes = new HashMap<>();
            //attributes.put("posts", model.getAllPosts());
            VelocityTemplateEngine engine = new VelocityTemplateEngine();
            return engine.render(new ModelAndView(model, "groups.html"));
        } else {
            response.status(200);
            response.type("application/json");
            JsonTransformer json = new JsonTransformer();
            return json.render(model);
        }
    }
    public static Object groupHosts(Request request, Response response) throws Exception {
        //Init
        Map<String, Object> model = new HashMap<>();
        model.put("userid", request.attribute("userid"));
        model.put("admin", request.attribute("admin"));
        model.put("version", VERSION);
        //Main
        String groupname = request.params("groupname");
        Stack<Properties> hostlist = DBBean.getHostList(null, null, groupname);

        //user.put("name", request.params("data"));
        model.put("hostlist", hostlist);
        //user.setId(124124l);
        model.put("user", model);

        if (shouldReturnHtml(request)) {
            response.status(200);
            response.type("text/html");
            Map<String, Object> attributes = new HashMap<>();
            //attributes.put("posts", model.getAllPosts());
            VelocityTemplateEngine engine = new VelocityTemplateEngine();
            return engine.render(new ModelAndView(model, "grouphosts.html"));
        } else {
            response.status(200);
            response.type("application/json");
            JsonTransformer json = new JsonTransformer();
            return json.render(model);
        }
    }
    
    
    //TODO Exception abfangen
    public static Object listHostFiles(Request request, Response response) throws Exception {
            String me = "/host/:hostname/file/*";
            //Init
            Logr.debug(me, "START");
            Map<String, Object> model = new HashMap<>();
            model.put("userid", request.attribute("userid"));
            model.put("admin", request.attribute("admin"));
            model.put("version", VERSION);
            //Main
            String hostname = request.params("hostname");
            String type = request.params("type");
            StringBuffer filepathsplat = new StringBuffer();
            for (int x = 0; x < request.splat().length; x++) {
                filepathsplat.append("/"+request.splat()[x]);
            }
            String filepath = filepathsplat.toString();
            Logr.debug(me, "0010:hostname="+hostname+",filename="+filepath);
            Properties host = DBBean.getHost(0, hostname);
            
            FileBean.xxx(host, type, filepath, request.raw(), response.raw());
            return null;
    }
    
    
    private static boolean shouldReturnHtml(Request request) {
        String accept = request.headers("Accept");
        return accept != null && accept.contains("text/html");
    }
}
