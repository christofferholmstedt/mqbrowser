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

import com.qbrowser.ActiveMQBrowser;
import com.qbrowser.container.MessageContainer;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.jms.Queue;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Topic;

/**
 *
 * @author takemura
 */
public class ReadOnlyActiveMQHeaderPropertyTable extends ReadOnlyHeaderPropertyTable {

    public ReadOnlyActiveMQHeaderPropertyTable(int row) {
        super(row);
    }

    @Override
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
                    s1 = ActiveMQBrowser.QUEUE_PREFIX + ((Queue) d1).getQueueName();
                } else {
                    s1 = ActiveMQBrowser.TOPIC_PREFIX + ((Topic) d1).getTopicName();
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
                    s = ActiveMQBrowser.QUEUE_PREFIX + ((Queue) d).getQueueName();
                } else {
                    s = ActiveMQBrowser.TOPIC_PREFIX + ((Topic) d).getTopicName();
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

            /*
            org.apache.activemq.command.Message acm = (org.apache.activemq.command.Message)message;
            int activemq_group_sequence = acm.getGroupSequence();
            Property jags = new Property();
            jags.setKey("Message Group Sequence Number");
            jags.setProperty_type(Property.INT_TYPE);
            jags.setProperty_value(activemq_group_sequence);
            list.add(jags);

            String activemq_groupid = acm.getGroupID();
            Property jagid = new Property();
            jagid.setKey("Message Group");
            jagid.setProperty_type(Property.STRING_TYPE);
            jagid.setProperty_value(activemq_groupid);
            list.add(jagid);  
            */

            //AdditionalPropertyから引っ張ってくる
            ArrayList ahp = mc.getAdditionalHeaders();
            for (int i = 0; i < ahp.size(); i++) {
                list.add((Property)ahp.get(i));
            }


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





            fireTableDataChanged();

        } catch (JMSException jmse) {
            //NOP
        }

        return list.size();

    }

}
