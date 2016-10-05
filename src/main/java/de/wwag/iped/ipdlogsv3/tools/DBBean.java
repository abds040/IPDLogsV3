/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wwag.iped.ipdlogsv3.tools;

import de.wwag.iped.ipdlogsv3.IPDLogsMain;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 *
 * @author admdi3sdn
 */
public class DBBean {
	public static String cl = "DBBean.";
        public static final String DBMSG001 = "(DBMSG001) DBBean.getHostList failed.";
        public static final String DBMSG002 = "(DBMSG002) DBBean.getConn failed.";


    public static Stack<Properties> getGroupList() throws Exception {
        Connection conn = null;
        try {
            //System.out.println("Database Records fpr getGroupList");
            conn = getConn();
            conn.setAutoCommit(true);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from wasup.ipdlogsv2_groups");
            Stack<Properties> grouplist = new Stack<Properties>();
            while (rs.next()) {
                //String group[] = new String[5];
                Properties group = new Properties();
                //System.out.println("Database Record="+rs.getString("groupid")+","+rs.getString("groupname")+rs.getString("groupdesc"));
                group.put("groupid"   ,rs.getString("groupid"));
                group.put("groupname" ,rs.getString("groupname"));
                group.put("groupdesc" ,rs.getString("groupdesc"));
                grouplist.add(group);
            }
            return grouplist;
        //} catch (Exception i) {
        //    throw i;
        } catch (Throwable t) {
            t.printStackTrace();
            throw new Exception(DBMSG001+"\n"+t.getMessage());
        } finally {
            try {
                conn.close();
            } catch (Throwable t) {}
            conn = null;
        }
    }
    public static Properties getGroup(int groupid) throws Exception {
        Connection conn = null;
        try {
            //System.out.println("Database Records fpr getGroupList");
            conn = getConn();
            conn.setAutoCommit(true);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from wasup.ipdlogsv2_groups where groupid = "+groupid);
            Properties group = new Properties();
            if (rs.next()) {
                group.put("groupid"   ,rs.getString("groupid"));
                group.put("groupname" ,rs.getString("groupname"));
                group.put("groupdesc" ,rs.getString("groupdesc"));
            }
            return group;
        //} catch (Exception i) {
        //    throw i;
        } catch (Throwable t) {
            t.printStackTrace();
            //TODO wrong SMGid
            throw new Exception(DBMSG001+"\n"+t.getMessage());
        } finally {
            try {
                conn.close();
            } catch (Throwable t) {}
            conn = null;
        }
    }
    public static int getGroupID(String groupname) throws Exception {
        Connection conn = null;
        try {
            //System.out.println("Database Records fpr getGroupList");
            conn = getConn();
            conn.setAutoCommit(true);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from wasup.ipdlogsv2_groups where groupname = '"+groupname+"'");
            Properties group = new Properties();
            if (rs.next()) {
                return rs.getInt("groupid");
            }
            return -1;
        //} catch (Exception i) {
        //    throw i;
        } catch (Throwable t) {
            t.printStackTrace();
            //TODO wrong SMGid
            throw new Exception(DBMSG001+"\n"+t.getMessage());
        } finally {
            try {
                conn.close();
            } catch (Throwable t) {}
            conn = null;
        }
    }
    public static Set<String> getGroupsForHostIDAsString(String hostid, String groupsearchstring) throws Exception {
        Connection conn = null;
        String me = "getGroupsForHostIDAsString";
        try {
            //No search performed if null

            //System.out.println("getGroupsForHostIDAsString - Search = "+groupsearchstring);
            conn = getConn();
            conn.setAutoCommit(true);
            Statement stmt = conn.createStatement();
            String hostidsearch = "";
            if (hostid != null) {
                hostidsearch = "where hostid = "+hostid;
            } else {
                hostidsearch = "";
            }
            ResultSet rs = stmt.executeQuery("select * from wasup.ipdlogsv2_grouphosts "+hostidsearch);
            Set<String> groupIDs = new HashSet<String>();
            while (rs.next()) {
                Logr.debug(cl+me, "getGroupsForHostIDAsString - found entry = "+rs.getString("groupid"));
                String groupname = getGroup(rs.getInt("groupid")).getProperty("groupname");
                if (groupname == null) { groupname = ""; }
                if ( (groupsearchstring == null) || ("".equals(groupsearchstring)) ) {
                    groupIDs.add(groupname);
                } else {
                    if (groupname.toLowerCase().indexOf(groupsearchstring.toLowerCase())>-1) {
                        groupIDs.add(groupname);
                    }
                }
            }
            return groupIDs;
        //} catch (Exception i) {
        //    throw i;
        } catch (Throwable t) {
            t.printStackTrace();
            throw new Exception(DBMSG001+"\n"+t.getMessage());
        } finally {
            try {
                conn.close();
            } catch (Throwable t) {}
            conn = null;
        }
    }
    public static boolean joinGroupHostname(String hostid, String groupname) throws Exception {
        Connection conn = null;
        try {
            //No search performed if null
            //System.out.println("getGroupsForHostIDAsString - Search = "+groupsearchstring);
            conn = getConn();
            conn.setAutoCommit(true);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from wasup.ipdlogsv2_grouphosts where hostid ="+hostid+" and groupid="+getGroupID(groupname));
            Set<String> groupIDs = new HashSet<String>();
            if (rs.next()) { return true; } // Already joined
            int rc = stmt.executeUpdate("insert into wasup.ipdlogsv2_grouphosts values ("+getGroupID(groupname)+","+hostid+")");
            if (rc == 1) {
                System.out.println("joinGroupHostname rc="+rc);
                return true;
            }
            return false;
        //} catch (Exception i) {
        //    throw i;
        } catch (Throwable t) {
            t.printStackTrace();
            throw new Exception(DBMSG001+"\n"+t.getMessage());
        } finally {
            try {
                conn.close();
            } catch (Throwable t) {}
            conn = null;
        }
    }
    public static boolean unjoinGroupHostname(String hostid, String groupname) throws Exception {
        Connection conn = null;
        try {
            //No search performed if null
            //System.out.println("getGroupsForHostIDAsString - Search = "+groupsearchstring);
            conn = getConn();
            conn.setAutoCommit(true);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from wasup.ipdlogsv2_grouphosts where hostid ="+hostid+" and groupid="+getGroupID(groupname));
            Set<String> groupIDs = new HashSet<String>();
            if (!rs.next()) { return true; } // Already joined
            int rc = stmt.executeUpdate("delete from wasup.ipdlogsv2_grouphosts where hostid ="+hostid+" and groupid="+getGroupID(groupname));
            if (rc == 1) {
                return true;
            }
            return false;
        //} catch (Exception i) {
        //    throw i;
        } catch (Throwable t) {
            t.printStackTrace();
            throw new Exception(DBMSG001+"\n"+t.getMessage());
        } finally {
            try {
                conn.close();
            } catch (Throwable t) {}
            conn = null;
        }
    }


    public static Stack<Properties> getHostList() throws Exception {
        return getHostList(null, null, null);
    }
    public static Stack<Properties> getHostList(String hostsearch, String descsearch, String groupsearch) throws Exception {
        Connection conn = null;
        try {
            System.out.println("getHostList");
            conn = getConn();
            conn.setAutoCommit(true);
            Statement stmt_hosts = conn.createStatement();
            Statement stmt_grouphosts = conn.createStatement();
            ResultSet rs = stmt_hosts.executeQuery("select * from wasup.ipdlogsv2_hosts");
            Stack<Properties> hostlist = new Stack<Properties>();
            while (rs.next()) {
                //String host[] = new String[5];
                Properties host = new Properties();
                //System.out.println("Database Record="+rs.getString("hostid")+","+rs.getString("hostname")+rs.getString("hostdesc"));
                String hostid   = rs.getString("hostid");
                String hostname = rs.getString("hostname");
                String hostdesc = rs.getString("hostdesc");
                String hostuser = rs.getString("hostuser");
                String hostpass = rs.getString("hostpass");
                String downloadtype = rs.getString("downloadtype");

                //System.out.println("getHostList - hostsearhc = "+hostsearch);
                if (hostsearch != null)  {
                    if (hostname.toLowerCase().indexOf(hostsearch.toLowerCase())==0) {
                        continue;
                    }
                }
                //System.out.println("getHostList - descsearhc = "+descsearch);
                if (descsearch != null)  {
                    if (hostdesc.toLowerCase().indexOf(descsearch.toLowerCase())==0) {
                        continue;
                    }
                }
                //System.out.println("getHostList - groupsearhc = "+groupsearch);
                if (groupsearch != null)  {
                    if (getGroupsForHostIDAsString(hostid, groupsearch).size() == 0) {
                        continue;
                    }
                }
                //System.out.println("getHostList - found = "+hostname +"("+hostid+")");

                host.setProperty("hostid"   ,hostid);
                host.setProperty("hostname" ,hostname);
                host.setProperty("hostdesc" ,hostdesc);
                host.setProperty("hostuser" ,hostuser);
                host.setProperty("hostpass" ,hostpass);
                host.setProperty("downloadtype", downloadtype);
                host.setProperty("groups"   ,""+getGroupsForHostIDAsString(hostid, null));

                hostlist.add(host);
            }
            return hostlist;
        //} catch (Exception i) {
        //    throw i;
        } catch (Throwable t) {
            t.printStackTrace();
            throw new Exception(DBMSG001+"\n"+t.getMessage());
        } finally {
            try {
                conn.close();
            } catch (Throwable t) {}
            conn = null;
            
        }

    }
    public static Properties getHost(int hostid, String hostname) throws Exception {
        String method = "DBBean.getHost";
        Logr.entry(method);
        Connection conn = null;
        try {
            Logr.debug(method, "Database Records for getHostList");
            conn = getConn();
            conn.setAutoCommit(true);
            Statement stmt = conn.createStatement();
            String sql_string = "select * from wasup.ipdlogsv2_hosts where hostid = "+hostid;
            if (hostname != null && hostname.length()>0) {
                sql_string = "select * from wasup.ipdlogsv2_hosts where hostname='"+hostname+"'";
            }
            ResultSet rs = stmt.executeQuery(sql_string);
            Properties host = new Properties();
            if (rs.next()) {
                Logr.debug(method,"Database Record="+rs.getString("hostid")+","+rs.getString("hostname")+rs.getString("hostdesc"));
                host.setProperty("hostid",   rs.getString("hostid"));
                host.setProperty("hostname", rs.getString("hostname"));
                host.setProperty("hostdesc", rs.getString("hostdesc"));
                host.setProperty("hostuser", rs.getString("hostuser"));
                host.setProperty("hostpass", rs.getString("hostpass"));
                host.setProperty("downloadtype", rs.getString("downloadtype"));
                host.setProperty("groups"   ,""+getGroupsForHostIDAsString(rs.getString("hostid"), null));
                return host;
            }
            throw new Exception("Host with hostid "+hostid+" not found");
        //} catch (Exception i) {
        //    throw i;
        } catch (Throwable t) {
            Logr.error(method,"Error getting host for hostid "+hostid);
            Logr.error(method, t.getMessage(), t);
            //DEBUG
            throw new Exception(DBMSG001+"\n"+t.getMessage());
        } finally {
            Logr.exit(method);
            try {
                conn.close();
            } catch (Throwable t) {}
            conn = null;
        }

    }
    public static Properties getHostFromDirId(int dirid)  throws Exception  {
        String method = "DBBean.getHostFromDirId";
        Logr.entry(method);
        Connection conn = null;
        try {
            conn = getConn();
            conn.setAutoCommit(true);
            Statement stmt = conn.createStatement();
            ResultSet rs1 = stmt.executeQuery("select * from wasup.ipdlogsv2_dirs where dirid = "+dirid);
            int hostid = -1;
            if (rs1.next()) {
                Logr.debug(method,"Database Record:dirid="+rs1.getInt("dirid")+",dirhostid="+rs1.getInt("dirhostid"));
                hostid = rs1.getInt("dirhostid");
            } else {
                throw new Exception("DirId "+dirid+" not found.");
            }
            return DBBean.getHost(hostid, null);
        //} catch (Exception i) {
        //    throw i;
        } catch (Throwable t) {
            Logr.error(method,"Error getting host from dirid "+dirid);
            Logr.error(method, t.getMessage(), t);
            throw new Exception(DBMSG001+"\n"+t.getMessage());
        } finally {
            Logr.exit(method);
            try {
                conn.close();
            } catch (Throwable t) {}
            conn = null;
        }
    }

    public static String getDirName(int dirid)  throws Exception  {
        String method = "DBBean.getDirName";
        Logr.entry(method);
        Connection conn = null;
        try {
            conn = getConn();
            conn.setAutoCommit(true);
            Statement stmt = conn.createStatement();
            ResultSet rs1 = stmt.executeQuery("select * from wasup.ipdlogsv2_dirs where dirid = "+dirid);
            if (rs1.next()) {
                Logr.debug(method,"Database Record:dirid="+rs1.getInt("dirid")+",dirname="+rs1.getString("dirname"));
                return rs1.getString("dirname");
            } else {
                throw new Exception("DirId "+dirid+" not found.");
            }
        //} catch (Exception i) {
        //    throw i;
        } catch (Throwable t) {
            Logr.error(method,"Error getting dirname from dirid "+dirid);
            Logr.error(method, t.getMessage(), t);
            throw new Exception(DBMSG001+"\n"+t.getMessage());
        } finally {
            Logr.exit(method);
            try {
                conn.close();
            } catch (Throwable t) {}
            conn = null;
        }
    }

    public static boolean setHost(int hostid, String hostname, String hostdesc, 
            String hostuser, String hostpass, String downloadtype) throws Exception {
        String method = "DBBean.getHost";
        Logr.entry(method);
        Connection conn = null;
        try {
            Logr.debug(method, "Database Records for getHostList");
            conn = getConn();
            conn.setAutoCommit(true);
            Statement stmt = conn.createStatement();
            StringBuffer sql = new StringBuffer();
            sql.append("update wasup.ipdlogsv2_hosts set ");
            sql.append("  hostname='"+hostname+"',hostdesc='"+hostdesc+"',");
            sql.append("  hostuser='"+hostuser+"',hostpass='"+hostpass+"',");
            sql.append("  downloadtype='"+downloadtype+"' ");
            sql.append("  where hostid="+hostid);

            int rc = stmt.executeUpdate(sql.toString());
            if (rc == 1) {
                Logr.debug(method, "Hostedit was successful for hostid "+hostid+". SQL="+sql.toString());
                return true;
            }
            throw new Exception ("Hostedit was unsuccessful for hostid "+hostid+". RC="+rc+", SQL="+sql.toString());
        //} catch (Exception i) {
        //    throw i;
        } catch (Throwable t) {
            Logr.error(method,"Hostedit was unsuccessful for hostid "+hostid);
            Logr.error(method, t.getMessage(), t);
            return false;
            //DEBUG
        } finally {
            Logr.exit("DBBean.getHost");
            try {
                conn.close();
            } catch (Throwable t) {}
            conn = null;
        }
    }


    public static Stack<Properties> getDirList_Hostname(String hostname) throws Exception {
        String hostid = getHost(0, hostname).getProperty("hostid");
        return getDirList_HostID(hostid, hostname);
    }
    public static Stack<Properties> getDirList_HostID(String hostid, String hostname) throws Exception {
        Connection conn = null;
        String me = "getDirList";
        try {
            Logr.debug(cl+me, "Database Records fpr getDirList: hostid="+hostid);
            conn = getConn();
            conn.setAutoCommit(true);
            Statement stmt = conn.createStatement();
            String sql_string = "select * from wasup.ipdlogsv2_dirs where dirhostid="+hostid;
            if (hostname == null) {
                try { hostname = getHost(Integer.parseInt(hostid), null).getProperty("hostname"); } catch (Exception e) { hostname = "ERROR"; }
            }
            ResultSet rs = stmt.executeQuery(sql_string);
            Stack<Properties> dirlist = new Stack<Properties>();
            while (rs.next()) {
                Properties dir = new Properties();
                //String host[] = new String[4];
                //System.out.println("Database Record="+rs.getString("hostid")+","+rs.getString("hostname")+rs.getString("hostdesc"));
                dir.put("dirid", rs.getString("dirid"));
                dir.put("dirhostid", rs.getString("dirhostid"));
                dir.put("dirhostname", hostname);
                dir.put("dirname", rs.getString("dirname"));
                dir.put("dirdesc", rs.getString("dirdesc"));
                dirlist.add(dir);
            }
            return dirlist;
        //} catch (Exception i) {
        //    throw i;
        } catch (Throwable t) {
            t.printStackTrace();
            throw new Exception(DBMSG001+"\n"+t.getMessage());
        } finally {
            try {
                conn.close();
            } catch (Throwable t) {}
        }
    }

    public static Map<String,String> getDirUserList(String dirid) throws Exception {
        Connection conn = null;
        try {
            System.out.println("getDirUserList");
            conn = getConn();
            conn.setAutoCommit(true);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from wasup.ipdlogsv2_dirusers where dirid="+dirid);
            Map<String,String> diruserlist = new TreeMap<String,String>();
            while (rs.next()) {
                //String diruser[] = new String[2];
                //System.out.println("Database Record="+rs.getString("hostid")+","+rs.getString("hostname")+rs.getString("hostdesc"));
                //diruser[0] = rs.getString("userid");
                //diruser[1] = rs.getString("username");
                //diruserlist.add(diruser);
                diruserlist.put(rs.getString("userid"),rs.getString("description"));
            }
            return diruserlist;
        //} catch (Exception i) {
        //    throw i;
        } catch (Throwable t) {
            t.printStackTrace();
            throw new Exception(DBMSG001+"\n"+t.getMessage());
        } finally {
            try {
                conn.close();
            } catch (Throwable t) {}
        }
    }

    public static boolean testDirUserID(String dirid, String userid) throws Exception {
        Connection conn = null;
        String method="DBBean.testDirUderIDxx";
        Logr.entry(method);
        try {
            Logr.debug(method,"dirid="+dirid+",userid="+userid.toLowerCase());
            conn = getConn();
            conn.setAutoCommit(true);
            Statement stmt = conn.createStatement();
            ResultSet rs1 = stmt.executeQuery("select * from wasup.ipdlogsv2_dirusers where dirid="+dirid+" and LOWER(userid)='"+userid.toLowerCase()+"'");
            if (rs1.next()) {
                Logr.debug(method,"dirid="+dirid+",userid="+userid.toLowerCase()+"---granted");
                return true;
            }
            Logr.debug(method,"dirid="+dirid+",userid="+userid.toLowerCase()+"---DENIED");
            return false;
        //} catch (Exception i) {
        //    throw i;
        } catch (Throwable t) {
            Logr.error(method, t.getMessage(), t);
            t.printStackTrace();
            //throw new Exception(Constants.MSG004+"\n"+t.getMessage());
            return false;
        } finally {
            try {
                conn.close();
            } catch (Throwable t) {}
            Logr.exit(method);
        }
    }

    public static boolean insertDownloadStatistics(String userid, String hostname, String filename, int filesize) {
        Connection conn = null;
        String method="DBBean.insertDownloadStatistics";
        Logr.entry(method);
        try {
            conn = getConn();
            conn.setAutoCommit(true);
            Statement stmt = conn.createStatement();
            String sql ="insert into ipdlogs_trans values("+
                        "'"+new java.sql.Date(System.currentTimeMillis())+"',"+
                        "'"+new java.sql.Time(System.currentTimeMillis())+"',"+
                        "'"+userid.toLowerCase()+"','"+hostname.toLowerCase()+"','"+filename+"',"+filesize+",0,"+
                        "'"+IPDLogsMain.VERSION+"')";
            int rc = stmt.executeUpdate(sql);
            if (rc == 1) return true;
            throw new  Exception("Error logging filedownload:"+userid.toLowerCase()+":"+hostname.toLowerCase()+":"+filename+":"+filesize);
        } catch (Throwable t) {
            Logr.error(method, "Error logging file download",t);
            t.printStackTrace();
            //throw new Exception(Constants.MSG004+"\n"+t.getMessage());
            return false;
        } finally {
            try {
                conn.close();
            } catch (Throwable t) {}
            Logr.exit(method);
        }
    }


    public static String adminAddGroup(String groupname, String groupdesc) throws Exception {
        Connection conn = null;
        String method="DBBean.adminAddGroup";
        Logr.entry(method);
        try {
            conn = getConn();
            conn.setAutoCommit(true);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from wasup.ipdlogsv2_groups where groupname='"+groupname+"'");
            if (rs.next()) { throw new Exception("Groupname already exists."); }
            int rc = stmt.executeUpdate("insert into wasup.ipdlogsv2_groups (groupname,groupdesc)"+
                    " values('"+groupname+"','"+groupdesc+"')");
            return "";
        //} catch (Exception i) {
        //    throw i;
        } catch (Throwable t) {
            t.printStackTrace();
            throw new Exception("Error adding Group:"+t.getMessage());
        } finally {
            try {
                conn.close();
            } catch (Throwable t) {}
            Logr.exit(method);
        }
    }

    public static String adminAddHost(String hostname, String hostuser, String hostpass, 
    		String hostdesc, String downloadtype) throws Exception {
        Connection conn = null;
        String method="DBBean.adminAddHost";
        Logr.entry(method);
        try {
            conn = getConn();
            conn.setAutoCommit(true);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from wasup.ipdlogsv2_hosts where hostname='"+hostname+"'");
            if (rs.next()) { throw new Exception("Hostname already exists."); }
            int rc = stmt.executeUpdate("insert into wasup.ipdlogsv2_hosts (hostname,hostuser,hostpass,hostdesc,downloadtype)"+
                    " values('"+hostname+"','"+hostuser+"','"+hostpass+"','"+hostdesc+"','"+downloadtype+"')");
            return "";
        //} catch (Exception i) {
        //    throw i;
        } catch (Throwable t) {
            t.printStackTrace();
            throw new Exception("Error adding Host:"+t.getMessage());
        } finally {
            try {
                conn.close();
            } catch (Throwable t) {}
            Logr.exit(method);
        }
    }

    // Not implemented yet
    public static Map adminListStatFiles() throws Exception {
        Connection conn = null;
        String method="DBBean.adminListStatFiles";
        Logr.entry(method);
        try {
            conn = getConn();
            conn.setAutoCommit(true);
            Statement stmt = conn.createStatement();
            //ResultSet rs = stmt.executeQuery("select * from wasup.ipdlogsv2_hosts where hostname='"+hostname+"'");
            //if (rs.next()) { throw new Exception("Hostname already exists."); }
            //int rc = stmt.executeUpdate("insert into wasup.ipdlogsv2_hosts (hostname,hostuser,hostpass,hostdesc)"+
            //        " values('"+hostname+"','"+hostuser+"','"+hostpass+"','"+hostdesc+"')");
            //return "";
            return null;
        //} catch (Exception i) {
        //    throw i;
        } catch (Throwable t) {
            t.printStackTrace();
            throw new Exception("Error adding Host:"+t.getMessage());
        } finally {
            try {
                conn.close();
            } catch (Throwable t) {}
            Logr.exit(method);
        }
    }


    public static String adminAddDir(String hostid, String dirname, String  dirdesc) throws Exception {
        System.out.println("adminAddDir");
        Connection conn = null;
        try {
            conn = getConn();
            conn.setAutoCommit(true);
            Statement stmt = conn.createStatement();
            //ResultSet rs = stmt.executeQuery("select * from wasup.ipdlogsv2_hosts where hostname='"+hostname+"'");
            //if (rs.next()) { throw new Exception("Hostname already exists."); }
            int rc = stmt.executeUpdate("insert into wasup.ipdlogsv2_dirs (dirhostid,dirname,dirdesc)"+
                    " values("+hostid+",'"+dirname+"','"+dirdesc+"')");
            return "";
        //} catch (Exception i) {
        //    throw i;
        } catch (Throwable t) {
            t.printStackTrace();
            throw new Exception("Error adding Host:"+t.getMessage());
        } finally {
            try {
                conn.close();
            } catch (Throwable t) {}
        }
    }

    public static boolean adminAddDirUser(String dirid, String userid, String description) throws Exception {
        String method="DBBean.adminAddDirUser";
        Logr.entry(method);
        Connection conn = null;
        try {
            conn = getConn();
            conn.setAutoCommit(true);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from wasup.ipdlogsv2_dirusers where dirid="+dirid+" and userid ='"+userid+"'");
            if (rs.next()) { throw new Exception("Userid already exists : "+userid); }
            String sql = "insert into wasup.ipdlogsv2_dirusers (dirid,userid,description)"+
                    " values("+dirid+",'"+userid.toLowerCase()+"','"+description+"')";
            Logr.debug(method, "SQL="+sql);
            int rc = stmt.executeUpdate(sql);
            if (rc == 1) {
                return true;
            } else {
                throw new Exception("Error adding dirid/userid combination. ("+dirid+"/"+userid.toLowerCase()+")");
            }
        //} catch (Exception i) {
        //    throw i;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
            throw new Exception("Error adding dirid/userid:"+t.getMessage());
        } finally {
            try {
                conn.close();
            } catch (Throwable t) {}
        }
    }
    
    public static TreeMap<String,ArrayList<String>> adminListHostTree(int hostid, String hostname) throws Exception {
        String method="DBBean.adminListHostTree";
        Logr.entry(method);
        Connection conn = null;
        TreeMap<String,ArrayList<String>> hostTree = new TreeMap<String,ArrayList<String>>();
        try {
            conn = getConn();
            conn.setAutoCommit(true);
            Statement stmt1 = conn.createStatement();
            String sql1 = "select * from wasup.ipdlogsv2_hosts where hostid="+hostid+" and hostname='"+hostname+"'";
            Logr.debug(method, "SQL1="+sql1);
            ResultSet rs1 = stmt1.executeQuery(sql1);
            if (!rs1.next()) { throw new Exception("HostID not found"); }
            
            Statement stmt2 = conn.createStatement();
            String sql2 = "select * from WASUP.IPDLOGSV2_DIRS where dirhostid = "+hostid;
            Logr.debug(method, "SQL2="+sql2);
            ResultSet rs2 = stmt2.executeQuery(sql2);
            while (rs2.next()) {
            	int dirid = rs2.getInt("dirid");
                Statement stmt3 = conn.createStatement();
                String sql3 = "select * from WASUP.IPDLOGSV2_DIRUSERS where dirid = "+dirid;
                Logr.debug(method, "SQL3="+sql3);
                ResultSet rs3 = stmt3.executeQuery(sql3);
                ArrayList<String> userlist = new ArrayList<String>();
                while (rs3.next()) {
                	userlist.add(rs3.getString("userid")+"  (" + rs3.getString("description")+")");
                }
                hostTree.put(rs2.getString("dirname")+"  ("+rs2.getInt("dirid")+")", userlist);		
            }
            return hostTree;
        //} catch (Exception i) {
        //    throw i;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
            throw new Exception("Error listing Host Tree:"+t.getMessage());
        } finally {
            try {
                conn.close();
            } catch (Throwable t) {}
        }
    }
    
    public static boolean adminDelHost(String username, int hostid, String hostname) throws Exception {
        String method="DBBean.adminDelHost";
        Logr.entry(method);
        Connection conn = null;
        //TreeMap<String,ArrayList<String>> hostTree = new TreeMap<String,ArrayList<String>>();
        try {
        	Logr.debug(method, "b4:hn="+hostname+",hid="+hostid);
        	        	
            conn = getConn();
            conn.setAutoCommit(true);
            Statement stmt1 = conn.createStatement();
            String sql1 = "select * from wasup.ipdlogsv2_hosts where hostid="+hostid+" and hostname='"+hostname+"'";
            Logr.debug(method, "SQL1="+sql1);
            ResultSet rs1 = stmt1.executeQuery(sql1);
            if (!rs1.next()) { throw new Exception("HostID not found"); }
            
            Statement stmt2 = conn.createStatement();
            String sql2 = "select * from WASUP.IPDLOGSV2_DIRS where dirhostid="+hostid;
            Logr.debug(method, "SQL2="+sql2);
            ResultSet rs2 = stmt2.executeQuery(sql2);
            if (rs2.next()) {
            	throw new Exception("HostName "+hostname+" could not be deleted. Not all Dirs are deleted");
            }
            String sql4 = "delete from WASUP.IPDLOGSV2_HOSTS where hostid="+hostid;
            Statement stmt4 = conn.createStatement();
            int rc4 = stmt4.executeUpdate(sql4);
            if (rc4 == 1) { Logr.journal(method, "UserID '"+username+"' deleted HOST '"+hostname+"'"); return true; }
            throw new Exception("HostName "+hostname+" could not be deleted.");
        //} catch (Exception i) {
        //    throw i;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
            throw new Exception("Error listing Host Tree:"+t.getMessage());
        } finally {
            try {
                conn.close();
            } catch (Throwable t) {}
        }
    }
    
    public static boolean adminDelHostDir(String username, int hostid, String hostname, String dirid) throws Exception {
        String method="DBBean.adminDelHostDir";
        Logr.entry(method);
        Connection conn = null;
        //TreeMap<String,ArrayList<String>> hostTree = new TreeMap<String,ArrayList<String>>();
        try {
        	Logr.debug(method, "b4:hn="+hostname+",hid="+hostid+",dirid="+dirid);
        	if (dirid.contains("(")) { dirid = dirid.substring(   dirid.indexOf("(")+1  ,   dirid.indexOf(")")  );}
        	Logr.debug(method, "at:hn="+hostname+",hid="+hostid+",dirid="+dirid);
        	        	
            conn = getConn();
            conn.setAutoCommit(true);
            Statement stmt1 = conn.createStatement();
            String sql1 = "select * from wasup.ipdlogsv2_hosts where hostid="+hostid+" and hostname='"+hostname+"'";
            Logr.debug(method, "SQL1="+sql1);
            ResultSet rs1 = stmt1.executeQuery(sql1);
            if (!rs1.next()) { throw new Exception("HostID not found"); }
            
            Statement stmt2 = conn.createStatement();
            String sql2 = "select * from WASUP.IPDLOGSV2_DIRS where dirhostid="+hostid+" and dirid = "+dirid;
            Logr.debug(method, "SQL2="+sql2);
            ResultSet rs2 = stmt2.executeQuery(sql2);
            //ArrayList<String> userlist = new ArrayList<String>();
            if (rs2.next()) {
            	String dirname = rs2.getString("dirname");
                Statement stmt3 = conn.createStatement();
                String sql3 = "select * from WASUP.IPDLOGSV2_DIRUSERS where dirid = "+dirid;
                Logr.debug(method, "SQL3="+sql3);
                ResultSet rs3 = stmt3.executeQuery(sql3);
                if (rs3.next()) {
                	Logr.debug(method, "SQL3 Users found");
                	throw new Exception("DirID "+dirid+" on HostName "+hostname+" could not be deleted. Not all Users are deleted");
                }
                String sql4 = "delete from WASUP.IPDLOGSV2_DIRS where dirhostid="+hostid+" and dirid = "+dirid;
                Statement stmt4 = conn.createStatement();
                int rc4 = stmt4.executeUpdate(sql4);
                if (rc4 == 1) { Logr.journal(method, "UserID '"+username+"' deleted DIR '"+dirname+"' on HOST '"+hostname+"'"); return true; }
            }
            throw new Exception("DirID "+dirid+" on HostName "+hostname+" could not be deleted.");
        //} catch (Exception i) {
        //    throw i;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
            throw new Exception("Error listing Host Tree:"+t.getMessage());
        } finally {
            try {
                conn.close();
            } catch (Throwable t) {}
        }
    }
    
    public static boolean adminDelHostDirUser(String username, int hostid, String hostname, String dirid, String userid) throws Exception {
        String method="DBBean.adminDelHostDirUser";
        Logr.entry(method);
        Connection conn = null;
        //TreeMap<String,ArrayList<String>> hostTree = new TreeMap<String,ArrayList<String>>();
        try {
        	Logr.debug(method, "b4:hn="+hostname+",hid="+hostid+",dirid="+dirid+","+userid);
        	if (userid.contains("(")) { userid = userid.substring(0,userid.indexOf("(")).trim();}
        	if (dirid.contains("(")) { dirid = dirid.substring(   dirid.indexOf("(")+1  ,   dirid.indexOf(")")  );}
        	Logr.debug(method, "at:hn="+hostname+",hid="+hostid+",dirid="+dirid+","+userid);
        	        	
            conn = getConn();
            conn.setAutoCommit(true);
            Statement stmt1 = conn.createStatement();
            String sql1 = "select * from wasup.ipdlogsv2_hosts where hostid="+hostid+" and hostname='"+hostname+"'";
            Logr.debug(method, "SQL1="+sql1);
            ResultSet rs1 = stmt1.executeQuery(sql1);
            if (!rs1.next()) { throw new Exception("HostID not found"); }
            
            Statement stmt2 = conn.createStatement();
            String sql2 = "select * from WASUP.IPDLOGSV2_DIRS where dirhostid="+hostid+" and dirid = "+dirid;
            Logr.debug(method, "SQL2="+sql2);
            ResultSet rs2 = stmt2.executeQuery(sql2);
            //ArrayList<String> userlist = new ArrayList<String>();
            if (rs2.next()) {
            	String dirname = rs2.getString("dirname");
                Statement stmt3 = conn.createStatement();
                String sql3 = "delete from WASUP.IPDLOGSV2_DIRUSERS where dirid = "+dirid+" and userid='"+userid+"'";
                Logr.debug(method, "SQL3="+sql3);
                int rc3 = stmt3.executeUpdate(sql3);
                if (rc3 == 1) {
                	Logr.journal(method, "UserID '"+username+"' deleted USERID '"+userid+"' for DIR '"+dirname+"' on HOST '"+hostname+"'");
                	return true;
                }
            }
            throw new Exception("UserID "+userid+" for DirID "+dirid+" on HostName "+hostname+" could not be deleted");
        //} catch (Exception i) {
        //    throw i;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
            throw new Exception("Error listing Host Tree:"+t.getMessage());
        } finally {
            try {
                conn.close();
            } catch (Throwable t) {}
        }
    }
    
    

    public static boolean addRawStats(String hostname, String rawjavastats, String rawlparstats, String rawfsstats, String oslevel) {
        Connection conn = null;
        String method="DBBean.addRawStats";
        Logr.entry(method);
        try {
            conn = getConn();
            conn.setAutoCommit(true);
            Statement stmt = conn.createStatement();
            String aDate = new java.sql.Date(System.currentTimeMillis()).toString();
            String sql1 = "select * from wasup.ipdlogsv2_hostshist where hostname='"+hostname+"' and datum='"+aDate+"'";
            ResultSet rs = stmt.executeQuery(sql1);
            if (rs.next()) {
                String sql="update wasup.ipdlogsv2_hostshist set "+
                        "rawjavastats='"+rawjavastats+"', "+
                        "rawlparstats='"+rawlparstats+"', "+
                        "rawfsstats='"+rawfsstats+"', "+
                        "oslevel='"+oslevel+"' "+
                        "where hostname='"+hostname+"' and datum='"+aDate+"'";
                Logr.debug(method, "SQL("+sql.length()+")="+"sql");
                int rc = stmt.executeUpdate(sql);
                if (rc==1) return true;
            } else {
                String sql="insert into wasup.ipdlogsv2_hostshist (hostname,datum,rawjavastats, rawlparstats, rawfsstats, oslevel)"+
                    " values('"+hostname+"','"+aDate+"',"+
                    "'"+rawjavastats+"','"+rawlparstats+"','"+rawfsstats+"','"+oslevel+"')";
                Logr.debug(method, "SQL("+sql.length()+")="+sql);
                int rc = stmt.executeUpdate(sql);
                if (rc==1) return true;
            }
            return false;
        //} catch (Exception i) {
        //    throw i;
        } catch (Throwable t) {
            t.printStackTrace();
            Logr.error(method,"Error adding Hostinfo:"+t.getMessage());
            return false;
        } finally {
            try {
                conn.close();
            } catch (Throwable t) {}
            Logr.exit(method);
        }
    }


    
    public static Connection getConn() throws Exception {
        try {
            /*if (Constants.DBTYPE.equalsIgnoreCase("jdbc")) {
                Context ic = new InitialContext();
                Context ec = (Context) ic.lookup("java:comp/env");
                DataSource ds = (DataSource)ec.lookup(Constants.DBJDBC);
                Connection conn = ds.getConnection();
                return conn;
            } else {*/

                Driver t_Driver = (Driver)Class.forName("com.ibm.db2.jcc.DB2Driver").newInstance();
                //Driver t_Driver = (Driver)Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
                Connection conn = DriverManager.getConnection(IPDLogsMain.DBLOCALURL,IPDLogsMain.DBLOCALUSER,IPDLogsMain.DBLOCALPASS);
                return conn;
            //}
        //} catch (Exception i) {
        //    throw i;
        } catch (Throwable t) {
            t.printStackTrace();
            throw new Exception(DBMSG002+"\n"+t.getMessage());
        }
    }
    public static Connection getConnLocal() throws Exception {
        try {
            Driver t_Driver = (Driver)Class.forName("com.ibm.db2.jcc.DB2Driver").newInstance();
            //Driver t_Driver = (Driver)Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
            Connection conn = DriverManager.getConnection(IPDLogsMain.DBLOCALURL,IPDLogsMain.DBLOCALUSER,IPDLogsMain.DBLOCALPASS);
            return conn;
        //} catch (Exception i) {
        //    throw i;
        } catch (Throwable t) {
            t.printStackTrace();
            throw new Exception(DBMSG002+"\n"+t.getMessage());
        }
    }
}
