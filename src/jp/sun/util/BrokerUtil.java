/* Copyright (C) 2000-2009

   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; version 2 of the License.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA */

package jp.sun.util;

import com.sun.messaging.jmq.admin.apps.console.event.*;
import com.sun.messaging.jmq.admin.bkrutil.BrokerAdmin;
import com.sun.messaging.jmq.util.admin.ServiceInfo;
import java.io.File;
import java.io.PrintStream;
import java.util.*;

// Referenced classes of package jp.sun.util:
//            ConsoleBrokerAdminManagerA

public class BrokerUtil
    implements AdminEventListener
{

    private BrokerAdmin ba;

    public BrokerUtil()
    {
    }

    public static StringBuffer printAllProperties(String hostname, String port, String userid, String password) 
    {
        
        StringBuffer sb = new StringBuffer();

        try
        {
            
           
            
            BrokerUtil bu = new BrokerUtil();
            sb.append("BrokerAdmin instance creating...").append("\n");
            bu.initBrokerAdmin(hostname, port, userid, password);
            sb.append("BrokerAdmin instance created.").append("\n");
            sb.append("starting connect...").append("\n");
            try
            {
                bu.ba.connect();
            }
            catch(Exception cone)
            {
                sb.append((new StringBuilder()).append("MQ Broker on ").append(bu.ba.getBrokerHost()).append(":").append(bu.ba.getBrokerPort()).append(" STOPPED").toString()).append("\n");
                //System.exit(1);
                return sb;
            }
            sb.append("connect completed.").append("\n");
            sb.append((new StringBuilder()).append("defaulttimeout : ").append(bu.ba.getDefaultTimeout()).toString()).append("\n");
            bu.ba.sendGetBrokerPropsMessage();
            Properties bproperties = bu.ba.receiveGetBrokerPropsReplyMessage(false);
            TreeMap tm = new TreeMap();
            String k;
            String v;
            for(Enumeration ekey = bproperties.keys(); ekey.hasMoreElements(); tm.put(k, v))
            {
                k = (String)ekey.nextElement();
                v = (String)bproperties.get(k);
            }

            String mk;
            String mv;
            for(Iterator itm = tm.entrySet().iterator(); itm.hasNext(); sb.append((new StringBuilder()).append(mk).append(" = ").append(mv).toString()).append("\n"))
            {
                java.util.Map.Entry me = (java.util.Map.Entry)itm.next();
                mk = (String)me.getKey();
                mv = (String)me.getValue();
            }

            bu.ba.sendGetServicesMessage(null);
            Vector vector = bu.ba.receiveGetServicesReplyMessage();
            ServiceInfo serviceinfo;
            for(Enumeration enumeration = vector.elements(); enumeration.hasMoreElements(); sb.append((new StringBuilder()).append(serviceinfo.name).append(" : ").append(bu.getState(serviceinfo.state)).toString()).append("\n"))
            {
                serviceinfo = (ServiceInfo)enumeration.nextElement();
            }

            bu.ba.forceClose();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        return sb;
    }

    public String getState(int i)
    {
        if(i == 4)
        {
            return "PAUSED";
        }
        if(i == 3)
        {
            return "RUNNING";
        } else
        {
            return "UNKNOWN";
        }
    }

    public void initBrokerAdmin(String hostname, String port, String userid, String password)
    {
        try
        {
            ConsoleBrokerAdminManagerA cba = new ConsoleBrokerAdminManagerA();
            
            cba.setHostName(hostname);
            cba.setPort(port);
            cba.setUserid(userid);
            cba.setPassword(password);
            
            cba.readBrokerAdminsFromFile();
            Vector ve = cba.getBrokerAdmins();
            //System.out.println((new StringBuilder()).append("count of brokeradmin : ").append(ve.size()).toString());
            if(ve.size() != 0)
            {
                ba = (BrokerAdmin)ve.firstElement();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void adminEventDispatched(AdminEvent adminevent)
    {
        if(!(adminevent instanceof DialogEvent) && !(adminevent instanceof BrokerAdminEvent) && !(adminevent instanceof BrokerErrorEvent))
        {
            if(!(adminevent instanceof BrokerCmdStatusEvent));
        }
    }
}
