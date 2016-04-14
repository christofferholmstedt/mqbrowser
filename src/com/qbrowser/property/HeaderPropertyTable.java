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

package com.qbrowser.property;

import com.qbrowser.QBrowserV2;
import com.qbrowser.container.MessageContainer;
import com.qbrowser.localstore.LocalMessageContainer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import javax.jms.Queue;
import java.util.ResourceBundle;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Topic;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author takemura
 */
public class HeaderPropertyTable extends  DefaultTableModel {

    //public static ResourceBundle resources = ResourceBundle.getBundle("com.qbrowser.resourcebase");
    static ResourceBundle resources = QBrowserV2.resources;
    public static final int DEFAULT_JMSPRIORITY = 4;

        final String[] columnNames =
                {resources.getString("qkey.proptable.jmsheader.key"),
            resources.getString("qkey.proptable.jmsheader.prop_value")};

        LinkedList list = null;

        ArrayList type_selection = null;

        public HeaderPropertyTable() {
            init();
            add_one_row(new Property());
            
        }

        public HeaderPropertyTable(int number_of_initial_rows) {
            init();
            for (int i = 0 ; i < number_of_initial_rows; i++) {
               add_one_row(new Property());
            }

        }

    @Override
        public int getRowCount() {
            if (list == null) {
                return 0;
            } else {
                return list.size();
            }
        }

    @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        
        @Override
        public Class<?> getColumnClass(int column) {
            switch (column) {
                case 0:
                    return String.class;
                case 1:
                    return Object.class;
            }
            return Object.class;
        }
        

                @Override
                public boolean isCellEditable(int row, int column) {
                    return true;
                }

                public Class<?> getCellClassAt(int row, int column) {
                    return getColumnClass(column);
                }

        @Override
        public void setValueAt(Object value, int row, int column) {
            if (list == null) {
                return;
            }

            if (list.size() < row) {
                return;
            }

            Property p = (Property) list.get(row);
            if (p == null) {
                return;
            }

            try {
                switch (column) {
                    case 0:
                        // Property KEY
                        if (value instanceof String) {
                          p.setKey((String)value);
                        }
                        return;

                    case 1:
                        // property VALUE
                        p.setProperty_value(value);
                        return;

                    default:
                        return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }



        }

        @Override
        public Object getValueAt(int row, int column) {
            if (list == null) {
                return null;
            }

            Property p = (Property) list.get(row);

            if (p == null) {
                return "";
            }

            try {
                switch (column) {
                    case 0:
                        // Property KEY
                        return p.getKey();

                    case 1:
                        // property VALUE
                        return p.getProperty_value();

                    default:
                        return "Bad column value: " + column;
                }
            } catch (Exception e) {
                return ("Error: " + e);
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
            list.add(e.nextElement());
        }

        fireTableDataChanged();

        return list.size();
    }

    public int load(MessageContainer mc) {
        if (mc == null)
            return 0;
        
        Message message = mc.getMessage();

        if (message == null) {
            return 0;
        }

        list = new LinkedList();

        try {

            long jexp = message.getJMSExpiration();

            if (jexp != 0) {
                Property jexp_prop = new Property();
                jexp_prop.setKey("JMSExpiration");
                jexp_prop.setProperty_type(Property.LONG_TYPE);
                jexp_prop.setProperty_value(jexp);
                list.add(jexp_prop);
            }

            Destination d = message.getJMSReplyTo();
            String s = null;
            if (d != null) {
                
                if (d instanceof Queue) {
                    s = ((Queue) d).getQueueName() + QBrowserV2.QUEUE_SUFFIX;
                } else {
                    s = ((Topic) d).getTopicName() + QBrowserV2.TOPIC_SUFFIX;
                }
            }

            if (s != null) {
                Property jrepto = new Property();
                jrepto.setKey("JMSReplyTo");
                jrepto.setProperty_type(Property.STRING_TYPE);
                jrepto.setProperty_value(s);
                list.add(jrepto);
            }

            String jcorid = message.getJMSCorrelationID();

            if ((jcorid != null) && (jcorid.length() > 0)) {
                Property jcoridp = new Property();
                jcoridp.setKey("JMSCorrelationID");
                jcoridp.setProperty_type(Property.STRING_TYPE);
                jcoridp.setProperty_value(jcorid);
                list.add(jcoridp);
            }

            int jpri = message.getJMSPriority();

            if (jpri != DEFAULT_JMSPRIORITY) {
                Property jprip = new Property();
                jprip.setKey("JMSPriority");
                jprip.setProperty_type(Property.INT_TYPE);
                jprip.setProperty_value(jpri);
                list.add(jprip);
            }

            String jtype = message.getJMSType();

            if ((jtype != null) && (jtype.length() > 0)) {
                Property jtypep = new Property();
                jtypep.setKey("JMSType");
                jtypep.setProperty_type(Property.STRING_TYPE);
                jtypep.setProperty_value(jtype);
                list.add(jtypep);
            }

            fireTableDataChanged();

        } catch (JMSException jmse) {
            //NOP
            }

        return list.size();

    }

    public int load(LocalMessageContainer message) {
        if (message == null) {
            return 0;
        }

        list = new LinkedList();

        try {

            long jexp = message.getVexpiration();

            if (jexp != 0) {
                Property jexp_prop = new Property();
                jexp_prop.setKey("JMSExpiration");
                jexp_prop.setProperty_type(Property.LONG_TYPE);
                jexp_prop.setProperty_value(jexp);
                list.add(jexp_prop);
            }

            Destination d = message.getVreplyto();
            String s = null;
            if (d != null) {
                if (d instanceof Queue) {
                    s = ((Queue) d).getQueueName() + QBrowserV2.QUEUE_SUFFIX;
                } else {
                    s = ((Topic) d).getTopicName() + QBrowserV2.TOPIC_SUFFIX;
                }
            }

            if (s != null) {
                Property jrepto = new Property();
                jrepto.setKey("JMSReplyTo");
                jrepto.setProperty_type(Property.STRING_TYPE);
                jrepto.setProperty_value(s);
                list.add(jrepto);
            }

            String jcorid = message.getVcorrelationid();

            if ((jcorid != null) && (jcorid.length() > 0)) {
                Property jcoridp = new Property();
                jcoridp.setKey("JMSCorrelationID");
                jcoridp.setProperty_type(Property.STRING_TYPE);
                jcoridp.setProperty_value(jcorid);
                list.add(jcoridp);
            }

            int jpri = message.getVpriority();

            if (jpri != DEFAULT_JMSPRIORITY) {
                Property jprip = new Property();
                jprip.setKey("JMSPriority");
                jprip.setProperty_type(Property.INT_TYPE);
                jprip.setProperty_value(jpri);
                list.add(jprip);
            }

            String jtype = message.getVjms_type();

            if ((jtype != null) && (jtype.length() > 0)) {
                Property jtypep = new Property();
                jtypep.setKey("JMSType");
                jtypep.setProperty_type(Property.STRING_TYPE);
                jtypep.setProperty_value(jtype);
                list.add(jtypep);
            }

            ArrayList ahp = message.getAdditionalHeaders();
            for (int i = 0; i < ahp.size(); i++) {
                list.add((Property)ahp.get(i));
            }

            fireTableDataChanged();

        } catch (JMSException jmse) {
            //NOP
            }

        return list.size();

    }

    public int loadAllProperties(MessageContainer mc) {
        if (mc == null) {
            return 0;
        }

        list = new LinkedList();

        try {

            Message message = mc.getMessage();

            String msgid = message.getJMSMessageID();
            Property msgidp = new Property();
            msgidp.setKey("JMSMessageID");
            msgidp.setProperty_type(Property.STRING_TYPE);
            msgidp.setProperty_value(msgid);
            list.add(msgidp);

            Destination d1 = mc.getVdest();
            String s1 = null;
            if (d1 != null) {

                if (d1 instanceof Queue) {
                    s1 = ((Queue) d1).getQueueName() + QBrowserV2.QUEUE_SUFFIX;
                } else {
                    s1 = ((Topic) d1).getTopicName() + QBrowserV2.TOPIC_SUFFIX;
                }
            } else {
                s1 = "";
            }


            Property jdestp = new Property();
            jdestp.setKey("JMSDestination");
            jdestp.setProperty_type(Property.STRING_TYPE);
            jdestp.setProperty_value(s1);
            list.add(jdestp);


            Destination d = mc.getVreplyto();
            String s = null;
            if (d != null) {

                if (d instanceof Queue) {
                    s = ((Queue) d).getQueueName() + QBrowserV2.QUEUE_SUFFIX;
                } else {
                    s = ((Topic) d).getTopicName() + QBrowserV2.TOPIC_SUFFIX;
                }
            } else {
                s = "";
            }


            Property jrepto = new Property();
            jrepto.setKey("JMSReplyTo");
            jrepto.setProperty_type(Property.STRING_TYPE);
            jrepto.setProperty_value(s);
            list.add(jrepto);


            String jcorid = message.getJMSCorrelationID();
            Property jcoridp = new Property();
            jcoridp.setKey("JMSCorrelationID");
            jcoridp.setProperty_type(Property.STRING_TYPE);
            jcoridp.setProperty_value(jcorid);
            list.add(jcoridp);


            int delivermode = message.getJMSDeliveryMode();
            Property delivermodep = new Property();
            delivermodep.setKey("JMSDeliverMode");
            delivermodep.setProperty_type(Property.INT_TYPE);
            delivermodep.setProperty_value(delivermode);
            list.add(delivermodep);

            int jpri = message.getJMSPriority();
            Property jprip = new Property();
            jprip.setKey("JMSPriority");
            jprip.setProperty_type(Property.INT_TYPE);
            jprip.setProperty_value(jpri);
            list.add(jprip);


            long jexp = message.getJMSExpiration();
            Property jexp_prop = new Property();
            jexp_prop.setKey("JMSExpiration");
            jexp_prop.setProperty_type(Property.LONG_TYPE);
            jexp_prop.setProperty_value(jexp);
            list.add(jexp_prop);


            String jtype = message.getJMSType();
            Property jtypep = new Property();
            jtypep.setKey("JMSType");
            jtypep.setProperty_type(Property.STRING_TYPE);
            jtypep.setProperty_value(jtype);
            list.add(jtypep);


            boolean redelivered = message.getJMSRedelivered();
            Property redvd = new Property();
            redvd.setKey("JMSRedelivered");
            redvd.setProperty_type(Property.BOOLEAN_TYPE);
            redvd.setProperty_value(redelivered);
            list.add(redvd);


            long jtimestamp = message.getJMSTimestamp();
            Property jtimestampp = new Property();
            jtimestampp.setKey("JMSTimestamp");
            jtimestampp.setProperty_type(Property.LONG_TYPE);
            jtimestampp.setProperty_value(jtimestamp);
            list.add(jtimestampp);

            ArrayList ahp = mc.getAdditionalHeaders();
            for (int i = 0; i < ahp.size(); i++) {
                list.add((Property)ahp.get(i));
            }


            fireTableDataChanged();

        } catch (JMSException jmse) {
            //NOP
            }

        return list.size();

    }


    public int loadAllProperties(LocalMessageContainer message) {
        if (message == null) {
            return 0;
        }

        list = new LinkedList();

        try {

            String msgid = message.getVmsgid();
            Property msgidp = new Property();
            msgidp.setKey("JMSMessageID");
            msgidp.setProperty_type(Property.STRING_TYPE);
            msgidp.setProperty_value(msgid);
            list.add(msgidp);

            Destination d1 = message.getVdest();
            String s1 = null;
            if (d1 != null) {
                if (d1 instanceof Queue) {
                    s1 = ((Queue) d1).getQueueName() + QBrowserV2.QUEUE_SUFFIX;
                } else {
                    s1 = ((Topic) d1).getTopicName() + QBrowserV2.TOPIC_SUFFIX;
                }
            } else {
                s1 = "";
            }


            Property jdestp = new Property();
            jdestp.setKey("JMSDestination");
            jdestp.setProperty_type(Property.STRING_TYPE);
            jdestp.setProperty_value(s1);
            list.add(jdestp);


            Destination d = message.getVreplyto();
            String s = null;
            if (d != null) {
                if (d instanceof Queue) {
                    s = ((Queue) d).getQueueName() + QBrowserV2.QUEUE_SUFFIX;
                } else {
                    s = ((Topic) d).getTopicName() + QBrowserV2.TOPIC_SUFFIX;
                }
            } else {
                s = "";
            }


            Property jrepto = new Property();
            jrepto.setKey("JMSReplyTo");
            jrepto.setProperty_type(Property.STRING_TYPE);
            jrepto.setProperty_value(s);
            list.add(jrepto);


            String jcorid = message.getVcorrelationid();
            Property jcoridp = new Property();
            jcoridp.setKey("JMSCorrelationID");
            jcoridp.setProperty_type(Property.STRING_TYPE);
            jcoridp.setProperty_value(jcorid);
            list.add(jcoridp);


            int delivermode = message.getVdeliverymode();
            Property delivermodep = new Property();
            delivermodep.setKey("JMSDeliverMode");
            delivermodep.setProperty_type(Property.INT_TYPE);
            delivermodep.setProperty_value(delivermode);
            list.add(delivermodep);

            int jpri = message.getVpriority();
            Property jprip = new Property();
            jprip.setKey("JMSPriority");
            jprip.setProperty_type(Property.INT_TYPE);
            jprip.setProperty_value(jpri);
            list.add(jprip);


            long jexp = message.getVexpiration();
            Property jexp_prop = new Property();
            jexp_prop.setKey("JMSExpiration");
            jexp_prop.setProperty_type(Property.LONG_TYPE);
            jexp_prop.setProperty_value(jexp);
            list.add(jexp_prop);


            String jtype = message.getVjms_type();
            Property jtypep = new Property();
            jtypep.setKey("JMSType");
            jtypep.setProperty_type(Property.STRING_TYPE);
            jtypep.setProperty_value(jtype);
            list.add(jtypep);


            boolean redelivered = message.isVredelivered();
            Property redvd = new Property();
            redvd.setKey("JMSRedelivered");
            redvd.setProperty_type(Property.BOOLEAN_TYPE);
            redvd.setProperty_value(redelivered);
            list.add(redvd);


            long jtimestamp = message.getVtimestamp();
            Property jtimestampp = new Property();
            jtimestampp.setKey("JMSTimestamp");
            jtimestampp.setProperty_type(Property.LONG_TYPE);
            jtimestampp.setProperty_value(jtimestamp);
            list.add(jtimestampp);


            ArrayList ahp = message.getAdditionalHeaders();
            for (int i = 0; i < ahp.size(); i++) {
                list.add((Property)ahp.get(i));
            }


            fireTableDataChanged();

        } catch (JMSException jmse) {
            //NOP
            }

        return list.size();

    }

        void init() {

            list = new LinkedList();
            
        }

        public void add_one_row(Property value) {
            list.add(value);
            fireTableDataChanged();
        }

        public void add_one_empty_row() {
            list.add(new Property());
            fireTableDataChanged();
        }

        public void deletePropertyAtRow(int row) {
            if (list == null) {
                return;
            }

            list.remove(row);
            fireTableDataChanged();

        }

        public Property getPropertyAtRow(int row) {
            if (list == null) {
                return null;
            }
            return ((Property) list.get(row));
        }
    }
