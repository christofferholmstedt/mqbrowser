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

import com.qbrowser.localstore.LocalMessageContainer;
import com.qbrowser.property.Property;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.jms.BytesMessage;
import javax.jms.JMSException;

/**
 *
 * @author takemura
 */
public class BytesMessagePersister extends MessagePersister {




    public BytesMessagePersister() {
        super();
    }

    public BytesMessagePersister(BytesMessage val) {
        message = val;
    }

    public BytesMessagePersister(LocalMessageContainer vlmc) {
        lmc = vlmc;
        message = vlmc.getMessage();
    }


    @Override
    public File persistToFile() throws Exception {
        File workfileDir = super.persistToFile();

        //Lazy Load用サイズファイル
        File target1 = new File(workfileDir.getAbsolutePath() + File.separator + "bytesbodysize");

        PrintWriter pwr = null;
       
        BytesMessage bm = (BytesMessage) message;

        java.io.FileOutputStream fo = null;
        //BytesBodyの永続化
        try {

                pwr = new PrintWriter(new FileWriter(target1));
                

                bm.reset();

                File efile = new File(workfileDir.getAbsolutePath() + File.separator + "MessageBody_" + Property.BYTES_TYPE);
                byte[] bibi = new byte[1024];

                fo = new FileOutputStream(efile);

                int len = 0;
                long readfilesize = 0;
                int count = 1000;

                while ((len = bm.readBytes(bibi)) != -1) {

                    fo.write(bibi, 0, len);
                    readfilesize += len;
                    
                    if (++count > 1000) {
                        sbuf.append(readfilesize + " " + resources.getString("qkey.msg.msg098") + bm.getBodyLength() + " " + resources.getString("qkey.msg.msg099") + "\n");
                        //sbuf.setCaretPosition(ta.getText().length());
                        //msgDialog.getRootPane().updateUI();
                        count = 0;
                    }
                    

                }

                if (count != 0) {
                        sbuf.append(readfilesize + " " + resources.getString("qkey.msg.msg098") + bm.getBodyLength() + " " + resources.getString("qkey.msg.msg099") + "\n");
                        //ta.setCaretPosition(ta.getText().length());
                        //msgDialog.getRootPane().updateUI();

                }

                pwr.println(bm.getBodyLength());



        } catch (JMSException ex) {
            ex.printStackTrace();
        } catch (Throwable thex) {
            thex.printStackTrace();

        } finally {
            if (fo != null) {
                try {
                fo.close();
                } catch (IOException ioe) {}
                fo = null;
            }

            if (pwr != null) {
                pwr.close();
            }
        }


        return workfileDir;

    }

}
