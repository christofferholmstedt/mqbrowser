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

package com.qbrowser.persist;

import com.qbrowser.QBrowserV2;
import com.qbrowser.localstore.LocalMessageContainer;
import com.qbrowser.property.Property;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import javax.jms.JMSException;
import javax.jms.StreamMessage;

/**
 *
 * @author takemura
 */
public class StreamMessagePersister extends MessagePersister {


    public StreamMessagePersister() {
        super();
    }

    public StreamMessagePersister(StreamMessage val) {
        message = val;
    }

    public StreamMessagePersister(LocalMessageContainer vlmc) {
        lmc = vlmc;
        message = vlmc.getMessage();
    }



    @Override
    public File persistToFile() throws Exception {
        File workfileDir = super.persistToFile();

        StreamMessage msg = (StreamMessage) message;
        PrintWriter pwr = null;

        //StreamBodyの永続化
        try {

              pwr = new PrintWriter(new FileWriter(new File(workfileDir.getAbsolutePath() + File.separator + "StreamMessageDef")));
              msg.reset();

                Object obj = null;
                int seq = 0;

                while ((obj = ((StreamMessage)msg).readObject()) != null) {

                seq++;

                if (obj instanceof String) {
                      File efile = new File(workfileDir.getAbsolutePath() + File.separator + "StreamMessage" + File.separator + seq + "_StreamMessageBody_" + Property.STRING_TYPE);
                      stringPropertyToFile(efile, (String)obj);
                      pwr.println(seq + QBrowserV2.MAGIC_SEPARATOR + Property.STRING_TYPE + QBrowserV2.MAGIC_SEPARATOR + efile.getName());
                } else if (obj instanceof Integer) {
                      File efile = new File(workfileDir.getAbsolutePath() + File.separator + "StreamMessage" + File.separator + seq + "_StreamMessageBody_" + Property.INT_TYPE);
                      intPropertyToFile(efile, (Integer)obj);
                      pwr.println(seq + QBrowserV2.MAGIC_SEPARATOR + Property.INT_TYPE + QBrowserV2.MAGIC_SEPARATOR + efile.getName());
                } else if (obj instanceof Boolean) {
                      File efile = new File(workfileDir.getAbsolutePath() + File.separator + "StreamMessage" + File.separator + seq + "_StreamMessageBody_" + Property.BOOLEAN_TYPE);
                      booleanPropertyToFile(efile, (Boolean)obj);
                      pwr.println(seq + QBrowserV2.MAGIC_SEPARATOR + Property.BOOLEAN_TYPE + QBrowserV2.MAGIC_SEPARATOR + efile.getName());
                } else if (obj instanceof Byte) {
                      File efile = new File(workfileDir.getAbsolutePath() + File.separator + "StreamMessage" + File.separator + seq + "_StreamMessageBody_" + Property.BYTE_TYPE);
                      bytePropertyToFile(efile, (Byte)obj);
                      pwr.println(seq + QBrowserV2.MAGIC_SEPARATOR + Property.BYTE_TYPE + QBrowserV2.MAGIC_SEPARATOR + efile.getName());
                } else if (obj instanceof Character) {
                      File efile = new File(workfileDir.getAbsolutePath() + File.separator + "StreamMessage" + File.separator + seq + "_StreamMessageBody_" + Property.CHARACTER_TYPE);
                      charPropertyToFile(efile, (Character)obj);
                      pwr.println(seq + QBrowserV2.MAGIC_SEPARATOR + Property.CHARACTER_TYPE + QBrowserV2.MAGIC_SEPARATOR + efile.getName());
                } else if (obj instanceof Double) {
                      File efile = new File(workfileDir.getAbsolutePath() + File.separator + "StreamMessage" + File.separator + seq + "_StreamMessageBody_" + Property.DOUBLE_TYPE);
                      doublePropertyToFile(efile, (Double)obj);
                      pwr.println(seq + QBrowserV2.MAGIC_SEPARATOR + Property.DOUBLE_TYPE + QBrowserV2.MAGIC_SEPARATOR + efile.getName());
                } else if (obj instanceof Float) {
                      File efile = new File(workfileDir.getAbsolutePath() + File.separator + "StreamMessage" + File.separator + seq + "_StreamMessageBody_" + Property.FLOAT_TYPE);
                      floatPropertyToFile(efile, (Float)obj);
                      pwr.println(seq + QBrowserV2.MAGIC_SEPARATOR + Property.FLOAT_TYPE + QBrowserV2.MAGIC_SEPARATOR + efile.getName());
                } else if (obj instanceof Long) {
                      File efile = new File(workfileDir.getAbsolutePath() + File.separator + "StreamMessage" + File.separator + seq + "_StreamMessageBody_" + Property.LONG_TYPE);
                      longPropertyToFile(efile, (Long)obj);
                      pwr.println(seq + QBrowserV2.MAGIC_SEPARATOR + Property.LONG_TYPE + QBrowserV2.MAGIC_SEPARATOR + efile.getName());
                } else if (obj instanceof Short) {
                      File efile = new File(workfileDir.getAbsolutePath() + File.separator + "StreamMessage" + File.separator + seq + "_StreamMessageBody_" + Property.SHORT_TYPE);
                      shortPropertyToFile(efile, (Short)obj);
                      pwr.println(seq + QBrowserV2.MAGIC_SEPARATOR + Property.SHORT_TYPE + QBrowserV2.MAGIC_SEPARATOR + efile.getName());
                } else if (obj instanceof byte[]) {
                      File efile = new File(workfileDir.getAbsolutePath() + File.separator + "StreamMessage" + File.separator + seq + "_StreamMessageBody_" + Property.BYTES_TYPE);
                      bytesPropertyToFile(efile, (byte[])obj);
                      pwr.println(seq + QBrowserV2.MAGIC_SEPARATOR + Property.BYTES_TYPE + QBrowserV2.MAGIC_SEPARATOR + efile.getName());
                }

            }

        } catch (JMSException ex) {
            //ex.printStackTrace();
        } catch (Throwable thex) {

        } finally {
            if (pwr != null) {
                pwr.close();
                pwr = null;
            }
        }


        return workfileDir;

    }

}
