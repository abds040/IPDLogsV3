/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wwag.iped.ipdlogsv3.tools;


import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Daniel
 */
public class XMLBean {

    public static String getXMLTagData(String xml, String tag, int maxlength) {
        String tagdata = XMLBean.getXMLTagData(xml, tag);
        if (tagdata.length()>maxlength) {
            return tagdata.substring(0,maxlength-1);
        } else {
            return tagdata;
        }
    }
    public static String getXMLTagData(String xml, String tag) {
        //#Logr.info("DEBUG-getXMLTagData-001");
        //#Logr.info("DEBUG-getXMLTagData-xml="+xml);
        //#Logr.info("DEBUG-getXMLTagData-EEE");
        int start = -1; int stop = 0;
        // Check fopr <TAG>
        start = xml.indexOf("<"+tag+">",stop)+2+tag.length();
        if (start == -1) {
            // Check fopr <TAG XXXXX>
            start = xml.indexOf("<"+tag+" ",stop)+2+tag.length();
        }
        stop  = xml.indexOf("</"+tag+">",start);
        if (start < 0) return "null";//ERROR: Starttag for tag "+tag+" was not found.";
        if (stop < 0) return "null";//ERROR: Endtag for tag "+tag+" was not found.";
        return xml.substring(start,stop);
    }

    public static List<String> getXMLTags(String xml, String tag) {
        //Logr.info("getXMLTags.Reading "+tag);
        //Logr.info("getXMLTags.Reading xml -"+xml+"-");
        //Logr.info("getXMLTags.Reading xmllength="+xml.length());
        int start = -1; int stop = 0;
        int start1 = -1;
        int start2 = -1;
        List<String> list = new ArrayList<String>();
        
        //Needed for parsing problem
        xml = "  "+xml;

        while (stop < xml.length()) {
            //System.out.println("getXMLTags.Loop1,start="+start+",stop="+stop);
            start = -1;
            start1 = xml.indexOf("<"+tag+">",stop);
            start2 = xml.indexOf("<"+tag+" ",stop);
            // The smallest value greater than -1 has the first tag.
            //System.out.println("getXMLTags.Loop2,start1="+start1+",start2="+start2);
            if ( (start1 > -1) && (start2 > -1) ) {
                if (start1 > start2) {
                    start = start2;
                } else {
                    start = start1;
                }
            } else {
                if (start1 == -1) start = start2;
                if (start2 == -1) start = start1;
            }
            if (start == -1) {
                stop = xml.length();
            } else {
                //System.out.println("getXMLTags.Loop3,start="+start+",stop="+stop);
                start =  xml.indexOf(">",start)+1;
                stop  = xml.indexOf("</"+tag+">",start);
                //System.out.println("getXMLTags.Loop4,start="+start+",stop="+stop);
                if (stop < 0) {
                    Logr.info("XMLTools.getXMLTags","ERROR-Stoptag was not found.");
                    Logr.info("XMLTools.getXMLTags","ERROR-......."+xml.substring(start));
                    return null;
                }
                //Logr.info("getXMLTags.Loop2,start="+start+",stop="+stop);
                //Logr.info("getXMLTags.Loop3,text="+xml.substring(start,stop));
                list.add(xml.substring(start,stop));
                stop = stop+3+tag.length();
                //Logr.info("getXMLTags.Loop4,start="+start+",stop="+stop);
            }
        }
        return list;
    }
}