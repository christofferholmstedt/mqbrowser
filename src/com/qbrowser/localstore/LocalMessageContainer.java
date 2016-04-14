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
import com.qbrowser.container.MessageContainer;
import com.qbrowser.persist.BytesMessageReader;
import com.qbrowser.persist.MapMessageReader;
import com.qbrowser.persist.ObjectMessageReader;
import com.qbrowser.persist.PersistedMessageReader;
import com.qbrowser.persist.StreamMessageReader;
import com.qbrowser.persist.TextMessageReader;
import com.qbrowser.util.QBrowserUtil;
import java.io.File;
import javax.jms.Message;

/**
 *
 * @author takemura
 */
public class LocalMessageContainer extends MessageContainer {



    private String real_file_path;


    //message + additional
    public LocalMessageContainer() {}

    public LocalMessageContainer(File msgarchive) throws Exception {

        String filepath = msgarchive.getAbsolutePath();
        File wf = null;

        try {

        if (filepath.indexOf("_" + QBrowserV2.TEXTMESSAGE) != -1) {

                TextMessageReader tmr = new TextMessageReader();
                wf = tmr.readPersistedMessageWithLazyLoad(new File(filepath));
                QBrowserUtil.populateHeadersOfLocalMessageContainer(tmr.recreateMessagefromReadDataWithLazyLoad(), this);
                //LocalStoreManager.addMsgIndex(puredest, tmsg.getVmsgid() , filepath);
                PersistedMessageReader.clearDir(wf);
                wf = null;

              } else
              if (filepath.indexOf("_" + QBrowserV2.BYTESMESSAGE) != -1) {

                BytesMessageReader bmr = new BytesMessageReader();

                wf = bmr.readPersistedMessageWithLazyLoad(new File(filepath));
                QBrowserUtil.populateHeadersOfLocalMessageContainer(bmr.recreateMessagefromReadDataWithLazyLoad(), this);

                PersistedMessageReader.clearDir(wf);
                wf = null;

              } else
              if (filepath.indexOf("_" + QBrowserV2.MAPMESSAGE) != -1) {

                MapMessageReader mmr = new MapMessageReader();

                wf = mmr.readPersistedMessageWithLazyLoad(new File(filepath));
                QBrowserUtil.populateHeadersOfLocalMessageContainer(mmr.recreateMessagefromReadDataWithLazyLoad(), this);
                
                PersistedMessageReader.clearDir(wf);
                wf = null;


              } else
              if (filepath.indexOf("_" + QBrowserV2.STREAMMESSAGE) != -1) {

                StreamMessageReader smr = new StreamMessageReader();

                wf = smr.readPersistedMessageWithLazyLoad(new File(filepath));
                QBrowserUtil.populateHeadersOfLocalMessageContainer(smr.recreateMessagefromReadDataWithLazyLoad(), this);

                PersistedMessageReader.clearDir(wf);
                wf = null;


              } else
              if (filepath.indexOf("_" + QBrowserV2.OBJECTMESSAGE) != -1) {

                ObjectMessageReader omr = new ObjectMessageReader();

                wf = omr.readPersistedMessageWithLazyLoad(new File(filepath));
                QBrowserUtil.populateHeadersOfLocalMessageContainer(omr.recreateMessagefromReadDataWithLazyLoad(), this);
                
                PersistedMessageReader.clearDir(wf);


              } else if (filepath.indexOf("_" + QBrowserV2.MESSAGE) != -1) {

                PersistedMessageReader pmr = new PersistedMessageReader();

                wf = pmr.readPersistedMessageWithLazyLoad(new File(filepath));
                QBrowserUtil.populateHeadersOfLocalMessageContainer(pmr.recreateMessagefromReadDataWithLazyLoad(), this);
               
                PersistedMessageReader.clearDir(wf);

              }

        } catch (Exception reade) {
            PersistedMessageReader.clearDir(wf);
            throw new Exception(QBrowserV2.resources.getString("qkey.msg.msg337") + "\n\nFile=" + msgarchive.getAbsolutePath());
        }

    }

    public void deleteRealMessageFile() {
        if (real_file_path != null) {
          File tf = new File(real_file_path);
          if (tf.exists()) {
            tf.delete();
          }
        }
    }

    @Override
    public void setMessage(javax.jms.Message message) {
        super.setMessage(message);
        if (message != null) {
            setMessage_type(QBrowserV2.messageType(message));
            try {
                setBody_size(QBrowserUtil.messageBodySizeOfLong(message));
            } catch (Exception e) {
                e.printStackTrace();
                setBody_size(0);
            }
        }
    }


    public javax.jms.Message getRealMessage(javax.jms.Session session) throws Exception {
        Message imes = getMessage();
        if (imes != null) {
            return imes;
        } else {

           if (getMessage_type() == null) {
                 setMessage_type(QBrowserUtil.getMessageTypeFromRealFilePath(real_file_path));
           }

           File wf = null;

           try {
            //レイジーロード
             if (getMessage_type().equals(QBrowserV2.TEXTMESSAGE)) {

                TextMessageReader tmr = new TextMessageReader();
                wf = tmr.readPersistedMessage(new File(real_file_path));
                LocalMessageContainer tmsg = tmr.recreateMessagefromReadData(session);
                //LocalStoreManager.addMsgIndex(puredest, tmsg.getVmsgid() , filepath);
                PersistedMessageReader.clearDir(wf);
                wf = null;
                Message res = tmsg.getMessage();
                //ここでリプレイされたメッセージの属性→LMCの属性にコピーする
                QBrowserUtil.populateHeadersOfLocalMessageContainer2(tmsg, this);
                return res;

              } else
              if (getMessage_type().equals(QBrowserV2.BYTESMESSAGE)) {

                BytesMessageReader bmr = new BytesMessageReader();
                wf = bmr.readPersistedMessage(new File(real_file_path));
                LocalMessageContainer tmsg = bmr.recreateMessagefromReadData(session);
                //LocalStoreManager.addMsgIndex(puredest, tmsg.getVmsgid() , filepath);
                PersistedMessageReader.clearDir(wf);
                wf = null;
                Message res = tmsg.getMessage();
                long bsize = getBody_size();
                QBrowserUtil.populateHeadersOfLocalMessageContainer2(tmsg, this);
                this.setBody_size(bsize);
                return res;

              } else
              if (getMessage_type().equals(QBrowserV2.MAPMESSAGE)) {

                MapMessageReader mmr = new MapMessageReader();
                wf = mmr.readPersistedMessage(new File(real_file_path));
                LocalMessageContainer tmsg = mmr.recreateMessagefromReadData(session);
                //LocalStoreManager.addMsgIndex(puredest, tmsg.getVmsgid() , filepath);
                PersistedMessageReader.clearDir(wf);
                wf = null;
                Message res = tmsg.getMessage();
                QBrowserUtil.populateHeadersOfLocalMessageContainer2(tmsg, this);
                return res;


              } else
              if (getMessage_type().equals(QBrowserV2.STREAMMESSAGE)) {

                StreamMessageReader smr = new StreamMessageReader();
                wf = smr.readPersistedMessage(new File(real_file_path));
                LocalMessageContainer tmsg = smr.recreateMessagefromReadData(session);
                //LocalStoreManager.addMsgIndex(puredest, tmsg.getVmsgid() , filepath);
                PersistedMessageReader.clearDir(wf);
                wf = null;
                Message res = tmsg.getMessage();
                QBrowserUtil.populateHeadersOfLocalMessageContainer2(tmsg, this);
                return res;


              } else
              if (getMessage_type().equals(QBrowserV2.OBJECTMESSAGE)) {

                ObjectMessageReader omr = new ObjectMessageReader();
                wf = omr.readPersistedMessage(new File(real_file_path));
                LocalMessageContainer tmsg = omr.recreateMessagefromReadData(session);
                //LocalStoreManager.addMsgIndex(puredest, tmsg.getVmsgid() , filepath);
                PersistedMessageReader.clearDir(wf);
                Message res = tmsg.getMessage();
                QBrowserUtil.populateHeadersOfLocalMessageContainer2(tmsg, this);
                return res;


              } else if (getMessage_type().equals(QBrowserV2.MESSAGE)) {

                PersistedMessageReader pmr = new PersistedMessageReader();
                wf = pmr.readPersistedMessage(new File(real_file_path));
                LocalMessageContainer tmsg = pmr.recreateMessagefromReadData(session);
                //LocalStoreManager.addMsgIndex(puredest, tmsg.getVmsgid() , filepath);
                PersistedMessageReader.clearDir(wf);
                Message res = tmsg.getMessage();
                QBrowserUtil.populateHeadersOfLocalMessageContainer2(tmsg, this);
                return res;

              }

           } catch (Exception ie) {
               PersistedMessageReader.clearDir(wf);
               throw ie;
           }

           return null;
        }
    }



    /**
     * @return the real_file_path
     */
    public String getReal_file_path() {
        return real_file_path;
    }

    /**
     * @param real_file_path the real_file_path to set
     */
    public void setReal_file_path(String real_file_path) {
        this.real_file_path = real_file_path;
    }





}
