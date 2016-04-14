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
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.jms.Session;
import javax.jms.StreamMessage;

/**
 *
 * @author takemura
 */
public class StreamMessageReader extends PersistedMessageReader {

    ArrayList stream_data = new ArrayList();

    void readFileAndSetPropertyValue(String filepath, Property prop) throws Exception {
        //タイプ判定
        String type = prop.getProperty_type();


        if (type.equals(Property.STRING_TYPE)) {

             prop.setProperty_value(readFileIntoString(new File(filepath)));
             prop.selfValidate();


        } else if (type.equals(Property.INT_TYPE)) {

             prop.setProperty_value(Integer.parseInt(readFileIntoString(new File(filepath))));
             prop.selfValidate();

        } else if (type.equals(Property.BOOLEAN_TYPE)) {

             prop.setProperty_value(Boolean.parseBoolean(readFileIntoString(new File(filepath))));
             prop.selfValidate();

        } else if (type.equals(Property.BYTES_TYPE)) {

             prop.setProperty_value(readFileIntoBytes(new File(filepath)));
             prop.validated_type = Property.PASSTHROUGH_TYPE_INT;
             prop.selfValidate();

        } else if (type.equals(Property.BYTE_TYPE)) {

             prop.setProperty_value(Byte.parseByte(readFileIntoString(new File(filepath))));
             prop.selfValidate();

        } else if (type.equals(Property.CHARACTER_TYPE)) {

             prop.setProperty_value(readFileIntoString(new File(filepath)));
             prop.selfValidate();

        } else if (type.equals(Property.DOUBLE_TYPE)) {

             prop.setProperty_value(Double.parseDouble(readFileIntoString(new File(filepath))));
             prop.selfValidate();

        } else if (type.equals(Property.FLOAT_TYPE)) {

             prop.setProperty_value(Float.parseFloat(readFileIntoString(new File(filepath))));
             prop.selfValidate();

        } else if (type.equals(Property.LONG_TYPE)) {

             prop.setProperty_value(Long.parseLong(readFileIntoString(new File(filepath))));
             prop.selfValidate();

        } else if (type.equals(Property.SHORT_TYPE)) {

             prop.setProperty_value(Short.parseShort(readFileIntoString(new File(filepath))));
             prop.selfValidate();

        }

    }

    @Override
    public File readPersistedMessage(File msgArchive) throws Exception {



        File workdirFile = super.readPersistedMessage(msgArchive);

        BufferedReader br = null;


        File def_file = new File(workdirFile.getAbsolutePath() + File.separator + "StreamMessageDef");
        if (def_file == null || !def_file.exists()) {
            clearDir(workdirFile);
            throw new IOException(QBrowserV2.resources.getString("qkey.msg.msg334") + def_file.getName() +
                    QBrowserV2.resources.getString("qkey.msg.msg335"));
        }


         try {

         //StreamMessage定義ファイルを読み込んで解析する
         br = new BufferedReader(new FileReader(def_file));
         String line = null;
         while ((line = br.readLine()) != null) {
             Property map_prop = new Property();
             ArrayList ar = QBrowserUtil.parseDelimitedString(line, QBrowserV2.MAGIC_SEPARATOR);
             for (int i = 0; i < ar.size() ; i++) {
                 String token = (String)ar.get(i);
                 switch(i) {
                     case 0: //シーケンス番号
                         //頭から1から順番に書かれている
                         map_prop.setKey(token);
                         break;
                     case 1: //プロパティタイプ
                         map_prop.setProperty_type(token);
                         break;
                     case 2: //ファイル名
                         //ファイルを読んで中身をtypeに従って設定する
                         String fullpath = workdirFile.getAbsolutePath() + File.separator + "StreamMessage" + File.separator + token;
                         readFileAndSetPropertyValue(fullpath, map_prop);
                         break;

                 }
             }

             stream_data.add(map_prop);
         }

         } catch (Exception ie) {
             clearDir(workdirFile);
             ie.printStackTrace();
             throw new IOException(QBrowserV2.resources.getString("qkey.msg.msg331") + ie.getMessage() + "\n\nFile=" + def_file.getName());
         } finally {
             if (br != null) {
                 br.close();
                 br = null;
             }
         }


        return workdirFile;

    }

    @Override
    public File readPersistedMessageWithLazyLoad(File msgArchive) throws Exception {
        File workdirFile = super.readPersistedMessageWithLazyLoad(msgArchive);
        return workdirFile;
    }

    @Override
    public LocalMessageContainer recreateMessagefromReadData(Session session) throws Exception {

        StreamMessage smsg = session.createStreamMessage();
        LocalMessageContainer lmc = new LocalMessageContainer();

        lmc.setReal_file_path(source_file_path);

        if (properties != null) {
            QBrowserUtil.copyUserProperties(properties, smsg);
        }

        lmc.setMessage(smsg);

        if (headers != null) {
            QBrowserUtil.copyMessageHeaders(headers, smsg);
            QBrowserUtil.populateHeadersOfLocalMessageContainer(headers, lmc);

        }

        try {

            for (int i = 0; i < stream_data.size(); i++) {


                Property sm_body_data = (Property) stream_data.get(i);

                switch (sm_body_data.validated_type) {
                    case Property.INVALID_TYPE_INT:
                        //VALIDではない、セットスキップ
                        break;

                    case Property.PASSTHROUGH_TYPE_INT:
                        smsg.writeBytes(sm_body_data.getProperty_valueASBytes());
                        break;

                    case Property.STRING_TYPE_INT:
                        smsg.writeString(sm_body_data.getProperty_valueASString());
                        break;

                    case Property.BOOLEAN_TYPE_INT:
                        smsg.writeBoolean(sm_body_data.getProperty_valueASBoolean());
                        break;

                    case Property.INT_TYPE_INT:
                        smsg.writeInt(sm_body_data.getProperty_valueASInt());
                        break;

                    case Property.BYTE_TYPE_INT:
                        smsg.writeByte(sm_body_data.getProperty_valueASByte());
                        break;

                    case Property.CHARACTER_TYPE_INT:
                        smsg.writeChar(sm_body_data.getProperty_valueASCharacter());
                        break;

                    case Property.BYTES_TYPE_INT:

                        byte[] bytesarray = QBrowserUtil.extractBytes(sm_body_data.getProperty_valueASString());
                        if (bytesarray == null) {
                            throw new Exception("Q0021");
                        }
                        smsg.writeBytes(bytesarray);
                        break;

                    case Property.DOUBLE_TYPE_INT:
                        smsg.writeDouble(sm_body_data.getProperty_valueASDouble());
                        break;

                    case Property.FLOAT_TYPE_INT:
                        smsg.writeFloat(sm_body_data.getProperty_valueASFloat());
                        break;

                    case Property.LONG_TYPE_INT:
                        smsg.writeLong(sm_body_data.getProperty_valueASLong());
                        break;

                    case Property.SHORT_TYPE_INT:
                        smsg.writeShort(sm_body_data.getProperty_valueASShort());
                        break;

                    default:
                        //NOP
                        break;
                }



            } //end for


        } catch (Throwable thex) {

            thex.printStackTrace();
        }


        return lmc;

    }

    @Override
    public LocalMessageContainer recreateMessagefromReadDataWithLazyLoad() throws Exception {
         LocalMessageContainer lmc = super.recreateMessagefromReadDataWithLazyLoad();
         lmc.setMessage_type(QBrowserV2.STREAMMESSAGE);
         return lmc;

    }
}
