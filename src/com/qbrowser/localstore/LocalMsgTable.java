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

package com.qbrowser.localstore;

import com.qbrowser.QBrowserV2;
import com.qbrowser.util.QBrowserUtil;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author takemura
 */
public class LocalMsgTable extends AbstractTableModel {

    //public static ResourceBundle resources = ResourceBundle.getBundle("com.qbrowser.resourcebase");
    static ResourceBundle resources = QBrowserV2.resources;

        final String[] columnNames =
                {"#", resources.getString("qkey.table.header.msgid"),
            resources.getString("qkey.table.header.timestamp"),
            resources.getString("qkey.table.header.type"),
            resources.getString("qkey.table.header.size"),
            resources.getString("qkey.table.header.mode"),
            resources.getString("qkey.table.header.priority")};
        SimpleDateFormat df =
                new SimpleDateFormat("yyyy/MM/dd kk:mm:ss z");
        LinkedList list = null;

        //MsgId / 実Row番号マップ
        public HashMap msgids = new HashMap();

        public int getRealRowNoFromMsgId(String msgid) {
            Integer iiv = (Integer)msgids.get(msgid);
            int retval = -1;
            if (iiv != null) {
                retval = iiv.intValue();
            }

            return retval;
        }

        public int getRowCount() {
            if (list == null) {
                return 0;
            } else {
                return list.size();
            }
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        public void setDisplayNumberAt(int number , int row) {
            if (list == null) return;

            if (list.size() <= row) {
                return;
            }

            LocalMessageContainer mc = (LocalMessageContainer) list.get(row);
            mc.setDisplaynumber(number);


        }

        public Object getValueAt(int row, int column) {
            if (list == null) {
                return null;
            }

            if (list.size() <= row) {
                return null;
            }

            LocalMessageContainer mc = (LocalMessageContainer) list.get(row);
            //Message m = mc.getMessage();
            //実Messageよりもメタ情報優先
            //String source_path = mc.getReal_file_path();


            if (mc == null) {
                return "null";
            }

                switch (column) {
                    case 0:
                        // MessageID
                        return mc.getDisplaynumber();
                    case 1:
                        // MessageID
                        return mc.getVmsgid();
                    case 2:
                        // Need to format into date/time
                        return df.format(new Date(mc.getVtimestamp()));
                    case 3:
                        return mc.getMessage_type();
                    case 4:
                        return QBrowserUtil.messageBodyLengthAsString(mc.getBody_size());
                    case 5:
                        // Delivery mode
                        //int mode = m.getJMSDeliveryMode();
                        int mode = mc.getVdeliverymode();
                        if (mode == DeliveryMode.PERSISTENT) {
                            return QBrowserV2.PERSISTENT;
                        } else if (mode == DeliveryMode.NON_PERSISTENT) {
                            return QBrowserV2.NONPERSISTENT;
                        } else {
                            return String.valueOf(mode) + "?";
                        }
                    case 6:
                        // Priority
                        return new Integer(mc.getVpriority());
                    default:
                        return "Bad column value: " + column;
                }
        }

        /**
         * Load and enumeration of messages into the table
         */
       public int load(Enumeration e) {
            if (e == null) {
                return 0;
            }

            list = new LinkedList();

            while (e.hasMoreElements()) {
                LocalMessageContainer mc = (LocalMessageContainer)e.nextElement();
                mc.setDisplaynumber(list.size());
                list.add(mc);
                msgids.put(mc.getVmsgid(), list.size() - 1);
            }

            fireTableDataChanged();

            return list.size();
        }

        public void init() {

            list = new LinkedList();
        }

        public void add_one_row_ifexists_update(LocalMessageContainer imsg) {

                String smsgid = imsg.getVmsgid();
                if (msgids.containsKey(smsgid)) {

                    for (int i = 0; i < list.size(); i++) {
                        LocalMessageContainer mc = (LocalMessageContainer) list.get(i);
                        if (mc.getVmsgid().equals(smsgid)) {
                            list.remove(i);
                            break;
                        }

                    }

                }

                imsg.setDisplaynumber(list.size());
                list.add(imsg);
                msgids.put(imsg.getVmsgid(), list.size() - 1);

                fireTableDataChanged();

        }

        public void add_one_row(LocalMessageContainer imsg) {
            imsg.setDisplaynumber(list.size());
            list.add(imsg);
            msgids.put(imsg.getVmsgid(), list.size() - 1);
            fireTableDataChanged();
        }

        public void deleteMessageAtRow(int row) {
            if (list == null) {
                return;
            }

            LocalMessageContainer mc = (LocalMessageContainer)list.get(row);
            msgids.remove(mc.getVmsgid());
            list.remove(row);
            mc = null;
            fireTableDataChanged();

        }

        public LocalMessageContainer getMessageAtRow(int row) {
            if (list == null) {
                return null;
            }
            return (LocalMessageContainer)list.get(row);
        }
    }
