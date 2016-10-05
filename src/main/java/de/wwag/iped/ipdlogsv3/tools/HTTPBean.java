/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wwag.iped.ipdlogsv3.tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import javax.net.ssl.HttpsURLConnection;

/**
 *
 * @author admdi3sdn
 */
public class HTTPBean {

    private HTTPBean() {

    }

    public static String getHTTPServerListing(String downloadtype, String hostname, String dirroot )  {
        try {
            StringBuffer fileliststring = new StringBuffer();
            URLConnection urlconn = HTTPBean.getConnection(
                        downloadtype, hostname, dirroot);

            BufferedReader br = new BufferedReader(new InputStreamReader(urlconn.getInputStream()));
            System.out.println(fileliststring.toString());

            String line = br.readLine();
            while (line != null) {
                fileliststring.append(line+"\n");
                line = br.readLine();
            }
            return fileliststring.toString();
        } catch (Exception E) {
            E.printStackTrace();
            return "ERROR";
        }
    }
    
    public static URLConnection getConnection(String protocoll, String hostname , String path) {
        try {
            int port = 6094; //HTTP
            protocoll="http";
           //host.getProperty("downloadtype"))
            /*if ("https".equalsIgnoreCase(protocoll)) {
                port = 6095;
            }*/
            if (!path.startsWith("/")) {
                path = "/"+path;
            }
            
            URL url = new URL(
                    protocoll + "://" + hostname + ".ww-intern.de:" + port + "/ipdlogshttp" + path);
            URLConnection urlconn = url.openConnection();
            if (System.getProperty("configPath") == null) {
                //if (urlconn instanceof HttpsURLConnection) {
                if (port == 6095) {
                    String keyStoreFileName = "E:\\ms_prog\\WorkspacesJava\\NB65\\IPDLogs2\\work\\ipdlogs.jks";
                    String keyStorePassword = "fwbm723";
                    SSLBean sslma = new SSLBean(keyStoreFileName, keyStorePassword, keyStoreFileName, keyStorePassword, "ipdlogs");
                    ((HttpsURLConnection) urlconn).setSSLSocketFactory(sslma.getSSLFactory());
                }
            }
            return urlconn;
        } catch (Exception E) {
            E.printStackTrace();
            return null;
        }
    }



}
