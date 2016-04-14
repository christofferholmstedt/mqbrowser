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
import com.qbrowser.util.QBrowserUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import javax.jms.BytesMessage;
import javax.jms.Session;

/**
 *
 * @author takemura
 */
public class BytesMessageReader extends PersistedMessageReader {

    File target_file;
    
    long body_size = -1;

    public static void main(String[] args) {
        BytesMessageReader bmr = new BytesMessageReader();
        try {
        bmr.readPersistedMessage(new File("c:\\temp1\\ID49-192.168.11.2(e18a30e4fe7f)-4203-1245762696859_BytesMessage.zip"));
        //System.out.println(bmr.target_file.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public File readPersistedMessage(File msgArchive) throws Exception {

        File workdirFile = super.readPersistedMessage(msgArchive);

        try {

        target_file = new File(workdirFile.getAbsolutePath() + File.separator + "MessageBody_" + Property.BYTES_TYPE);

        if (target_file == null || !target_file.exists()) {
            throw new IOException(QBrowserV2.resources.getString("qkey.msg.msg332") + target_file.getName() +
                    QBrowserV2.resources.getString("qkey.msg.msg333"));
        }

        } catch (Exception e) {
            clearDir(workdirFile);
            throw e;
        }

        return workdirFile;
    }

    @Override
    public File readPersistedMessageWithLazyLoad(File msgArchive) throws Exception {
        File workdirFile = super.readPersistedMessageWithLazyLoad(msgArchive);
        
        File size_file = new File(workdirFile.getAbsolutePath() + File.separator + "bytesbodysize");
        BufferedReader br = null;
        try {

            br = new BufferedReader(new FileReader(size_file));
            String line = br.readLine();
            if (line != null) {
                try {
                   body_size = Long.parseLong(line.trim());
                } catch (NumberFormatException ne) {
                    //NP
                }
            }


        } catch (IOException ie) {
            clearDir(workdirFile);
            body_size = 0;
        } finally {
            if (br != null) {
                br.close();
            }
        }

        return workdirFile;
    }

    

    @Override
    public LocalMessageContainer recreateMessagefromReadData(Session session) throws Exception {

        BytesMessage bmsg = session.createBytesMessage();
        LocalMessageContainer lmc = new LocalMessageContainer();

        if (properties != null) {
            QBrowserUtil.copyUserProperties(properties, bmsg);
        }

        lmc.setMessage(bmsg);
        lmc.setReal_file_path(source_file_path);

        if (headers != null) {
            QBrowserUtil.copyMessageHeaders(headers, bmsg);
            QBrowserUtil.populateHeadersOfLocalMessageContainer(headers, lmc);

        }

         java.io.FileInputStream fi = null;

         try {
         fi = new FileInputStream(target_file);

         byte buf[] = new byte[1024];
         int len = 0;

         int filesizecount = 0;

                while ((len = fi.read(buf)) != -1) {
                    filesizecount += buf.length;
                    bmsg.writeBytes(buf, 0, len);
                }

         //lmc.setBody_size(filesizecount);

         } catch (IOException ioe) {
            ioe.printStackTrace();
            throw new IOException(QBrowserV2.resources.getString("qkey.msg.msg331") + ioe.getMessage() + "\n\nFile=" + target_file.getName());
         } finally {
             if (fi != null) {
                 try {
                 fi.close();
                 } catch (IOException iie) {}
                 fi = null;
             }
         }

        return lmc;

    }

    @Override
    public LocalMessageContainer recreateMessagefromReadDataWithLazyLoad() throws Exception {
         LocalMessageContainer lmc = super.recreateMessagefromReadDataWithLazyLoad();
         lmc.setBody_size(body_size);
         lmc.setMessage_type(QBrowserV2.BYTESMESSAGE);

         return lmc;

    }

}
