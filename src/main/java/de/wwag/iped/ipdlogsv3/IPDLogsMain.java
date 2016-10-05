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
public class IPDLogsMain {

    public static final String VERSION = "v3.01";
    public static final String DBJDBC = "jdbc/ipdlogsv2";
    public static final String DBLOCALURL = "jdbc:db2://db2was1s.ww-intern.de:50234/wasstats";
    public static final String DBLOCALUSER= "wasup";
    public static final String DBLOCALPASS= "wasI9"; //TEST
    /**
     * @param args the command line arguments
     */
 public static void main(String[] args) {
        port (9000);
        //port(80);
        staticFiles.location("/static"); // Static files
        // TODO code application logic here
        
        /*before("/login", (request, response) -> {
            System.out.println("Before doLogin");
            return;
        });*/
        
        before("/*", (request, response) -> { IPDLogsMainImpl.before(request, response);});
        RouteOverview.enableRouteOverview();
        get("/",                       (request, response) -> { return IPDLogsMainImpl.root(request, response);});
        get("/groups",                 (request, response) -> {return IPDLogsMainImpl.groups(request, response);});
        get("/group/:groupname/hosts", (request, response) -> { return IPDLogsMainImpl.groupHosts(request, response);});
        get("/host/:hostname/dirs",    (request, response) -> {
            //Init
            Map<String, Object> model = new HashMap<>();
            model.put("userid", request.attribute("userid"));
            model.put("admin", request.attribute("admin"));
            model.put("version", VERSION);
            //Main
            String hostname = request.params("hostname");
            Stack dirlist = DBBean.getDirList_Hostname(hostname);
            //TODO Fehlerhandling wenn dirlist fehlerhaft

            model.put("hostname", hostname);
            model.put("dirlist", dirlist);
            //model.put("user",model);
            
            if (shouldReturnHtml(request)) {
                response.status(200);
                response.type("text/html");
                Map<String, Object> attributes = new HashMap<>();
                //attributes.put("posts", model.getAllPosts());
                VelocityTemplateEngine engine = new VelocityTemplateEngine();
                return engine.render(new ModelAndView(model, "hostdirs.html"));
            } else {
                response.status(200);
                response.type("application/json");
                JsonTransformer json = new JsonTransformer();
                return json.render(model);
            }
        });
        
        // /host/:hostname/dir/:dirid/files
        get("/host/:hostname/dir/:dirid", (request, response) -> {
            String me = "/host/:hostname/dir/:dirid";
            //Init
            Logr.debug(me, "START");
            Map<String, Object> model = new HashMap<>();
            model.put("userid", request.attribute("userid"));
            model.put("admin", request.attribute("admin"));
            model.put("version", VERSION);
            //Main
            String hostname = request.params("hostname");
            String dirid = request.params("dirid");
            Logr.debug(me, "0010:hostname="+hostname+",dirid="+dirid);
            String dirname = DBBean.getDirName(Integer.parseInt(dirid));
            String filelist_html = HTTPBean.getHTTPServerListing("http", hostname, dirname);
            Logr.debug(me, "FileListHTML:"+filelist_html);
            List<FileBean> filelist = FileBean.parseFileObjectsHTML(filelist_html, dirname);
            Logr.debug(me, "FileList:"+filelist);
            //TODO Fehlerhandling wenn dirlist fehlerhaft

            model.put("hostname", hostname);
            model.put("dirid"   , dirid);
            model.put("dirname" , dirname);
            model.put("filelist", filelist);
            //model.put("user",model);
            
            if (shouldReturnHtml(request)) {
                response.status(200);
                response.type("text/html");
                Map<String, Object> attributes = new HashMap<>();
                //attributes.put("posts", model.getAllPosts());
                VelocityTemplateEngine engine = new VelocityTemplateEngine();
                return engine.render(new ModelAndView(model, "hostdirfiles.html"));
            } else {
                response.status(200);
                response.type("application/json");
                JsonTransformer json = new JsonTransformer();
                return json.render(model);
            }
        });
        
        // /host/:hostname/dir/:dirid/files
        get("/host/:hostname/type/:type/file/*", (request, response) -> { return IPDLogsMainImpl.listHostFiles(request, response); });
        
        
        get("/posts5/:data", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("hello", request.params("data"));
            Map<String, Object> user = new HashMap<>();
            user.put("name", request.params("data"));
            user.put("email", "testemail");
            //user.setId(124124l);
            model.put("user",user);
            
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
        });
        
        
        
        get("/*", (req, res) -> {
            res.redirect("/");
            return "Wo wollet se no!";
        });
        post("/*", (req, res) -> {
            return "Wo wollet se no!";
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
