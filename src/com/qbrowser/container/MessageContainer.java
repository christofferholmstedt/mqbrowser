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

package com.qbrowser.container;

import com.qbrowser.QBrowserV2;
import com.qbrowser.util.QBrowserUtil;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueBrowser;

/**
 *
 * @author takemura
 */
public class MessageContainer {
    int displaynumber;
    //必ず「今」存在する場所をポイントすること。どこにもないものはnullとする。
    private String dest_name_with_suffix;
    private String vmsgid;

    private String vcorrelationid;
    private int vdeliverymode = 2;
    private int vpriority = 4;
    private long vexpiration = 0;
    private String vjms_type;
    private boolean vredelivered;
    private long vtimestamp;
    private ArrayList<com.qbrowser.property.Property> additionalHeaders = new ArrayList();

    private String message_type;
    Destination vdest;
    Destination vreplyto;
    private javax.jms.Message message;
    private long body_size = -1;

    /**
     * @return the displaynumber
     */
    public int getDisplaynumber() {
        return displaynumber;
    }

    /**
     * @param displaynumber the displaynumber to set
     */
    public void setDisplaynumber(int displaynumber) {
        this.displaynumber = displaynumber;
    }

    /**
     * @return the message
     */
    public javax.jms.Message getMessage() {
        return message;
    }

    public void setMessageAfterLazyLoad(javax.jms.Message message) {
        this.message = message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(javax.jms.Message message) {
        this.message = message;
        if (message != null) {
            try {
             //setMessage_type(QBrowserV2.messageType(message));
             //this.setVdest(message.getJMSDestination());
             //this.setVreplyto(message.getJMSReplyTo());
                setBody_size(QBrowserUtil.messageBodySizeOfLong(message));
                setMessage_type(QBrowserV2.messageType(message));
                this.setVmsgid(message.getJMSMessageID());
                this.setVdest(message.getJMSDestination());
                this.setVreplyto(message.getJMSReplyTo());
                this.setVcorrelationid(message.getJMSCorrelationID());
                this.setVdeliverymode(message.getJMSDeliveryMode());
                this.setVexpiration(message.getJMSExpiration());
                this.setVjms_type(message.getJMSType());
                this.setVpriority(message.getJMSPriority());
                this.setVredelivered(message.getJMSRedelivered());
                this.setVtimestamp(message.getJMSTimestamp());


            } catch (Exception e) {}
        }
    }

    /**
     * @return the body_size
     */
    public long getBody_size() {
        return body_size;
    }

    /**
     * @param body_size the body_size to set
     */
    public void setBody_size(long body_size) {
        this.body_size = body_size;
    }

    public void setMessageFromBrokerWithLazyLoad(javax.jms.Message message) {

        //this.message = message;

        try {

            if (message != null) {
                setBody_size(QBrowserUtil.messageBodySizeOfLong(message));
                setMessage_type(QBrowserV2.messageType(message));
                this.setVmsgid(message.getJMSMessageID());
                this.setVdest(message.getJMSDestination());
                this.setVreplyto(message.getJMSReplyTo());
                this.setVcorrelationid(message.getJMSCorrelationID());
                this.setVdeliverymode(message.getJMSDeliveryMode());
                this.setVexpiration(message.getJMSExpiration());
                this.setVjms_type(message.getJMSType());
                this.setVpriority(message.getJMSPriority());
                this.setVredelivered(message.getJMSRedelivered());
                this.setVtimestamp(message.getJMSTimestamp());

            }
        } catch (Exception jmse) {
            jmse.printStackTrace();
        }
    }

    /**
     * @return the vdest
     */
    public Destination getVdest() {
        return vdest;
    }

    /**
     * @param vdest the vdest to set
     */
    public void setVdest(Destination vdest) {
        this.vdest = vdest;
    }

    /**
     * @return the vreplyto
     */
    public Destination getVreplyto() {
        return vreplyto;
    }

    /**
     * @param vreplyto the vreplyto to set
     */
    public void setVreplyto(Destination vreplyto) {
        this.vreplyto = vreplyto;
    }

    /**
     * @return the vcorrelationid
     */
    public String getVcorrelationid() {
        return vcorrelationid;
    }

    /**
     * @param vcorrelationid the vcorrelationid to set
     */
    public void setVcorrelationid(String vcorrelationid) {
        this.vcorrelationid = vcorrelationid;
    }

    /**
     * @return the vdeliverymode
     */
    public int getVdeliverymode() {
        return vdeliverymode;
    }

    /**
     * @param vdeliverymode the vdeliverymode to set
     */
    public void setVdeliverymode(int vdeliverymode) {
        this.vdeliverymode = vdeliverymode;
    }

    /**
     * @return the vpriority
     */
    public int getVpriority() {
        return vpriority;
    }

    /**
     * @param vpriority the vpriority to set
     */
    public void setVpriority(int vpriority) {
        this.vpriority = vpriority;
    }

    /**
     * @return the vexpiration
     */
    public long getVexpiration() {
        return vexpiration;
    }

    /**
     * @param vexpiration the vexpiration to set
     */
    public void setVexpiration(long vexpiration) {
        this.vexpiration = vexpiration;
    }

    /**
     * @return the vjms_type
     */
    public String getVjms_type() {
        return vjms_type;
    }

    /**
     * @param vjms_type the vjms_type to set
     */
    public void setVjms_type(String vjms_type) {
        this.vjms_type = vjms_type;
    }

    /**
     * @return the vredelivered
     */
    public boolean isVredelivered() {
        return vredelivered;
    }

    /**
     * @param vredelivered the vredelivered to set
     */
    public void setVredelivered(boolean vredelivered) {
        this.vredelivered = vredelivered;
    }

    /**
     * @return the vtimestamp
     */
    public long getVtimestamp() {
        return vtimestamp;
    }

    /**
     * @param vtimestamp the vtimestamp to set
     */
    public void setVtimestamp(long vtimestamp) {
        this.vtimestamp = vtimestamp;
    }

    /**
     * @return the vmsgid
     */
    public String getVmsgid() {
        return vmsgid;
    }

    /**
     * @param vmsgid the vmsgid to set
     */
    public void setVmsgid(String vmsgid) {
        this.vmsgid = vmsgid;
    }

    /**
     * @return the message_type
     */
    public String getMessage_type() {
        return message_type;
    }

    /**
     * @param message_type the message_type to set
     */
    public void setMessage_type(String message_type) {
        this.message_type = message_type;
    }

    public javax.jms.Message getRealMessageFromBroker(javax.jms.Session session, Queue queue) {
        Message imes = getMessage();
        if (imes != null) {
            return imes;
        } else {

            QueueBrowser qb = null;
            Message retval = null;

            try {
                //レイジーロード
                //Queue q = session.createQueue(queuename);

                String selector = "JMSMessageID ='" + getVmsgid() + "'";
                qb = session.createBrowser(queue, selector);


                Enumeration emt = qb.getEnumeration();
                if (emt.hasMoreElements()) {
                    Message imsg = (Message) emt.nextElement();
                    setMessageAfterLazyLoad(imsg);
                    retval = imsg;
                }


            } catch (Exception ie) {
                ie.printStackTrace();
            } finally {
                if (qb != null) {

                    try {
                         qb.close();
                    } catch (JMSException je) {

                    }
                    qb = null;

                }
            }

            return retval;
        }
    }

    /**
     * @return the dest_name_with_suffix
     */
    public String getDest_name_with_suffix() {
        return dest_name_with_suffix;
    }

    /**
     * @param dest_name_with_suffix the dest_name_with_suffix to set
     */
    public void setDest_name_with_suffix(String dest_name_with_suffix) {
        this.dest_name_with_suffix = dest_name_with_suffix;
    }


    public String getPureDest_name() {
        if (this.dest_name_with_suffix == null) {
            return null;
        } else {
            return QBrowserUtil.getPureDestName(dest_name_with_suffix);
        }
    }

    public String getDest_type() {
        if (QBrowserUtil.isLocalStore(this.dest_name_with_suffix)) {
            return QBrowserV2.LOCAL_STORE_LITERAL;
        } else if (QBrowserUtil.isTopic(this.dest_name_with_suffix)) {
            return QBrowserV2.TOPIC_LITERAL;
        } else {
            return QBrowserV2.QUEUE_LITERAL;
        }
     }

    /**
     * @return the additionalHeaders
     */
    public ArrayList<com.qbrowser.property.Property> getAdditionalHeaders() {
        return additionalHeaders;
    }

    /**
     * @param additionalHeaders the additionalHeaders to set
     */
    public void setAdditionalHeaders(ArrayList<com.qbrowser.property.Property> additionalHeaders) {
        this.additionalHeaders = additionalHeaders;
    }



}
