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
import javax.jms.Message;
import com.qbrowser.persist.BytesMessagePersister;
import com.qbrowser.persist.MapMessagePersister;
import com.qbrowser.persist.MessagePersister;
import com.qbrowser.persist.ObjectMessagePersister;
import com.qbrowser.persist.StreamMessagePersister;
import com.qbrowser.persist.TextMessagePersister;
import com.qbrowser.util.QBrowserUtil;
import java.awt.TextArea;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ResourceBundle;
import javax.jms.BytesMessage;
import javax.jms.MapMessage;
import javax.jms.ObjectMessage;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;
import javax.swing.JDialog;

/**
 *
 * @author takemura
 */
public class LocalStoreManager {

    LocalStoreDef lsd;

    //ターゲットFIND用インデックス（全体)
    //キー：宛先名(PURE) 値：msgid/filepathのHashMap
    static HashMap local_store_index = new HashMap();
    //public static ResourceBundle resources = ResourceBundle.getBundle("com.qbrowser.resourcebase");
    static ResourceBundle resources = QBrowserV2.resources;

    //キー：任意の宛先 値： ローカル宛先名（サフィックスなし）/ セレクタのペア
    static HashMap destName_LocalSubscriptionDest = new HashMap();
    static String saved_def_name;


    //LocalStoreのCRUDマシン
    public LocalStoreManager(String id) {
        local_store_index = new HashMap();
        destName_LocalSubscriptionDest = new HashMap();
        //初期定義ローディング
        lsd = new LocalStoreDef();
        saved_def_name = QBrowserUtil.getQBrowserTempFileDir() + "localstoredef" + id;

        File lsdef = new File(saved_def_name);

        //ファイルがない場合は一番最初の定義を作成する
        if (!lsdef.exists()) {

            File qvd = new File(QBrowserUtil.getQBrowserTempFileDir());
            if (!qvd.exists()) {
                qvd.mkdirs();
            }

            LocalStoreProperty lsp1 = new LocalStoreProperty();
            lsp1.setDestName("TMPWORK");
            lsp1.setReal_file_directory(QBrowserUtil.getQBrowserTempFileDir() + "tmpwork_localstore");
            lsp1.setValid(true);
            lsd.setLocalStoreProperty(lsp1);
            lsd.save(new File(saved_def_name));

        } else {
            //ファイルが存在する場合はそこから定義をローディング
            lsd.readAndParseLocalStoreDefFile(lsdef, destName_LocalSubscriptionDest);
        }
    }

    public static void addMsgIndex(String puredest, String msgid, String msg_filepath) {
        HashMap msgid_filepath = (HashMap)local_store_index.get(puredest);
        if (msgid_filepath == null) {
            msgid_filepath = new HashMap();
        }

        msgid_filepath.put(msgid, msg_filepath);
        local_store_index.put(puredest, msgid_filepath);
    }

    public void removeMsgIndex(String puredest, String msgid) {
        HashMap msgid_filepath = (HashMap)local_store_index.get(puredest);
        if (msgid_filepath != null) {
            msgid_filepath.remove(msgid);
        }
    }

    //宛先名とmsgidからメッセージファイルの場所を探す
    public String getMsgFilePath(String puredest, String msgid) {

        HashMap msgid_filepath = (HashMap)local_store_index.get(puredest);
        if (msgid_filepath != null) {
            String filepath = (String)msgid_filepath.get(msgid);
            return filepath;
        } else {
            return null;
        }

    }

    public ArrayList<String> getAllLocalStoreNames() {
        Collection col1 = lsd.getAllLocalStoreProperties();
        Iterator icol1 = col1.iterator();
        ArrayList retval = new ArrayList();
        while (icol1.hasNext()) {
            LocalStoreProperty lsp = (LocalStoreProperty)icol1.next();
            retval.add(lsp.getDestName());
        }

        Collections.sort(retval);

        return retval;

    }

    public ArrayList<String> getAllValidLocalStoreNames() {
        Collection col1 = lsd.getAllLocalStoreProperties();
        Iterator icol1 = col1.iterator();
        ArrayList retval = new ArrayList();
        while (icol1.hasNext()) {
            LocalStoreProperty lsp = (LocalStoreProperty)icol1.next();
            if (lsp.isValid()) {
              retval.add(lsp.getDestName());
            }
        }

        Collections.sort(retval);

        return retval;

    }

    public Collection<LocalStoreProperty> listLocalStoreProperties() {
         return lsd.getAllLocalStoreProperties();
    }
    
    public LocalStoreProperty getLocalStoreProperty(String local_dest_without_suffix) {
        return lsd.getLocalStoreProperty(local_dest_without_suffix);
    }

    public LocalStoreManager.LocalStore getLocalStoreInstance(String local_dest_without_suffix) {
         //プロパティの抽出
         LocalStoreProperty lsp = lsd.getLocalStoreProperty(local_dest_without_suffix);
         try {
             return new LocalStore(lsp);
         } catch (Exception e) {
             return null;
         }


    }


    public void addLocalStoreProperty(LocalStoreProperty lsp) {
        lsd.setLocalStoreProperty(lsp);
    }

    //この宛先は、レシーブ時にどのローカル宛先へコピーすればいいかを調べる
    //戻り：ローカル宛先が入ったArrayList パラメータ：調べたい宛先
    public ArrayList<String> getCopyToListOfTheDestination(String dest_name_with_suffix) {


         HashMap local_and_selector = (HashMap)destName_LocalSubscriptionDest.get(dest_name_with_suffix);
         ArrayList retval = new ArrayList();

        if (local_and_selector != null) {
            Iterator ilas = local_and_selector.keySet().iterator();
            while (ilas.hasNext()) {
                String local_dest = (String) ilas.next();
                retval.add(local_dest);
            }
        }

         return retval;

    }

    public void addDestCopySubscriptionToLocalStore(String local_store_name_without_suffix,
                                                            String subscribe_dest_name_with_suffix,
                                                            String selector) throws Exception  {
        //LSPの取得
        LocalStoreProperty lsp = lsd.getLocalStoreProperty(local_store_name_without_suffix);
        if (lsp == null) {
            throw new Exception("Local Store not found.");
        }

        //自分はループになるので、サブスクライブ不可
        if (lsp.getDestNameWithSuffix().equals(subscribe_dest_name_with_suffix)) {
            throw new Exception("Subscription target cannot be itself.");
        }

        HashMap local_and_selector = (HashMap)destName_LocalSubscriptionDest.get(subscribe_dest_name_with_suffix);
        if (local_and_selector == null) {
            local_and_selector = new HashMap();
        }
        local_and_selector.put(local_store_name_without_suffix, selector);
        destName_LocalSubscriptionDest.put(subscribe_dest_name_with_suffix, local_and_selector);

        //サブスクライブする宛先/サブスクライブ時のセレクタのペア
        lsp.addFromDests(subscribe_dest_name_with_suffix, selector);
        lsd.setLocalStoreProperty(lsp);
        lsd.save(new File(saved_def_name));

    }

    //ある宛先に関連した、全部のローカルストアエントリを落とす
    //Topic自体の削除時なんかに使う
    public void removeRelatedEntryOfSubscribeDest(String subscribe_dest_name_with_suffix) {
          destName_LocalSubscriptionDest.remove(subscribe_dest_name_with_suffix);
    }

    public void removeDestCopySubscriptionToLocalStore(String local_store_name_without_suffix,
                                                            String subscribe_dest_name_with_suffix) throws Exception  {

        HashMap local_and_selector = (HashMap)destName_LocalSubscriptionDest.get(subscribe_dest_name_with_suffix);
        if (local_and_selector == null) {
            local_and_selector = new HashMap();
        }
        local_and_selector.remove(local_store_name_without_suffix);
        destName_LocalSubscriptionDest.put(subscribe_dest_name_with_suffix, local_and_selector);

    }

    public void updateAndSaveLocalStoreProperty(LocalStoreProperty lsp) throws Exception  {
        if (lsp == null) {
            throw new Exception("LocalStoreProperty is not set.");
        }

        lsd.setLocalStoreProperty(lsp);
        lsd.save(new File(saved_def_name));
    }

    public void addNewLocalStoreProperty(LocalStoreProperty lsp) throws Exception  {
        if (lsp == null) {
            throw new Exception("LocalStoreProperty is not set.");
        }

        LocalStoreProperty glsp = lsd.getLocalStoreProperty(lsp.getDestName());
        if (glsp != null) {
            throw new Exception(resources.getString("qkey.msg.msg299"));
        }


        lsd.setLocalStoreProperty(lsp);
        lsd.save(new File(saved_def_name));
    }

    public void updateLocalStoreProperty(LocalStoreProperty lsp) throws Exception {
         LocalStoreProperty target = lsd.getLocalStoreProperty(lsp.getDestName());
         if (target == null) {
             throw new Exception("target local store not found.");
         }

         lsd.setLocalStoreProperty(lsp);
    }

    public void removeLocalStoreProperty(LocalStoreProperty lsp) {
        lsd.removeLocalStoreProperty(lsp.getDestName());
        lsd.save(new File(saved_def_name));
    }

    public void deleteLocalStoreData(String msgid, String local_store_name_without_suffix) throws IOException {
        String target_file_path = getMsgFilePath(local_store_name_without_suffix, msgid);
        File tf = new File(target_file_path);
        tf.delete();
    }

    public void clearLocalStore(String local_store_name_without_suffix) throws IOException {
        LocalStoreProperty target = lsd.getLocalStoreProperty(local_store_name_without_suffix);
        File targetdir = new File(target.getReal_file_directory());
        if (targetdir != null && targetdir.exists() && targetdir.isDirectory()) {
            File[] files = targetdir.listFiles();
            for (int i = 0; i < files.length ; i++) {
                if (files[i].getName().endsWith("Message.zip")) {
                    files[i].delete();
                }
            }
        }
    }

public static class LocalStore {

    //public ResourceBundle resources = ResourceBundle.getBundle("com.qbrowser.resourcebase");
    static ResourceBundle resources = QBrowserV2.resources;
    LocalStoreProperty thisproperty;

    public LocalStore(LocalStoreProperty thisvalue) throws Exception {
        if (thisvalue == null) {
            throw new Exception("local store property is not set.");
        }
        thisproperty =thisvalue;
    }


    public File messageToFile(Message sel_message, StringBuilder sbuf, JDialog cmp) {

        File tf = null;
        File workdir = null;
       

        if (sel_message instanceof TextMessage) {
            TextMessagePersister mp = new TextMessagePersister((TextMessage) sel_message);
            mp.setTextBuffer(sbuf);
            mp.setMsgDialog(cmp);
            try {
                workdir = mp.persistToFile();
                String file_target_string = thisproperty.getReal_file_directory() + QBrowserUtil.eliminateDameMoji(sel_message.getJMSMessageID()) + "_TextMessage.zip";
                tf = new File(file_target_string);
                mp.zipUp(workdir, tf);
                sbuf.append(resources.getString("qkey.msg.msg253"));
                sbuf.append("\n\n");
                return tf;
            } catch (Exception ex) {
                mp.cleanupWorkDir(workdir);
                ex.printStackTrace();
            }
        } else if (sel_message instanceof BytesMessage) {
            BytesMessagePersister mp = new BytesMessagePersister((BytesMessage) sel_message);
            mp.setTextBuffer(sbuf);
            mp.setMsgDialog(cmp);
            try {
                workdir = mp.persistToFile();
                String file_target_string = thisproperty.getReal_file_directory()  + QBrowserUtil.eliminateDameMoji(sel_message.getJMSMessageID()) + "_BytesMessage.zip";
                tf = new File(file_target_string);
                mp.zipUp(workdir, tf);
                sbuf.append(resources.getString("qkey.msg.msg253"));
                sbuf.append("\n\n");
                cmp.getRootPane().updateUI();
                return tf;
            } catch (Exception ex) {
                mp.cleanupWorkDir(workdir);
                ex.printStackTrace();
            }
        } else if (sel_message instanceof MapMessage) {
            MapMessagePersister mp = new MapMessagePersister((MapMessage) sel_message);
            mp.setTextBuffer(sbuf);
            mp.setMsgDialog(cmp);
            try {
                workdir = mp.persistToFile();
                String file_target_string = thisproperty.getReal_file_directory() + QBrowserUtil.eliminateDameMoji(sel_message.getJMSMessageID()) + "_MapMessage.zip";
                tf = new File(file_target_string);
                mp.zipUp(workdir, tf);
                sbuf.append(resources.getString("qkey.msg.msg253"));
                //ta.append(file_target_string);
                sbuf.append("\n\n");
                return tf;
            } catch (Exception ex) {
                mp.cleanupWorkDir(workdir);
                ex.printStackTrace();
            }
        } else if (sel_message instanceof StreamMessage) {
            StreamMessagePersister mp = new StreamMessagePersister((StreamMessage) sel_message);
            mp.setTextBuffer(sbuf);
            mp.setMsgDialog(cmp);
            try {
                workdir = mp.persistToFile();
                String file_target_string = thisproperty.getReal_file_directory() + QBrowserUtil.eliminateDameMoji(sel_message.getJMSMessageID()) + "_StreamMessage.zip";
                tf = new File(file_target_string);
                mp.zipUp(workdir, tf);
                sbuf.append(resources.getString("qkey.msg.msg253"));
                //ta.append(file_target_string);
                sbuf.append("\n\n");
                return tf;
            } catch (Exception ex) {
                mp.cleanupWorkDir(workdir);
                ex.printStackTrace();

            }
        } else if (sel_message instanceof ObjectMessage) {
            ObjectMessagePersister mp = new ObjectMessagePersister((ObjectMessage) sel_message);
            mp.setTextBuffer(sbuf);
            mp.setMsgDialog(cmp);
            try {
                workdir = mp.persistToFile();
                String file_target_string = thisproperty.getReal_file_directory() + QBrowserUtil.eliminateDameMoji(sel_message.getJMSMessageID()) + "_ObjectMessage.zip";
                tf = new File(file_target_string);
                mp.zipUp(workdir, tf);
                sbuf.append(resources.getString("qkey.msg.msg253"));
                //ta.append(file_target_string);
                sbuf.append("\n\n");
                return tf;
            } catch (Exception ex) {
                mp.cleanupWorkDir(workdir);
                ex.printStackTrace();
            }
        } else if (sel_message instanceof Message) {
            MessagePersister mp = new MessagePersister(sel_message);
            mp.setMsgDialog(cmp);
            mp.setTextBuffer(sbuf);
            try {
                workdir = mp.persistToFile();
                String file_target_string = thisproperty.getReal_file_directory() + QBrowserUtil.eliminateDameMoji(sel_message.getJMSMessageID()) + "_Message.zip";
                tf = new File(file_target_string);
                mp.zipUp(workdir, tf);
                sbuf.append(resources.getString("qkey.msg.msg253"));
                sbuf.append("\n\n");
                return tf;
            } catch (Exception ex) {
                mp.cleanupWorkDir(workdir);
                ex.printStackTrace();
            }
        }

        return tf;
    }

    public File localMessageToFile(javax.jms.Session session,LocalMessageContainer lmc, StringBuilder sbuf, JDialog cmp) throws Exception {

        File tf = null;
        File workdir = null;
        Message innermessage = lmc.getMessage();
        if(innermessage == null) {
            innermessage = lmc.getRealMessage(session);
        }

        if (innermessage instanceof TextMessage) {
            TextMessagePersister mp = new TextMessagePersister(lmc);
            mp.setTextBuffer(sbuf);
            mp.setMsgDialog(cmp);
            try {
                workdir = mp.persistToFile();
                String file_target_string = thisproperty.getReal_file_directory() + QBrowserUtil.eliminateDameMoji(lmc.getVmsgid()) + "_TextMessage.zip";
                tf = new File(file_target_string);
                mp.zipUp(workdir, tf);
                sbuf.append(resources.getString("qkey.msg.msg253"));
                sbuf.append("\n\n");
                cmp.getRootPane().updateUI();
                lmc.setReal_file_path(file_target_string);
                return tf;
            } catch (Exception ex) {
                mp.cleanupWorkDir(workdir);
                ex.printStackTrace();
            }
        } else if (innermessage instanceof BytesMessage) {
            BytesMessagePersister mp = new BytesMessagePersister(lmc);
            mp.setTextBuffer(sbuf);
            mp.setMsgDialog(cmp);
            try {
                workdir = mp.persistToFile();
                String file_target_string = thisproperty.getReal_file_directory()  + QBrowserUtil.eliminateDameMoji(lmc.getVmsgid()) + "_BytesMessage.zip";
                tf = new File(file_target_string);
                mp.zipUp(workdir, tf);
                sbuf.append(resources.getString("qkey.msg.msg253"));
                sbuf.append("\n\n");
                cmp.getRootPane().updateUI();
                lmc.setReal_file_path(file_target_string);
                return tf;
            } catch (Exception ex) {
                mp.cleanupWorkDir(workdir);
                ex.printStackTrace();
            }
        } else if (innermessage instanceof MapMessage) {
            MapMessagePersister mp = new MapMessagePersister(lmc);
            mp.setTextBuffer(sbuf);
            mp.setMsgDialog(cmp);
            try {
                workdir = mp.persistToFile();
                String file_target_string = thisproperty.getReal_file_directory() + QBrowserUtil.eliminateDameMoji(lmc.getVmsgid()) + "_MapMessage.zip";
                tf = new File(file_target_string);
                mp.zipUp(workdir, tf);
                sbuf.append(resources.getString("qkey.msg.msg253"));
                sbuf.append("\n\n");
                cmp.getRootPane().updateUI();
                lmc.setReal_file_path(file_target_string);
                return tf;
            } catch (Exception ex) {
                mp.cleanupWorkDir(workdir);
                ex.printStackTrace();
            }
        } else if (innermessage instanceof StreamMessage) {
            StreamMessagePersister mp = new StreamMessagePersister(lmc);
            mp.setTextBuffer(sbuf);
            mp.setMsgDialog(cmp);
            try {
                workdir = mp.persistToFile();
                String file_target_string = thisproperty.getReal_file_directory() + QBrowserUtil.eliminateDameMoji(lmc.getVmsgid()) + "_StreamMessage.zip";
                tf = new File(file_target_string);
                mp.zipUp(workdir, tf);
                sbuf.append(resources.getString("qkey.msg.msg253"));
                //ta.append(file_target_string);
                sbuf.append("\n\n");
                cmp.getRootPane().updateUI();
                lmc.setReal_file_path(file_target_string);
                return tf;
            } catch (Exception ex) {
                mp.cleanupWorkDir(workdir);
                ex.printStackTrace();
            }
        } else if (innermessage instanceof ObjectMessage) {
            ObjectMessagePersister mp = new ObjectMessagePersister(lmc);
            mp.setTextBuffer(sbuf);
            mp.setMsgDialog(cmp);
            try {
                workdir = mp.persistToFile();
                String file_target_string = thisproperty.getReal_file_directory() + QBrowserUtil.eliminateDameMoji(lmc.getVmsgid()) + "_ObjectMessage.zip";
                tf = new File(file_target_string);
                mp.zipUp(workdir, tf);
                sbuf.append(resources.getString("qkey.msg.msg253"));
                //ta.append(file_target_string);
                sbuf.append("\n\n");
                cmp.getRootPane().updateUI();
                lmc.setReal_file_path(file_target_string);
                return tf;
            } catch (Exception ex) {
                mp.cleanupWorkDir(workdir);
                ex.printStackTrace();
            }
        } else if (innermessage instanceof Message) {
            MessagePersister mp = new MessagePersister(lmc);
            mp.setMsgDialog(cmp);
            mp.setTextBuffer(sbuf);
            try {
                workdir = mp.persistToFile();
                String file_target_string = thisproperty.getReal_file_directory() + QBrowserUtil.eliminateDameMoji(lmc.getVmsgid()) + "_Message.zip";
                tf = new File(file_target_string);
                mp.zipUp(workdir, tf);
                sbuf.append(resources.getString("qkey.msg.msg253"));
                sbuf.append("\n\n");
                cmp.getRootPane().updateUI();
                lmc.setReal_file_path(file_target_string);
                return tf;
            } catch (Exception ex) {
                mp.cleanupWorkDir(workdir);
                ex.printStackTrace();
            }
        }

        return tf;
    }
}


}
