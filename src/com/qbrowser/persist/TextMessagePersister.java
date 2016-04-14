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
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.jms.JMSException;
import javax.jms.TextMessage;

/**
 *
 * @author takemura
 */
public class TextMessagePersister extends MessagePersister {

    String textbody;

    public TextMessagePersister() {
        super();
    }

    public TextMessagePersister(TextMessage val) {
        message = val;
        try {
          textbody = val.getText();
        } catch (JMSException jme) {
            jme.printStackTrace();
        }
    }

    public TextMessagePersister(LocalMessageContainer vlmc) {
        lmc = vlmc;
        message = vlmc.getMessage();
        try {
          textbody = ((TextMessage)message).getText();
        } catch (JMSException jme) {
            jme.printStackTrace();
        }
    }

    @Override
    public File persistToFile() throws Exception {
        File workfileDir = super.persistToFile();

        //Lazy Load用サイズファイル
        File target1 = new File(workfileDir.getAbsolutePath() + File.separator + "textbodysize");

        PrintWriter pwr = null;

        

            ByteArrayInputStream bis = null;
            java.io.FileOutputStream fo = null;

        //TextBodyの永続化
        try {

            pwr = new PrintWriter(new FileWriter(target1));
            long fsize = 0;
            File efile = new File(workfileDir.getAbsolutePath() + File.separator + "TextMessageBody.txt");
            if ((textbody != null) && (textbody.length() > 0)) {
                byte[] bibi = new byte[1024];

                bis = new ByteArrayInputStream(textbody.getBytes());
                fo = new FileOutputStream(efile);

                int len = 0;
                long readfilesize = 0;

                while ((len = bis.read(bibi, 0, bibi.length)) != -1) {
                    fo.write(bibi, 0, len);
                    readfilesize += len;

                }

                fsize = readfilesize;

            } else {
                efile.createNewFile();
            }

            pwr.println(fsize);

        } catch (Throwable thex) {
            thex.printStackTrace();
        } finally {
              if (fo != null) {
                try {
                    fo.close();
                    
                } catch (IOException ioe) {
                    //
                }

                fo = null;
              }
              if (bis != null) {
                  try {
                    bis.close();
                    
                  } catch (IOException ioe) {
                      //
                  }
                  bis = null;
              }
              if (pwr != null) {
                  pwr.close();
              }
          }

        

        return workfileDir;

    }

}
