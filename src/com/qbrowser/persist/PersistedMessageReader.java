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
import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.jms.Message;
import javax.jms.Session;

/**
 *
 * @author takemura
 */
public class PersistedMessageReader {

    ArrayList headers = new ArrayList();
    ArrayList properties = new ArrayList();
    String source_file_path;

    public PersistedMessageReader() {

    }


    public static void clearDir(File workdirFile) {
        if (workdirFile == null) {
            return;
        }
            cleanupWorkDir(workdirFile);
            workdirFile.delete();
    }

    static void cleanupWorkDir(File workdir) {
        File[] files = workdir.listFiles();
        for (int i  = 0; i < files.length ; i++) {
            if (files[i].isDirectory()) {
                cleanupWorkDir(files[i]);
                if(files[i].listFiles().length == 0) {
                    files[i].delete();
                }
            } else if (files[i].isFile()) {
                try {
                    files[i].delete();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public LocalMessageContainer recreateMessagefromReadData(Session session) throws Exception {

        Message msg = session.createMessage();
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
        lmc.setReal_file_path(source_file_path);
        //lmc.setMessage_type(QBrowserV2.MESSAGE);

        return lmc;

    }

    //実メッセージのローディングはメモリ節約のためオンデマンドとする
    public LocalMessageContainer recreateMessagefromReadDataWithLazyLoad() throws Exception {
        LocalMessageContainer lmc = new LocalMessageContainer();

        lmc.setReal_file_path(source_file_path);


         if (headers != null) {
            QBrowserUtil.populateHeadersOfLocalMessageContainer(headers, lmc);
         }

        //注：メッセージはnullのまま
        //ボディとプロパティはどうせ一覧には出ないので要求されたらファイルからロード
        //Messageなのでボディは空。
        lmc.setMessage_type(QBrowserV2.MESSAGE);

        return lmc;
    }


    public File readPersistedMessage2(File msgArchive) throws Exception {

        if (msgArchive == null || !msgArchive.exists()) {
            throw new Exception("msg archive file not found. It may be removed.");
        }

        source_file_path = msgArchive.getAbsolutePath();
        File workdirFile = null;

        try {

            //ワークエリアに解凍
            String workdir = QBrowserUtil.getQBrowserTempFileDir() + "JMS" + System.nanoTime() + File.separator;
            workdirFile = new File(workdir);
            workdirFile.mkdirs();

            unZipFile2(msgArchive, workdirFile);

            //JMSHeaders.txtとUserProperties.txtはworkdir直下に出てくる
            restorePersistedJMSHeaders(new File(workdir + "JMSHeaders.txt"));
            //restorePersistedUserProperties(new File(workdir + "UserProperties.txt"));

        } catch (Exception e) {
            clearDir(workdirFile);
            throw e;
        }

        return workdirFile;


    }


    public File readPersistedMessage(File msgArchive) throws Exception {

        if (msgArchive == null || !msgArchive.exists()) {
            throw new Exception("msg archive file not found. It may be removed.");
        }

        source_file_path = msgArchive.getAbsolutePath();
        File workdirFile = null;

        try {

            //ワークエリアに解凍
            String workdir = QBrowserUtil.getQBrowserTempFileDir() + "JMS" + System.nanoTime() + File.separator;
            workdirFile = new File(workdir);
            workdirFile.mkdirs();

            unZipFile(msgArchive, workdirFile);

            //JMSHeaders.txtとUserProperties.txtはworkdir直下に出てくる
            restorePersistedJMSHeaders(new File(workdir + "JMSHeaders.txt"));
            restorePersistedUserProperties(new File(workdir + "UserProperties.txt"));

        } catch (Exception e) {
            clearDir(workdirFile);
            throw e;
        }

        return workdirFile;


    }

    public File readPersistedMessageWithLazyLoad(File msgArchive) throws Exception {
            File workdirFile = readPersistedMessage2(msgArchive);
            return workdirFile;
    }
    
    private String replaceFileSeparator(String original) {
        char[] input = original.toCharArray();
        StringBuffer result = new StringBuffer();
        for (int i = 0 ; i < input.length; i++) {
            if(input[i] == '/') {
                result.append(File.separatorChar);
            } else {
                result.append(input[i]);
            }
        }

        return result.toString();
    }

    private void unZipFile(File file, File tenkaisakiDir) {
        ZipFile zip = null;
        try {
            zip = new ZipFile(file);
            
            for(Enumeration ezip = zip.entries(); ezip.hasMoreElements();) {
                Object ent = ezip.nextElement();
                ZipEntry ze = (ZipEntry)ent;
                String originalzipentry = ze.getName();
                String targetfilename = tenkaisakiDir.getAbsolutePath() + File.separator + replaceFileSeparator(originalzipentry);

                File targetfile = new File(targetfilename);

                //ターゲットファイルがディレクトリだったら自分自身をmkdir
                if (ze.isDirectory()) {
                    targetfile.mkdirs();
                } else {
                    //それ以外なら親ディレクトリを作成
                    targetfile.getParentFile().mkdirs();

                    BufferedInputStream bis = new BufferedInputStream(zip.getInputStream(ze));
                    BufferedOutputStream bo = new BufferedOutputStream(new FileOutputStream(targetfile));
                    int j;
                    while((j = bis.read()) != -1) {
                        bo.write(j);
                    }
                    bis.close();
                    bo.close();

                }

                



            }

        } catch (Exception zipe) {
            zipe.printStackTrace();
        } finally {
            
            if (zip != null) {
                try {
                zip.close();
                } catch (IOException ioe) {}
                zip = null;
            }
        }


    }

    private void unZipFile2(File file, File tenkaisakiDir) {
        //必要なファイルだけ解凍する
        //JMSHeaders.txt
        //なんとかbodysizeファイル
        ZipFile zip = null;
        try {
            zip = new ZipFile(file);
            boolean headers_file_found = false;
            boolean bodysize_file_found = true;
            String srcfilename = file.getName();
            if (srcfilename.endsWith("_TextMessage.zip") || srcfilename.endsWith("_BytesMessage.zip")) {
                bodysize_file_found = false;
            }

            for(Enumeration ezip = zip.entries(); ezip.hasMoreElements();) {
                Object ent = ezip.nextElement();
                ZipEntry ze = (ZipEntry)ent;
                String originalzipentry = ze.getName();
                //System.out.println(originalzipentry);
                if (originalzipentry.indexOf("JMSHeaders.txt") != -1) {
                    headers_file_found = true;
                } else if (!bodysize_file_found && originalzipentry.endsWith("bodysize")) {
                    bodysize_file_found = true;
                }
                String targetfilename = tenkaisakiDir.getAbsolutePath() + File.separator + replaceFileSeparator(originalzipentry);

                File targetfile = new File(targetfilename);

                //ターゲットファイルがディレクトリだったら自分自身をmkdir
                if (ze.isDirectory()) {
                    targetfile.mkdirs();
                } else {
                    //それ以外なら親ディレクトリを作成
                    targetfile.getParentFile().mkdirs();

                    BufferedInputStream bis = new BufferedInputStream(zip.getInputStream(ze));
                    BufferedOutputStream bo = new BufferedOutputStream(new FileOutputStream(targetfile));
                    int j;
                    while((j = bis.read()) != -1) {
                        bo.write(j);
                    }
                    bis.close();
                    bo.close();

                }


              if (headers_file_found && bodysize_file_found) {
                  break;
              }


            }

        } catch (Exception zipe) {
            zipe.printStackTrace();
        } finally {

            if (zip != null) {
                try {
                zip.close();
                } catch (IOException ioe) {}
                zip = null;
            }
        }


    }

    public static void main(String[] args) {
        PersistedMessageReader pmr = new PersistedMessageReader();
        try {
            
        File workdir = pmr.readPersistedMessage(new File("c:\\temp1\\IDPSQ96050-3196-1245141988656-00111_Message.zip"));
        
        pmr.clearDir(workdir);


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void parseMultipleLineStringProperty(ArrayList store, ArrayList targetArray) {


        StringBuilder sb = new StringBuilder();

        //最初の行にはキーが入っているはず
        String firstline = (String)store.get(0);

        //Key値を抽出
        int secondPar = firstline.substring(1).indexOf("\"");
        String keyvalue = firstline.substring(1, secondPar + 1);
        //System.out.print(keyvalue + "=");

        int startstr = firstline.indexOf(MessagePersister.STRING_START);
        String string_value = firstline.substring(startstr + MessagePersister.STRING_START.length(), firstline.length());
        //System.out.println(string_value);
        sb.append(string_value).append("\n");

        //2行目以降も調べるっす。
        for (int i = 1; i < store.size(); i++) {
            String theline = (String)store.get(i);
             int endstr = theline.indexOf(MessagePersister.STRING_END);
             if (endstr != -1) {
                 //終端文字が見つかった
                 //終端文字までの文字列を取得
                 String end2 = theline.substring(0, endstr);
                 //System.out.println(end2);
                 sb.append(end2).append("\n");
             } else {
                 //まだ終端文字が見つからないので、これは文字列の真っ只中。
                 //System.out.println(theline);
                 sb.append(theline).append("\n");
             }
        }

        Property prop = new Property();
        prop.setKey(keyvalue);
        prop.setProperty_type(Property.STRING_TYPE);
        prop.setProperty_value(sb.toString());
            try {
              prop.selfValidate();
              targetArray.add(prop);
            } catch (Exception e) {
                //入れない
            }
    }

    void parseOneLineStringProperty(String orig, ArrayList targetArray) {
        //Key値を抽出
        int secondPar = orig.substring(1).indexOf("\"");
        String keyvalue = orig.substring(1, secondPar + 1);
        //System.out.print(keyvalue + "=");
        //文字列開始位置を特定
        int startstr = orig.indexOf(MessagePersister.STRING_START);
        int endstr = orig.indexOf(MessagePersister.STRING_END);
        String string_value = orig.substring(startstr + MessagePersister.STRING_START.length(), endstr);
        //System.out.println(string_value);
        Property prop = new Property();
        prop.setKey(keyvalue);
        prop.setProperty_type(Property.STRING_TYPE);
        prop.setProperty_value(string_value);
            try {
              prop.selfValidate();
              targetArray.add(prop);
            } catch (Exception e) {
                //入れない
            }
    }

    void parseOneLineBooleanProperty(String orig, ArrayList targetArray) {
        //Key値を抽出
        int secondPar = orig.substring(1).indexOf("\"");
        String keyvalue = orig.substring(1, secondPar + 1);
        //System.out.print(keyvalue + "=");
        int equalspos = orig.indexOf("=");
        if (equalspos != -1) {
            String value1 = orig.substring(equalspos+1);
            //System.out.println("value1 " + value1);
            int firstpar = value1.indexOf("\"");
            int secondpar = value1.substring(1).indexOf("\"");
            String target_value = value1.substring(firstpar+1, secondpar+1);
            //System.out.println(target_value);
            Property prop = new Property();
            prop.setKey(keyvalue);
            prop.setProperty_type(Property.BOOLEAN_TYPE);
            prop.setProperty_value(new Boolean(target_value));
            try {
              prop.selfValidate();
              targetArray.add(prop);
            } catch (Exception e) {
                //入れない
            }
        }
    }

    void parseOneLineDoubleProperty(String orig, ArrayList targetArray) {
        //Key値を抽出
        int secondPar = orig.substring(1).indexOf("\"");
        String keyvalue = orig.substring(1, secondPar + 1);
        //System.out.print(keyvalue + "=");
        int equalspos = orig.indexOf("=");
        if (equalspos != -1) {
            String value1 = orig.substring(equalspos+1);
            //System.out.println("value1 " + value1);
            int firstpar = value1.indexOf("\"");
            int secondpar = value1.substring(1).indexOf("\"");
            String target_value = value1.substring(firstpar+1, secondpar+1);
            //System.out.println(target_value);
            Property prop = new Property();
            prop.setKey(keyvalue);
            prop.setProperty_type(Property.DOUBLE_TYPE);
            prop.setProperty_value(new Double(target_value));
            try {
              prop.selfValidate();
              targetArray.add(prop);
            } catch (Exception e) {
                //入れない
            }
        }
    }

    void parseOneLineFloatProperty(String orig, ArrayList targetArray) {
        //Key値を抽出
        int secondPar = orig.substring(1).indexOf("\"");
        String keyvalue = orig.substring(1, secondPar + 1);
        //System.out.print(keyvalue + "=");
        int equalspos = orig.indexOf("=");
        if (equalspos != -1) {
            String value1 = orig.substring(equalspos+1);
            //System.out.println("value1 " + value1);
            int firstpar = value1.indexOf("\"");
            int secondpar = value1.substring(1).indexOf("\"");
            String target_value = value1.substring(firstpar+1, secondpar+1);
            //System.out.println(target_value);
            Property prop = new Property();
            prop.setKey(keyvalue);
            prop.setProperty_type(Property.FLOAT_TYPE);
            prop.setProperty_value(new Float(target_value));
            try {
              prop.selfValidate();
              targetArray.add(prop);
            } catch (Exception e) {
                //入れない
            }
        }
    }

    void parseOneLineIntProperty(String orig, ArrayList targetArray) {
        //Key値を抽出
        int secondPar = orig.substring(1).indexOf("\"");
        String keyvalue = orig.substring(1, secondPar + 1);
        //System.out.print(keyvalue + "=");
        int equalspos = orig.indexOf("=");
        if (equalspos != -1) {
            String value1 = orig.substring(equalspos+1);
            //System.out.println("value1 " + value1);
            int firstpar = value1.indexOf("\"");
            int secondpar = value1.substring(1).indexOf("\"");
            String target_value = value1.substring(firstpar+1, secondpar+1);
            //System.out.println(target_value);
            Property prop = new Property();
            prop.setKey(keyvalue);
            prop.setProperty_type(Property.INT_TYPE);
            prop.setProperty_value(new Integer(target_value));
            try {
              prop.selfValidate();
              targetArray.add(prop);
            } catch (Exception e) {
                //入れない
            }
        }
    }

    String readFileIntoString(File infile) throws Exception {

            java.io.FileInputStream fi = null;
            ByteArrayOutputStream baos = null;

            String retval = null;

            try {

            fi = new FileInputStream(infile);
            baos = new ByteArrayOutputStream();

            byte buf[] = new byte[1024];
            int len = 0;

            int filesizecount = 0;

            while ((len = fi.read(buf)) != -1) {
                filesizecount += buf.length;
                baos.write(buf, 0, len);
            }

            retval = baos.toString();

            } catch (Exception ie) {
                throw ie;
            } finally {

                if (fi != null) {
                    try {
                     fi.close();
                    } catch (IOException iie) {}
                    fi = null;
                }

                if (baos != null) {
                    try {
                     baos.close();
                    } catch (IOException iie) {}
                    baos = null;
                }
            }

            return retval;

    }

    byte[] readFileIntoBytes(File infile) throws Exception {
            java.io.FileInputStream fi = null;
            ByteArrayOutputStream baos = null;
            byte[] retval = null;

            try {

            fi = new FileInputStream(infile);
            baos = new ByteArrayOutputStream();

            byte buf[] = new byte[1024];
            int len = 0;

            int filesizecount = 0;

            while ((len = fi.read(buf)) != -1) {
                filesizecount += buf.length;
                baos.write(buf, 0, len);
            }

            retval = baos.toByteArray();

            } catch (Exception ie) {
                throw ie;
            } finally {

                if (fi != null) {
                    try {
                     fi.close();
                    } catch (IOException iie) {}
                    fi = null;
                }

                if (baos != null) {
                    try {
                     baos.close();
                    } catch (IOException iie) {}
                    baos = null;
                }
            }

            return retval;
    }

    void parseOneLineLongProperty(String orig, ArrayList targetArray) {
        //Key値を抽出
        int secondPar = orig.substring(1).indexOf("\"");
        String keyvalue = orig.substring(1, secondPar + 1);
        //System.out.print(keyvalue + "=");
        int equalspos = orig.indexOf("=");
        if (equalspos != -1) {
            String value1 = orig.substring(equalspos+1);
            //System.out.println("value1 " + value1);
            int firstpar = value1.indexOf("\"");
            int secondpar = value1.substring(1).indexOf("\"");
            String target_value = value1.substring(firstpar+1, secondpar+1);
            //System.out.println(target_value);
            Property prop = new Property();
            prop.setKey(keyvalue);
            prop.setProperty_type(Property.LONG_TYPE);
            prop.setProperty_value(new Long(target_value));
            try {
              prop.selfValidate();
              targetArray.add(prop);
            } catch (Exception e) {
                //入れない
            }
        }
    }

    void parseOneLineShortProperty(String orig, ArrayList targetArray) {
        //Key値を抽出
        int secondPar = orig.substring(1).indexOf("\"");
        String keyvalue = orig.substring(1, secondPar + 1);
        //System.out.print(keyvalue + "=");
        int equalspos = orig.indexOf("=");
        if (equalspos != -1) {
            String value1 = orig.substring(equalspos+1);
            //System.out.println("value1 " + value1);
            int firstpar = value1.indexOf("\"");
            int secondpar = value1.substring(1).indexOf("\"");
            String target_value = value1.substring(firstpar+1, secondpar+1);
            //System.out.println(target_value);
            Property prop = new Property();
            prop.setKey(keyvalue);
            prop.setProperty_type(Property.SHORT_TYPE);
            prop.setProperty_value(new Short(target_value));
            try {
              prop.selfValidate();
              targetArray.add(prop);
            } catch (Exception e) {
                //入れない
            }
        }
    }

    void restorePersistedJMSHeaders(File infile) throws IOException {
        //MessagePersisterで書いた情報を読み取って、headersに復元する
        if (infile == null || !infile.exists()) {
            throw new IOException(QBrowserV2.resources.getString("qkey.msg.msg329"));
        }
        BufferedReader br = null;
        try {

           br = new BufferedReader(new FileReader(infile));

           boolean inStringProperty = false;
           ArrayList string_store = new ArrayList();

           String line = null;
           while ((line = br.readLine()) != null) {
               if (inStringProperty) {
                   if (line.indexOf(MessagePersister.STRING_END) != -1) {
                       //String終端文字が見つかった
                       string_store.add(line);
                       //溜め込んでいた文字列を使ってパース開始
                       parseMultipleLineStringProperty(string_store, headers);
                       string_store.clear();
                       inStringProperty = false;
                   } else {
                       //終端文字も登場しないので、これは文字列真っ只中
                       string_store.add(line);
                   }
               } else {
                   if (line.indexOf(MessagePersister.STRING_START) != -1) {
                       //String開始文字が見つかった
                       //でも一行で終了するStringもある。終端文字を同一行でチェック
                       if (line.indexOf(MessagePersister.STRING_END) != -1) {
                           //一行ストリング決定！
                           parseOneLineStringProperty(line, headers);
                       } else {
                           //同一行に見つからないので、それは複数行String
                           inStringProperty = true;
                           //保存しておく必要あり
                           string_store.add(line);
                       }
                   } else if (line.indexOf("," + Property.BOOLEAN_TYPE) != -1) {
                       //BooleanType
                       parseOneLineBooleanProperty(line, headers);
                   } else if (line.indexOf("," + Property.DOUBLE_TYPE) != -1) {
                       //Doubletype
                       parseOneLineDoubleProperty(line, headers);
                   } else if (line.indexOf("," + Property.FLOAT_TYPE) != -1) {
                       //FloatType
                       parseOneLineFloatProperty(line, headers);
                   } else if (line.indexOf("," + Property.INT_TYPE) != -1) {
                       //IntType
                       parseOneLineIntProperty(line, headers);
                   } else if (line.indexOf("," + Property.LONG_TYPE) != -1) {
                       //LongType
                       parseOneLineLongProperty(line, headers);
                   } else if (line.indexOf("," + Property.SHORT_TYPE) != -1) {
                       //ShortType
                       parseOneLineShortProperty(line, headers);
                   }
               }
           }

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new IOException(QBrowserV2.resources.getString("qkey.msg.msg331") + ex.getMessage() + "\n\nFile=" + infile.getName());
        } finally {
            if (br != null) {
                br.close();
                br = null;
            }
        }
    }



    void restorePersistedUserProperties(File infile) throws IOException {
        if (infile == null || !infile.exists()) {
            throw new IOException(QBrowserV2.resources.getString("qkey.msg.msg330"));
        }

        //MessagePersisterで書いた情報を読み取って、headersに復元する
        BufferedReader br = null;
        try {

           br = new BufferedReader(new FileReader(infile));

           boolean inStringProperty = false;
           ArrayList string_store = new ArrayList();

           String line = null;
           while ((line = br.readLine()) != null) {
               if (inStringProperty) {
                   if (line.indexOf(MessagePersister.STRING_END) != -1) {
                       //String終端文字が見つかった
                       string_store.add(line);
                       //溜め込んでいた文字列を使ってパース開始
                       parseMultipleLineStringProperty(string_store, properties);
                       string_store.clear();
                       inStringProperty = false;
                   } else {
                       //終端文字も登場しないので、これは文字列真っ只中
                       string_store.add(line);
                   }
               } else {
                   if (line.indexOf(MessagePersister.STRING_START) != -1) {
                       //String開始文字が見つかった
                       //でも一行で終了するStringもある。終端文字を同一行でチェック
                       if (line.indexOf(MessagePersister.STRING_END) != -1) {
                           //一行ストリング決定！
                           parseOneLineStringProperty(line, properties);
                       } else {
                           //同一行に見つからないので、それは複数行String
                           inStringProperty = true;
                           //保存しておく必要あり
                           string_store.add(line);
                       }
                   } else if (line.indexOf("," + Property.BOOLEAN_TYPE) != -1) {
                       //BooleanType
                       parseOneLineBooleanProperty(line, properties);
                   } else if (line.indexOf("," + Property.DOUBLE_TYPE) != -1) {
                       //Doubletype
                       parseOneLineDoubleProperty(line, properties);
                   } else if (line.indexOf("," + Property.FLOAT_TYPE) != -1) {
                       //FloatType
                       parseOneLineFloatProperty(line, properties);
                   } else if (line.indexOf("," + Property.INT_TYPE) != -1) {
                       //IntType
                       parseOneLineIntProperty(line, properties);
                   } else if (line.indexOf("," + Property.LONG_TYPE) != -1) {
                       //LongType
                       parseOneLineLongProperty(line, properties);
                   } else if (line.indexOf("," + Property.SHORT_TYPE) != -1) {
                       //ShortType
                       parseOneLineShortProperty(line, properties);
                   }
               }
           }

        } catch (Exception ex) {
            ex.printStackTrace();
             throw new IOException(QBrowserV2.resources.getString("qkey.msg.msg331") + ex.getMessage() + "\n\nFile=" + infile.getName());
        } finally {
            if (br != null) {
                br.close();
                br = null;
            }
        }
    }

}
