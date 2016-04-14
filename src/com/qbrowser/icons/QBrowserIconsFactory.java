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

package com.qbrowser.icons;

import com.jidesoft.icons.IconsFactory;

import javax.swing.*;

public class QBrowserIconsFactory {


        public final static String Flagbase = "icons/flag";
        public final static String Refresh = "icons/refresh16.png";
        public final static String Forward = "icons/forward16.png";
        public final static String Connect = "icons/connect16.png";
        public final static String Disconnect = "icons/disconnect16.png";
        public final static String Recycle = "icons/recycle16.png";
        public final static String Move = "icons/move16.png";
        public final static String Save = "icons/save16.png";
        public final static String NewMessageFromFile = "icons/newmessagef16.png";
        public final static String OpenFile = "icons/open16.png";
        public final static String OpenMultiFile = "icons/openmulti16.png";
        public final static String Confirm = "icons/confirm16.png";
        public final static String Playing = "icons/playing16.png";
        public final static String Stopped = "icons/stopped16.png";
        public final static String Copyin = "icons/copyin16.png";
        public final static String RecordList = "icons/recordlist16.png";
        public final static String Copy = "icons/copy16.png";
        public final static String Paste = "icons/paste16.png";
        public final static String Globe = "icons/globe16.png";
        public final static String FolderOpen = "icons/folder_open.png";
        public final static String FolderClose = "icons/folder.png";
        public final static String Shutdown = "icons/shutdown16.png";
        public final static String Restart = "icons/restart16.png";
        public final static String Paused_queue = "icons/flag1_p.png";
        public final static String Paused_topic = "icons/flag4_p.png";
        public final static String Paused_localstore = "icons/flag7_p.png";
        public final static String Subscribed_topic = "icons/flag4_s.png";
        public final static String Notsubscribed_topic = "icons/flag4_ns.png";

        public final static String QBIcon = "icons/network32.png";
        public final static String EXIT = "icons/close16.png";
        public final static String NewMsg = "icons/newmessage16.png";
        public final static String ChkAll = "icons/checkall16.png";
        public final static String DelMsg = "icons/trashb16.png";
        public final static String ConnList = "icons/conn_list16.png";
        public final static String SvcList = "icons/service_list16.png";

        public final static String SvcDetails = "icons/service_details16.png";
        public final static String ConfigPrinter = "icons/config_printer16.png";
        public final static String ClientVersion = "icons/infoabout16.png";
        public final static String AtesakiDetails = "icons/atesaki_details16.png";
        public final static String PurgeDest = "icons/trash16.png";
        public final static String ListAtesaki = "icons/list_atesaki16.png";
        public final static String CmdInput = "icons/cmd_input16.png";
        public final static String FilteredTxn = "icons/filter_txn16.png";
        public final static String AllTxn = "icons/all_txn16.png";
        public final static String BkrDetails = "icons/broker_details16.png";
        public final static String Download = "icons/download16.png";
        public final static String Upload = "icons/upload16.png";
        public final static String Subscribe = "icons/subscribe16.png";
        public final static String MsgTable = "icons/tablePane16.png";



    public static ImageIcon getImageIcon(String name) {
        if (name != null)
            return IconsFactory.getImageIcon(QBrowserIconsFactory.class, name);
        else
            return null;
    }

    

    public static void main(String[] argv) {
        IconsFactory.generateHTML(QBrowserIconsFactory.class);
    }


}

