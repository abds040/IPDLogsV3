/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wwag.iped.ipdlogsv3.tools;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author di3sdn
 */
public class TempTestHTTPHead {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        System.setProperty("javax.net.ssl.trustStore","C:\\Daten\\Arbeit\\Pues\\Test.jks");
        System.setProperty("javax.net.ssl.trustStoreType", "JKS");
        System.setProperty("javax.net.ssl.trustStorePassword","ASWeb");
        
        
        // TODO code application logic here
        //String url = "https://personenuebersicht-stu.ww-intern.de:443";
        String url = "https://wasitud16.ww-intern.de:443";
        //String url = "http://wasitud16.ww-intern.de";
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("HEAD");
        int responseCode = connection.getResponseCode();
        System.out.println("HEAD:RC"+responseCode);
        if (responseCode != 200) {
            // Not OK.
        }
    }

}
