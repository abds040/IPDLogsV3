/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wwag.iped.ipdlogsv3.tools;

import java.io.BufferedInputStream;
import java.io.Serializable;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author admdi3sdn
 */
public class FileBean implements Serializable {
    private String fileName = null;
    private String fileParent = null;
    private boolean directory = false;
    private String size = null;
    private String date = null;
    private String dateobj = null;
    
    public FileBean(String filename, boolean directory, String lastupdate,String size, String parent) {
        this.fileName = filename;
        this.directory = directory;
        this.date = lastupdate;
        this.size = size;
        this.fileParent = parent;
    }

    public String getDate() {
        return date;
    }
    
    public String getDateLongAsString() {
    	Logr.debug("FileObject.getDateLongAsString", "File:"+getFileName()+":"+getDate());
    	long datelong = 0l;
    	try {
    		SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
    		Date date = formatter.parse(getDate());
    		datelong = date.getTime();
    	} catch (Exception e) {	}
    	/*try {
    		SimpleDateFormat formatter = new SimpleDateFormat("MMM dd HH:mm");
    		Date date = formatter.parse(getDate());
    		datelong = date.getTime();
    	} catch (Exception e) {	}*/
    	//1 Oct 11 2014 
    	//2 Mar 07 12:37
    	StringBuffer datestring = new StringBuffer(Long.toString(datelong));
    	while (datestring.length()<20) {
    		datestring.insert(0, "0");
    	}
    	Logr.debug("FileObject.getDateLongAsString", "File:"+getFileName()+":"+datestring);
    	return datestring.toString();
    }

    public boolean isDirectory() {
        return directory;
    }

    public String getFileName() {
        return fileName;
    }

    public String getSize() {
        return size;
    }

    public String getFileParent() {
        return fileParent;
    }
    
    public String getFilePath() {
        return fileParent+"/"+fileName;
    }
    
    
    public String toString() {
        return this.getFileName()+" - "+this.getDate()+ " - " + this.getSize();
    }
    
    
    public static List<FileBean> parseFileObjectsHTML(String fileliststring, String filelisturl) {
        String method = "FileObject.parseFileObjectHTML";
        List<FileBean> fos = new ArrayList<FileBean>();
        try {
            
            int pathStart = fileliststring.toString().toLowerCase().indexOf("<title>");
            int pathStop = -1;
            if (pathStart > -1) {
                pathStart = fileliststring.toString().toLowerCase().indexOf("/", pathStart);
                pathStop = fileliststring.toString().toLowerCase().indexOf("</title>", pathStart);
            }
            String parent = fileliststring.toString().substring(pathStart, pathStop);
            if (parent.toLowerCase().startsWith("/ipdlogshttp")) {
                parent = parent.substring("/ipdlogshttp".length());
            }
            Logr.info(method, "Path="+parent);
            
            
            List<String> filelist = XMLBean.getXMLTags(fileliststring.toString(), "tr");
            if (filelist == null || filelist.size() == 0) {
                throw new Exception("Error parsing Directory:"+filelisturl);
            }
            Logr.debug(method, "FileList:"+filelist);

            for (String aFileItem : filelist) {
                Logr.debug(method, "---");
                Logr.debug(method, "FI:"+aFileItem);

                List<String> filetags = XMLBean.getXMLTags(aFileItem, "td");
                for (String aFileTagItem : filetags) {
                    Logr.debug(method, "   TAG:"+aFileTagItem);
                }

                if (filetags.size()> 3) {
                    if  ( (filetags.get(1) != null) && (filetags.get(1).toLowerCase().indexOf("parent directory")>-1) ) {
                        // Exclude Parent Directory
                    } else {

                        boolean isDirectory = (filetags.get(0).toLowerCase().indexOf("alt=\"[dir]\"") > -1);

                        int nameStart = filetags.get(1).toLowerCase().indexOf("<a href=\"")+9;
                        int nameStop = filetags.get(1).toLowerCase().indexOf("\">",nameStart);
                        String fileName = filetags.get(1).substring(nameStart,nameStop);

                        //Logr.error(method, "File:"+fileName+":"+);

                        String fileDate = filetags.get(2);
                        String fileSize = filetags.get(3);

                        FileBean fo = new FileBean(fileName, isDirectory, fileDate, fileSize, parent);
                        fos.add(fo);
                    }
                } else {
                    Logr.error(method, "Error Parsing a line for Directory:"+filelisturl);
                    Logr.error(method, "Line cound not be parsed:"+aFileItem);
                    //FileObject fo = new FileObject("ErrorFile", false, "ErrorDate", "ErrorSize", "ErrorParent");
                    //fos.add(fo);
                }
            }
            return fos;
        } catch (Exception e) {
            return null;
        }
    }
    
    public static void xxx(Properties host, String type, String filename, HttpServletRequest request, HttpServletResponse response) {
        Logr.entry("XXX");
        //String type = "text";
        int fileSize = 0;
        ServletOutputStream out = null;
        
        Logr.debug("XXXXX", "00001");
        if ("text".equals(type)) {
            Logr.debug("XXXXX", "00002:Text");
            response.setContentType("text/plain;charset=UTF-8");
        } else if ("binary".equals(type)) {
            Logr.debug("XXXXX", "00002:Binary");
            //response.setContentType(Utilities.getContextType(downloadfilepath));
            //Logr.debug("XXXXX", "Binary Context Type for "+downloadfilepath+" = "+response.getContentType());
        } else {
            Logr.debug("XXXXX", "00002:Else Html");
            response.setContentType("text/html;charset=UTF-8");
        }
        Logr.debug("XXXXX", "00003");
        fileSize = 0;
        try {
            Logr.debug("XXXXX", "00004");
            out = response.getOutputStream();
            URLConnection urlconn = HTTPBean.getConnection(
                    host.getProperty("downloadtype"),
                    host.getProperty("hostname"),
                    filename);
            
            Logr.debug("XXXXX", "00005:"+urlconn.getURL().toString());
            // Get the response
            BufferedInputStream bis = new BufferedInputStream(urlconn.getInputStream());
            byte b[] = new byte[100000];
            int c = 0;
            Logr.debug("XXXXX", "00006");
            //out.write("Stööööööööööööööööööööökkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkart".getBytes());
            out.flush();
            while ((c = bis.read(b)) != -1) {
                Logr.debug("XXXXX", "00007");
                fileSize += c;
                out.write(b, 0, c);
                out.flush();
            }
            Logr.debug("XXXXX", "00008");
            b = null;
            try {
                bis.close();
            } catch (Exception E) {
                Logr.debug("XXXXX", "00008:Error");
                E.printStackTrace();
            }
            out.flush();
        } catch (Exception e) {
            Logr.debug("XXXXX", "00009:Error");
            e.printStackTrace();
        } finally {
            Logr.debug("XXXXX", "00010-Fin");
            try {
                out.close();
            } catch (Exception EX) {
            }
            DBBean.insertDownloadStatistics(request.getUserPrincipal().getName(), host.getProperty("hostname"), filename, (int) fileSize);
            Logr.info("XXXX", "HTTP-Download: File=" + filename + "  size=" + fileSize);
        }
        Logr.exit("XXX");
    }
    
}