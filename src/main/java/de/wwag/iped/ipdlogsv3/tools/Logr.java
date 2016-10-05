/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wwag.iped.ipdlogsv3.tools;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author admdi3sdn
 */
public class Logr {
    public static boolean Logr_debug = false;
	private static Logger LOGN = Logger.getLogger("IPDLOGS_NORMAL");
	private static Logger LOGJ = Logger.getLogger("IPDLOGS_JOURNAL");
	private static Logger LOGS = Logger.getLogger("IPDLOGS_STATS");


	public static void journal(String method, String msg) {
    	LOGJ.severe(method+"-"+msg);
        //debug(method,"ENTRY");
    }
	
    public static void entry(String method) {
    	LOGN.entering("", method);
        SOUT("ENTER-"+method);
        //debug(method,"ENTRY");
    }
    public static void exit(String method) {
    	LOGN.exiting("", method);
        SOUT("EXIT -"+method);
        //debug(method,"EXIT ");
    }
    public static void debug(String method, String msg) {
    	LOGN.fine(method+"-"+msg);
        SOUT("FINE -"+method+"-"+msg);
        /*if (Logr_debug) {
            log(method,"DEBUG",msg,null,false);
        }*/
    }
    public static void info(String method, String msg) {
    	LOGN.info(method+"-"+msg);
        //log(method,"INFO ",msg,null,false);
    }
    public static void warning(String method, String msg) {
    	LOGN.warning(method+"-"+msg);
        SOUT("WARN -"+method+"-"+msg);
        //log(method,"WARN ",msg,null,false);
    }
    public static void error(String method, String msg) {
    	LOGN.severe(method+"-"+msg);
        SOUT("ERROR-"+method+"-"+msg);
        //error(method,msg,null);
    }
    public static void error(String method, String msg, Throwable t) {
    	LOGN.log(Level.SEVERE, method+"-"+msg,t);
        SOUT("ERROR-"+method+"-"+msg+"-"+t);
        //log(method,"ERROR",msg,t,true);
    }

    private static void SOUT(String msg) {
        System.out.println("["+new java.util.Date().toString()+"] "+msg);
    }
    
    private static void log_ld(String method, String level, String message, Throwable t, boolean syserr) {
        System.out.println("["+new java.util.Date().toString()+"] "+level+":"+method+"-"+message);
        if (t!= null) t.printStackTrace(System.out);
        if (syserr) {
            if (t!= null) t.printStackTrace(System.err);
        }
    }

}
