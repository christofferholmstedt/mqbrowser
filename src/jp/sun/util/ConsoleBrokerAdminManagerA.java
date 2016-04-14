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

import com.sun.messaging.jmq.admin.apps.console.BrokerListProperties;
import com.sun.messaging.jmq.admin.bkrutil.BrokerAdmin;
import com.sun.messaging.jmq.admin.bkrutil.BrokerAdminException;
import com.sun.messaging.jmq.admin.util.UserPropertiesException;
import java.util.Vector;

public class ConsoleBrokerAdminManagerA
{

    private String fileName;
    private String dirName;
    private Vector admins;
    
    private String hostName;
    private String port;
    private String userid;
    private String password;

    public ConsoleBrokerAdminManagerA()
    {
        fileName = "brokerlist.properties";
        admins = new Vector();
    }

    public void addBrokerAdmin(BrokerAdmin brokeradmin)
    {
        admins.addElement(brokeradmin);
    }

    public void deleteBrokerAdmin(BrokerAdmin brokeradmin)
    {
        String s = brokeradmin.getKey();
        for(int i = 0; i < admins.size(); i++)
        {
            BrokerAdmin brokeradmin1 = (BrokerAdmin)admins.get(i);
            String s1 = brokeradmin1.getKey();
            if(s.equals(s1))
            {
                admins.remove(i);
                return;
            }
        }

    }

    public void readBrokerAdminsFromFile()
        throws UserPropertiesException, BrokerAdminException
    {
        BrokerListProperties brokerlistproperties = readFromFile();
        int i = brokerlistproperties.getBrokerCount();
        for(int j = 0; j < i; j++)
        {
            BrokerAdmin brokeradmin = brokerlistproperties.getBrokerAdmin(j);
            addBrokerAdmin(brokeradmin);
        }

    }

    public void writeBrokerAdminsToFile()
        throws UserPropertiesException
    {
        BrokerListProperties brokerlistproperties = new BrokerListProperties();
        for(int i = 0; i < admins.size(); i++)
        {
            BrokerAdmin brokeradmin = (BrokerAdmin)admins.get(i);
            brokerlistproperties.addBrokerAdmin(brokeradmin);
        }

        writeToFile(brokerlistproperties);
    }

    public Vector getBrokerAdmins()
    {
        return admins;
    }

    public boolean exist(String s)
    {
        for(int i = 0; i < admins.size(); i++)
        {
            BrokerAdmin brokeradmin = (BrokerAdmin)admins.get(i);
            String s1 = brokeradmin.getKey();
            if(s.equals(s1))
            {
                return true;
            }
        }

        return false;
    }

    public void setFileName(String s)
    {
        fileName = s;
    }

    public void setDirName(String value)
    {
        dirName = value;
    }

    private BrokerListProperties readFromFile()
        throws UserPropertiesException
    {
        BrokerListProperties brokerlistproperties = new BrokerListProperties();
        brokerlistproperties.setProperty("broker0.userName",userid);
        brokerlistproperties.setProperty("broker.count","1");
        brokerlistproperties.setProperty("version","2.0");
        brokerlistproperties.setProperty("broker0.key","Broker");
        brokerlistproperties.setProperty("broker0.password",password);
        brokerlistproperties.setProperty("broker0.attrs.imqBrokerHostPort", port);
        brokerlistproperties.setProperty("broker0.attrs.imqBrokerHostName",hostName);
        
        return brokerlistproperties;
    }

    private void writeToFile(BrokerListProperties brokerlistproperties)
        throws UserPropertiesException
    {
        return;
    }

    public String getSomething()
    {
        return "takemura";
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
