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
import javax.jms.MapMessage;
import javax.jms.Session;

/**
 *
 * @author takemura
 */
public class MapMessageReader extends PersistedMessageReader {

    ArrayList map_properties = new ArrayList();

    void readFileAndSetPropertyValue(String filepath, Property prop) {
        //タイプ判定
        String type = prop.getProperty_type();

        try {

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

        } catch (Exception convertEx) {
            convertEx.printStackTrace();
        }
    }

    @Override
    public File readPersistedMessage(File msgArchive) throws Exception {

        File workdirFile = super.readPersistedMessage(msgArchive);

        File def_file = new File(workdirFile.getAbsolutePath() + File.separator + "MapMessageDef");

        if (def_file == null || !def_file.exists()) {
            throw new IOException(QBrowserV2.resources.getString("qkey.msg.msg334") + def_file.getName() +
                    QBrowserV2.resources.getString("qkey.msg.msg335"));
        }

        BufferedReader br = null;

        try {

         //MapMessage定義ファイルを読み込んで解析する
         br = new BufferedReader(new FileReader(def_file));
         String line = null;
         while ((line = br.readLine()) != null) {
             Property map_prop = new Property();
             ArrayList ar = QBrowserUtil.parseDelimitedString(line, QBrowserV2.MAGIC_SEPARATOR);
             for (int i = 0; i < ar.size() ; i++) {
                 String token = (String)ar.get(i);
                 switch(i) {
                     case 0: //シーケンス番号
                         //頭から1から順番に書かれているはずなのでとりあえず無視
                         break;
                     case 1: //プロパティKEY名
                         map_prop.setKey(token);
                         break;
                     case 2: //プロパティタイプ
                         map_prop.setProperty_type(token);
                         break;
                     case 3: //ファイル名
                         //ファイルを読んで中身をtypeに従って設定する
                         String fullpath = workdirFile.getAbsolutePath() + File.separator + "MapMessage" + File.separator + token;
                         this.readFileAndSetPropertyValue(fullpath, map_prop);
                         break;

                 }
             }

             map_properties.add(map_prop);
         }

        } catch (Exception e) {
            clearDir(workdirFile);
            e.printStackTrace();
            throw new IOException(QBrowserV2.resources.getString("qkey.msg.msg331") + e.getMessage() + "\n\nFile=" + def_file.getName());
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

        MapMessage mapmsg = session.createMapMessage();
        LocalMessageContainer lmc = new LocalMessageContainer();

        if (properties != null) {
            QBrowserUtil.copyUserProperties(properties, mapmsg);
        }

        lmc.setMessage(mapmsg);
        lmc.setReal_file_path(source_file_path);

        if (headers != null) {
            QBrowserUtil.copyMessageHeaders(headers, mapmsg);
            QBrowserUtil.populateHeadersOfLocalMessageContainer(headers, lmc);

        }

        try {

        for (int i = 0; i < map_properties.size(); i++) {

         
            Property mapm_body_data = (Property)map_properties.get(i);

            String key = mapm_body_data.getKey();

            //keyがnullのものについては、未入力と判定する
            if (key != null) {

                switch (mapm_body_data.validated_type) {
                    case Property.INVALID_TYPE_INT:
                        //VALIDではない、セットスキップ
                        break;

                    case Property.PASSTHROUGH_TYPE_INT:
                        mapmsg.setBytes(key, mapm_body_data.getProperty_valueASBytes());
                        break;

                    case Property.STRING_TYPE_INT:
                        mapmsg.setString(key, mapm_body_data.getProperty_valueASString());
                        break;

                    case Property.BOOLEAN_TYPE_INT:
                        mapmsg.setBoolean(key, mapm_body_data.getProperty_valueASBoolean());
                        break;

                    case Property.INT_TYPE_INT:
                        mapmsg.setInt(key, mapm_body_data.getProperty_valueASInt());
                        break;

                    case Property.BYTE_TYPE_INT:
                        mapmsg.setByte(key, mapm_body_data.getProperty_valueASByte());
                        break;

                    case Property.BYTES_TYPE_INT:

                        byte[] bytesarray = QBrowserUtil.extractBytes(mapm_body_data.getProperty_valueASString());
                        if (bytesarray == null) {
                            throw new Exception("Q0021");
                        }
                        mapmsg.setBytes(key, bytesarray);
                        break;

                    case Property.DOUBLE_TYPE_INT:
                        mapmsg.setDouble(key, mapm_body_data.getProperty_valueASDouble());
                        break;

                    case Property.FLOAT_TYPE_INT:
                        mapmsg.setFloat(key, mapm_body_data.getProperty_valueASFloat());
                        break;

                    case Property.LONG_TYPE_INT:
                        mapmsg.setLong(key, mapm_body_data.getProperty_valueASLong());
                        break;

                    case Property.SHORT_TYPE_INT:
                        mapmsg.setShort(key, mapm_body_data.getProperty_valueASShort());
                        break;

                    default:
                        //NOP
                        break;
                }



            }





        } //end for

        } catch (Throwable thex) {
            throw new IOException("MapMessage " + QBrowserV2.resources.getString("qkey.msg.msg336") + thex.getMessage());
        }


        return lmc;

    }

    @Override
    public LocalMessageContainer recreateMessagefromReadDataWithLazyLoad() throws Exception {
         LocalMessageContainer lmc = super.recreateMessagefromReadDataWithLazyLoad();
         lmc.setMessage_type(QBrowserV2.MAPMESSAGE);
         return lmc;

    }

}
