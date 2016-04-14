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
import com.qbrowser.util.QBrowserUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import javax.jms.ObjectMessage;
import javax.jms.Session;

/**
 *
 * @author takemura
 */
public class ObjectMessageReader extends PersistedMessageReader {

    Object retrieved_object;

    @Override
    public File readPersistedMessageWithLazyLoad(File msgArchive) throws Exception {
        File workdirFile = super.readPersistedMessageWithLazyLoad(msgArchive);
        return workdirFile;
    }

    @Override
    public File readPersistedMessage(File msgArchive) throws Exception {



        File workdirFile = super.readPersistedMessage(msgArchive);
        FileInputStream inFile = null;
        ObjectInputStream inObject = null;

        File body_file = new File(workdirFile.getAbsolutePath() + File.separator + "ObjectMessageBody");
        if (body_file == null || !body_file.exists()) {
            clearDir(workdirFile);
            throw new IOException(QBrowserV2.resources.getString("qkey.msg.msg332") + body_file.getName() +
                    QBrowserV2.resources.getString("qkey.msg.msg333"));
        }

        try {

            //FileInputStreamオブジェクトの生成
            inFile = new FileInputStream(workdirFile.getAbsolutePath() + File.separator + "ObjectMessageBody");
            //ObjectInputStreamオブジェクトの生成
            inObject = new ObjectInputStream(inFile);
            //オブジェクトの読み込み
            retrieved_object = inObject.readObject();


        } catch (Exception thex) {
            clearDir(workdirFile);
            thex.printStackTrace();
            throw thex;
        } finally {
            if (inObject != null) {
                try {
                    inObject.close();
                } catch (Exception ie) {
                }
                inObject = null;
            }

            if (inFile != null) {
                try {
                    inFile.close();
                } catch (Exception ie) {
                }
                inFile = null;
            }
        }

        //clearDir(workdirFile);

        //return null;
        return workdirFile;

    }

    @Override
    public LocalMessageContainer recreateMessagefromReadData(Session session) throws Exception {

        ObjectMessage msg = session.createObjectMessage();
        LocalMessageContainer lmc = new LocalMessageContainer();

        if (properties != null) {
            QBrowserUtil.copyUserProperties(properties, msg);
        }

        lmc.setMessage(msg);
        lmc.setReal_file_path(source_file_path);

        if (headers != null) {
            QBrowserUtil.copyMessageHeaders(headers, msg);
            QBrowserUtil.populateHeadersOfLocalMessageContainer(headers, lmc);

        }

        msg.setObject((java.io.Serializable)retrieved_object);



        return lmc;

    }

    @Override
    public LocalMessageContainer recreateMessagefromReadDataWithLazyLoad() throws Exception {
         LocalMessageContainer lmc = super.recreateMessagefromReadDataWithLazyLoad();
         lmc.setMessage_type(QBrowserV2.OBJECTMESSAGE);
         return lmc;

    }

}
