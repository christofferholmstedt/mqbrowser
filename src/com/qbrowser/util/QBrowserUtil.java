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

package com.qbrowser.util;

import com.qbrowser.QBrowserV2;
import com.qbrowser.container.MessageContainer;
import com.qbrowser.localstore.LocalMessageContainer;
import com.qbrowser.localstore.genericdest.LocalQueue;
import com.qbrowser.localstore.genericdest.LocalTopic;
import com.qbrowser.property.Property;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Label;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.StringTokenizer;
import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 *
 * @author takemura
 */
public class QBrowserUtil {

    public static final String PASSFILE_NAME = "passfile.txt";
    public static String[] pad = {"", "0", "00", "000", "0000"};
    static JDialog errDialog = null;
    static JPanel panel_parent;
    public static final String TOPIC_SUFFIX_LOWER = ": topic";
    public static final String QUEUE_SUFFIX_LOWER = ": queue";
    public static final String LOCALSTORE_SUFFIX_LOWER = ": local_store";
    public static final String TOPIC_PREFIX_LOWER = "topic://";
    public static final String QUEUE_PREFIX_LOWER = "queue://";

    public static void setPanel_Parent(JPanel value) {
        panel_parent = value;
    }

    public static File checkIfPassFileExists() {
        File the_pass_file = new File(getTargetPassfilePath());
        if (the_pass_file.exists()) {
            return the_pass_file;
        } else {
            return null;
        }
    }

    //clean up pass file when launched.
    public static void cleanupPassFile() {
        File the_pass_file = null;
        if ((the_pass_file = checkIfPassFileExists()) != null) {
            try {
                the_pass_file.delete();
            } catch (Throwable thex) {
                System.err.println(thex.getMessage());
            }
        }
    }

    public static int getVersionAsInt() {
        String versionString = System.getProperty("java.version");
        int firstDot = versionString.indexOf(".");
        String tensString = versionString.substring(0, firstDot);
        int nextDot = versionString.indexOf(".", firstDot + 1);
        if (nextDot < 0) {
            nextDot = versionString.length();
        }
        String onesString = versionString.substring(firstDot + 1, nextDot);
        int version = -1;
        try {
            int tens = new Integer(tensString).intValue();
            int ones = new Integer(onesString).intValue();
            version = (tens * 10) + ones;
        } catch (NumberFormatException nfe) {
        }
        return version;
    }

    public static void createPassfile(String password) {

         try {
           File targetps = new File(getQBrowserTempFileDir());
          targetps.mkdirs();
           File targetpassfile = new File(getTargetPassfilePath());
          targetpassfile.createNewFile();
          PrintWriter pwr = new PrintWriter(new FileWriter(targetpassfile));
          pwr.println("imq.imqcmd.password=" + password);
          pwr.close();
         } catch (Exception e) {
             e.printStackTrace();
         }
    }

    public static String getQBrowserTempFileDir() {
        return getUserHome() + File.separator + ".qbrowserv2" + File.separator;
    }

    public static String getTargetPassfilePath() {
        return getQBrowserTempFileDir() + PASSFILE_NAME;
    }

    public static void copy(File from, File to) throws IOException {
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(from));
            out = new BufferedOutputStream(new FileOutputStream(to));
            byte[] buff = new byte[4096];
            int len = 0;
            while ((len = in.read(buff, 0, buff.length)) >= 0) {
                out.write(buff, 0, len);
            }
        } finally {
            if (in != null) {
                try {
                    in.close();
                    
                } catch (IOException e) {
                    //e.printStackTrace();
                }
                in = null;
            }
            if (out != null) {
                try {
                    out.close();
                    
                } catch (IOException e) {
                    //e.printStackTrace();
                }
                out = null;
            }
        }
    }


    public static String getUserHome() {
        return System.getProperty("user.home");
    }

    //これを呼ぶ前にはリスナーは解除した方がいい。
    public static void ArrayListToJComboBox(ArrayList inputarray, JComboBox targetcombobox) {
          DefaultComboBoxModel model = (DefaultComboBoxModel) targetcombobox.getModel();


          for (int i = 0; i < inputarray.size(); i++) {

              String key = (String) inputarray.get(i);

              if (checkDups(key, targetcombobox)) {
               
                  model.insertElementAt(key, 0);
              }

          }

          if (targetcombobox.getItemCount() > 0)
          targetcombobox.setSelectedIndex(0);

    }

    public static ArrayList jcomboBoxToArrayList(JComboBox inputbox) {
        ArrayList targetArray = new ArrayList();

        try {
        for (int i = 0; i < inputbox.getItemCount(); i++) {
           String key = (String)inputbox.getItemAt(i);
           targetArray.add(key);
        }

        } catch (Throwable thex) {
            System.err.println(thex.getMessage());
        }

        return targetArray;
    }

    public static void saveHistoryToFile(String history_name, ArrayList target_history) {
        //USER.HOMEへ、現在持っている履歴をファイルにしてセーブする。
        //セーブする形式は１レコード=１行。
        String targetFilePath = getQBrowserTempFileDir() + history_name;
        File targethisfile = new File(targetFilePath);
        PrintWriter pwr = null;
        try {

            //なかったら作成する
            if (!targethisfile.exists())
            targethisfile.createNewFile();

            pwr = new PrintWriter(new FileWriter(targethisfile));
            for (int i = target_history.size() - 1; i > -1; i--) {
                String history_entry = (String) target_history.get(i);
                pwr.println(history_entry);
            }

        } catch (Exception ioe) {
            //NOP
            System.err.println(ioe.getMessage());
        } finally {
            if (pwr != null) {
                try {
                    pwr.close();
                } catch (Exception pwre) {
                    //NOP
                }
                pwr = null;
            }
        }

    }

    public static ArrayList getHistoryFromFile(String history_name) {
        //USER.HOMEにセーブされている現在持っている履歴をファイルからArrayListにリストアする

        String targetFilePath = getQBrowserTempFileDir() + history_name;
        File ifile = new File(targetFilePath);
        ArrayList retVal = new ArrayList();
        BufferedReader br = null;
        try {
            if (ifile.exists()) {
                //ファイルがあったときだけ読み出し動作実施
                br = new BufferedReader(new FileReader(ifile));
                String line = null;
                while ((line = br.readLine()) != null) {
                    retVal.add(line);
                }
            }

        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        } finally {
            if (br != null) {
                try {
                br.close();
                
                } catch (Exception fie) {
                    //NOP
                }
                br = null;
            }
        }

        return retVal;
    }

    public static boolean checkDups(String hikaku, JComboBox targetcombobox) {
            DefaultComboBoxModel model = (DefaultComboBoxModel) targetcombobox.getModel();
            for (int i = 0; i < model.getSize(); i++) {
                String key = (String) model.getElementAt(i);
                if (key.trim().equals(hikaku.trim())) {
                    return false;
                }
            }
            return true;
    }

    public static byte[] extractBytes(String filepath) {

            File ftest = new File(filepath);
            if (!ftest.exists()) {
                return null;
            }

            byte[] retv = new byte[0];

            java.io.FileInputStream fi = null;
            ByteArrayOutputStream baos = null;

            try {

            fi = new FileInputStream(filepath);
            baos = new ByteArrayOutputStream();

            byte buf[] = new byte[1024];
            int len = 0;

            int filesizecount = 0;

            while ((len = fi.read(buf)) != -1) {
                filesizecount += buf.length;
                baos.write(buf, 0, len);
            }

            retv = baos.toByteArray();

            } catch (Throwable ie) {
                popupErrorMessageDialog(ie, panel_parent);
                return null;
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

            return retv;
    }
    
    static String displayFloatValueKirisute(float value, int maxsyousutenketa) {
        StringBuilder result = new StringBuilder();

        Float ft = new Float(value);
        char[] chars = ft.toString().toCharArray();
        int kcount = 0;
        boolean is_syosu = false;

        for (int i = 0; chars.length > i; i++) {

            if (chars[i] == '.') {
                is_syosu = true;
            } else {

                if (is_syosu) {
                    kcount++;
                }
            }

            result.append(chars[i]);

            if (kcount == maxsyousutenketa) {
                return result.toString();
            }

        }

        return result.toString();
    }

    public static String bytesLengthAsString(byte[] bytes) {
        String result = "N/A";
        boolean kbflag = false;
        try {
            float lengthr = bytes.length;
            if (lengthr > 1023) {
                lengthr = lengthr / 1024;
                kbflag = true;
            }

            if (lengthr != -1) {
                result = displayFloatValueKirisute(lengthr, 2);
            }

            //.0は切る
            if (result.endsWith(".0")) {
                result = result.substring(0, result.length() - 2);
            }

            if (kbflag) {
                result = result + " KB";
            } else {
                result = result + " byte";
            }

        } catch (Exception e) {
            //NOP
            //e.printStackTrace();
        }




        return result;

    }

    public static void addLabelAndValueComponent(GridBagConstraints valueConstraints, JPanel targetPanel,GridBagLayout valueGbag ,String labelStr, Component value,
        int yAxis) {
        JLabel label = new JLabel(labelStr, Label.RIGHT);
        

        valueConstraints.gridx = 0;
        valueConstraints.gridy = yAxis;
        valueConstraints.weightx = 1.0;
        valueConstraints.weighty = 1.0;
        valueConstraints.anchor = GridBagConstraints.EAST;
        valueGbag.setConstraints(label, valueConstraints);
        targetPanel.add(label);

        valueConstraints.gridx = 1;
        valueConstraints.gridy = yAxis;
        valueConstraints.weightx = 1.0;
        valueConstraints.weighty = 1.0;
        valueConstraints.anchor = GridBagConstraints.WEST;
        valueGbag.setConstraints(value, valueConstraints);
        targetPanel.add(value);

    }

    public static void addRadioButtonAndValueComponent(GridBagConstraints valueConstraints, JPanel targetPanel,GridBagLayout valueGbag ,JRadioButton jrb, Component value,
        int yAxis) {


        valueConstraints.gridx = 0;
        valueConstraints.gridy = yAxis;
        valueConstraints.weightx = 1.0;
        valueConstraints.weighty = 1.0;
        valueConstraints.anchor = GridBagConstraints.EAST;
        valueGbag.setConstraints(jrb, valueConstraints);
        targetPanel.add(jrb);

        valueConstraints.gridx = 1;
        valueConstraints.gridy = yAxis;
        valueConstraints.weightx = 1.0;
        valueConstraints.weighty = 1.0;
        valueConstraints.anchor = GridBagConstraints.WEST;
        valueGbag.setConstraints(value, valueConstraints);
        targetPanel.add(value);

    }

    public static ArrayList<String> getTargetMsgidArrayFromStringFlavor(String orig) {
        ArrayList result = new ArrayList();
        if (orig == null || orig.length() == 0) {
            return result;
        }

        ArrayList lines = parseDelimitedString(orig, "\n");
        for (int i  = 0; i < lines.size(); i++) {
            String line = (String)lines.get(i);
            StringTokenizer st = new StringTokenizer(line);
            //二番目がmsgid
            int count = 0;
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                count++;
                if (count == 2) {
                    result.add(token);
                }
            }
        }

        return result;
    }

    public static ArrayList parseDelimitedString(String orig, String delim) {

        if (orig == null) {
            return null;
        }

        
        ArrayList ar = new ArrayList();

        try {

            int icur = -1;
            String cur = orig;
            while ((icur = cur.indexOf(delim)) != -1) {
                String token = cur.substring(0, icur);
                ar.add(token);
                cur = cur.substring(icur + delim.length());
            }
            ar.add(cur);

        } catch (Throwable thex) {
            //NOP
        }

        return ar;

    }

    public static void addBlankRow(GridBagConstraints valueConstraints, JPanel targetPanel,GridBagLayout valueGbag ,
        int yAxis) {
        JLabel label = new JLabel(" ", Label.RIGHT);


        valueConstraints.gridx = 0;
        valueConstraints.gridy = yAxis;
        valueConstraints.weightx = 1.0;
        valueConstraints.weighty = 1.0;
        valueConstraints.anchor = GridBagConstraints.EAST;
        valueGbag.setConstraints(label, valueConstraints);
        targetPanel.add(label);


    }

    public static void copyMessageHeaders(Message srcmsg, Message destmsg) throws JMSException {

            destmsg.setJMSMessageID(srcmsg.getJMSMessageID());

            destmsg.setJMSDestination(srcmsg.getJMSDestination());
            destmsg.setJMSReplyTo(srcmsg.getJMSReplyTo());

            destmsg.setJMSCorrelationID(srcmsg.getJMSCorrelationID());

            destmsg.setJMSDeliveryMode(srcmsg.getJMSDeliveryMode());

            destmsg.setJMSPriority(srcmsg.getJMSPriority());

            destmsg.setJMSExpiration(srcmsg.getJMSExpiration());

            destmsg.setJMSType(srcmsg.getJMSType());

            destmsg.setJMSRedelivered(srcmsg.getJMSRedelivered());

            destmsg.setJMSTimestamp(srcmsg.getJMSTimestamp());
    }


    public static String getPureDestName(String orig) {
        //ABC : Queue
        //DEF : Topic

        int index = orig.indexOf(" :");

        if (index != -1) {
            return orig.substring(0, index);
        } else {

            index = orig.toLowerCase().indexOf("topic://");
            if (index != -1) {
                return orig.substring(index + 8);
            } else {
                index = orig.toLowerCase().indexOf("queue://");
                if (index != -1) {
                    return orig.substring(index + 8);
                } else {
                    return orig;
                }
            }

        }

    }

    public static void copyMessageHeaders(ArrayList headers, Message destmsg) throws JMSException {

            for (int i = 0; i < headers.size(); i++) {

                try {
                Property prop = (Property)headers.get(i);
                String key = prop.getKey();
                if (key.equals("JMSMessageID")) {
                   destmsg.setJMSMessageID(prop.getProperty_valueASString());
                } else if (key.equals("JMSDestination")) {


                    final String destname = prop.getProperty_valueASString();

                    if (destname != null && destname.length() > 0) {

                    if ((destname.toLowerCase().indexOf(TOPIC_SUFFIX_LOWER) != -1) || destname.trim().toLowerCase().startsWith(TOPIC_PREFIX_LOWER)) {

                        LocalTopic tp = new LocalTopic(QBrowserUtil.getPureDestName(destname));
                        tp.setOriginalDestinationWithSuffix(destname);
                        destmsg.setJMSDestination(tp);


                    } else {
                        LocalQueue lq = new LocalQueue(QBrowserUtil.getPureDestName(destname));
                        lq.setOriginalDestinationWithSuffix(destname);
                        destmsg.setJMSDestination(lq);
                    }

                   }

                } else if (key.equals("JMSReplyTo")) {

                    final String destname = prop.getProperty_valueASString();

                    if (destname != null && destname.length() > 0) {
                    
                    if ((destname.toLowerCase().indexOf(TOPIC_SUFFIX_LOWER) != -1) || destname.trim().toLowerCase().startsWith(TOPIC_PREFIX_LOWER)) {

                        LocalTopic tp = new LocalTopic(QBrowserUtil.getPureDestName(destname));
                        tp.setOriginalDestinationWithSuffix(destname);
                        destmsg.setJMSReplyTo(tp);
                    
                    } else {
                        LocalQueue lq = new LocalQueue(QBrowserUtil.getPureDestName(destname));
                        lq.setOriginalDestinationWithSuffix(destname);
                        destmsg.setJMSReplyTo(lq);
                    }

                    }


                 } else if (key.equals("JMSCorrelationID")) {

                     String colid = prop.getProperty_valueASString();

                     if ((colid != null) && (colid.length() > 0) && (!colid.equals("null"))) {
                       destmsg.setJMSCorrelationID(colid);
                     }

                 } else if (key.equals("JMSDeliverMode")) {

                     destmsg.setJMSDeliveryMode(prop.getProperty_valueASInt());

                 } else if (key.equals("JMSPriority")) {

                     destmsg.setJMSPriority(prop.getProperty_valueASInt());

                 } else if (key.equals("JMSExpiration")) {

                     destmsg.setJMSExpiration(prop.getProperty_valueASLong());

                 } else if (key.equals("JMSType")) {

                     String jms_type = prop.getProperty_valueASString();

                     if ((jms_type != null) && (jms_type.length() > 0) && (!jms_type.equals("null"))) {
                       destmsg.setJMSType(jms_type);
                     }

                 } else if (key.equals("JMSRedelivered")) {

                     destmsg.setJMSRedelivered(prop.getProperty_valueASBoolean());

                 } else if (key.equals("JMSTimestamp")) {
                     destmsg.setJMSTimestamp(prop.getProperty_valueASLong());

                 }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

    }

  public static void copyUserProperties(Message srcmsg, Message destmsg) throws JMSException {

        if ((srcmsg == null) || (destmsg == null)) {
            return;
        }

        for (Enumeration enu = srcmsg.getPropertyNames();
                enu.hasMoreElements();) {

            String name = (enu.nextElement()).toString();
            Object propvalueobj = srcmsg.getObjectProperty(name);
            if (propvalueobj instanceof String) {

                destmsg.setStringProperty(name, (String)propvalueobj);

            } else if (propvalueobj instanceof Integer) {

                destmsg.setIntProperty(name, (Integer)propvalueobj);

            } else if (propvalueobj instanceof Boolean) {

                destmsg.setBooleanProperty(name, (Boolean)propvalueobj);

            } else if (propvalueobj instanceof Byte) {

                destmsg.setByteProperty(name, (Byte)propvalueobj);

            } else if (propvalueobj instanceof Double) {

                destmsg.setDoubleProperty(name, (Double)propvalueobj);

            } else if (propvalueobj instanceof Float) {

                destmsg.setFloatProperty(name, (Float)propvalueobj);

            } else if (propvalueobj instanceof Long) {

                destmsg.setLongProperty(name, (Long)propvalueobj);

            } else if (propvalueobj instanceof Short) {

                destmsg.setShortProperty(name, (Short)propvalueobj);

            } else {

                destmsg.setObjectProperty(name, propvalueobj);
            }
        }

    }

  public static void populateHeadersOfLocalMessageContainer(ArrayList headers, LocalMessageContainer destmsg) throws JMSException {

            ArrayList additional = new ArrayList();

            for (int i = 0; i < headers.size(); i++) {

                try {
                Property prop = (Property)headers.get(i);
                String key = prop.getKey();
                if (key.equals("JMSMessageID")) {
                   destmsg.setVmsgid(prop.getProperty_valueASString());
                } else if (key.equals("JMSDestination")) {


                    final String destname = prop.getProperty_valueASString();

                    if (destname != null && destname.length() > 0) {

                    if ((destname.toLowerCase().indexOf(TOPIC_SUFFIX_LOWER) != -1) || destname.trim().toLowerCase().startsWith(TOPIC_PREFIX_LOWER)) {
                        LocalTopic tp = new LocalTopic(QBrowserUtil.getPureDestName(destname));
                        tp.setOriginalDestinationWithSuffix(destname);
                        destmsg.setVdest(tp);


                    } else {
                        LocalQueue lq = new LocalQueue(QBrowserUtil.getPureDestName(destname));
                        lq.setOriginalDestinationWithSuffix(destname);
                        destmsg.setVdest(lq);
                    }

                   }

                } else if (key.equals("JMSReplyTo")) {

                    final String destname = prop.getProperty_valueASString();

                    if (destname != null && destname.length() > 0) {

                    if ((destname.toLowerCase().indexOf(TOPIC_SUFFIX_LOWER) != -1) || destname.trim().toLowerCase().startsWith(TOPIC_PREFIX_LOWER)) {
                        LocalTopic tp = new LocalTopic(QBrowserUtil.getPureDestName(destname));
                        tp.setOriginalDestinationWithSuffix(destname);
                        destmsg.setVreplyto(tp);

                    } else {
                        LocalQueue lq = new LocalQueue(QBrowserUtil.getPureDestName(destname));
                        lq.setOriginalDestinationWithSuffix(destname);
                        destmsg.setVreplyto(lq);
                    }

                    }


                 } else if (key.equals("JMSCorrelationID")) {

                     String colid = prop.getProperty_valueASString();

                     if ((colid != null) && (colid.length() > 0) && (!colid.equals("null"))) {
                       destmsg.setVcorrelationid(colid);
                     }

                 } else if (key.equals("JMSDeliverMode")) {

                     destmsg.setVdeliverymode(prop.getProperty_valueASInt());

                 } else if (key.equals("JMSPriority")) {

                     destmsg.setVpriority(prop.getProperty_valueASInt());

                 } else if (key.equals("JMSExpiration")) {

                     destmsg.setVexpiration(prop.getProperty_valueASLong());

                 } else if (key.equals("JMSType")) {

                     String jms_type = prop.getProperty_valueASString();

                     if ((jms_type != null) && (jms_type.length() > 0) && (!jms_type.equals("null"))) {
                       destmsg.setVjms_type(jms_type);
                     }

                 } else if (key.equals("JMSRedelivered")) {

                     destmsg.setVredelivered(prop.getProperty_valueASBoolean());

                 } else if (key.equals("JMSTimestamp")) {
                     destmsg.setVtimestamp(prop.getProperty_valueASLong());

                 } else {
                     additional.add(prop);
                 }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            destmsg.setAdditionalHeaders(additional);

    }

  public static void populateHeadersOfLocalMessageContainer(Message srcmsg, LocalMessageContainer destmsg) throws JMSException {

            destmsg.setMessage(srcmsg);
            destmsg.setVmsgid(srcmsg.getJMSMessageID());

            destmsg.setVdest(srcmsg.getJMSDestination());
            destmsg.setVreplyto(srcmsg.getJMSReplyTo());

            destmsg.setVcorrelationid(srcmsg.getJMSCorrelationID());

            destmsg.setVdeliverymode(srcmsg.getJMSDeliveryMode());

            destmsg.setVpriority(srcmsg.getJMSPriority());

            destmsg.setVexpiration(srcmsg.getJMSExpiration());

            destmsg.setVjms_type(srcmsg.getJMSType());

            destmsg.setVredelivered(srcmsg.getJMSRedelivered());

            destmsg.setVtimestamp(srcmsg.getJMSTimestamp());
    }

  public static void populateHeadersOfLocalMessageContainerWithConvertLocalDestObject(Message srcmsg, LocalMessageContainer destmsg) throws JMSException {

            destmsg.setMessage(srcmsg);
            destmsg.setVmsgid(srcmsg.getJMSMessageID());

            //ローカルオブジェクトに変更する（情報から再構築）
            destmsg.setVdest(srcmsg.getJMSDestination());
            destmsg.setVreplyto(srcmsg.getJMSReplyTo());

            destmsg.setVcorrelationid(srcmsg.getJMSCorrelationID());

            destmsg.setVdeliverymode(srcmsg.getJMSDeliveryMode());

            destmsg.setVpriority(srcmsg.getJMSPriority());

            destmsg.setVexpiration(srcmsg.getJMSExpiration());

            destmsg.setVjms_type(srcmsg.getJMSType());

            destmsg.setVredelivered(srcmsg.getJMSRedelivered());

            destmsg.setVtimestamp(srcmsg.getJMSTimestamp());
    }

  public static void populateHeadersOfLocalMessageContainer(LocalMessageContainer srcmsg, LocalMessageContainer destmsg) throws JMSException {

            destmsg.setMessage(srcmsg.getMessage());
            destmsg.setMessage_type(srcmsg.getMessage_type());
            destmsg.setBody_size(srcmsg.getBody_size());
            destmsg.setReal_file_path(srcmsg.getReal_file_path());

            destmsg.setVmsgid(srcmsg.getVmsgid());

            destmsg.setVdest(srcmsg.getVdest());
            destmsg.setVreplyto(srcmsg.getVreplyto());

            destmsg.setVcorrelationid(srcmsg.getVcorrelationid());

            destmsg.setVdeliverymode(srcmsg.getVdeliverymode());

            destmsg.setVpriority(srcmsg.getVpriority());

            destmsg.setVexpiration(srcmsg.getVexpiration());

            destmsg.setVjms_type(srcmsg.getVjms_type());

            destmsg.setVredelivered(srcmsg.isVredelivered());

            destmsg.setVtimestamp(srcmsg.getVtimestamp());

            destmsg.setAdditionalHeaders(srcmsg.getAdditionalHeaders());
    }

  public static void populateHeadersOfLocalMessageContainer2(LocalMessageContainer srcmsg, LocalMessageContainer destmsg) throws JMSException {

            destmsg.setMessage(srcmsg.getMessage());
            destmsg.setMessage_type(srcmsg.getMessage_type());
            //destmsg.setBody_size(srcmsg.getBody_size());
            destmsg.setReal_file_path(srcmsg.getReal_file_path());

            destmsg.setVmsgid(srcmsg.getVmsgid());

            destmsg.setVdest(srcmsg.getVdest());
            destmsg.setVreplyto(srcmsg.getVreplyto());

            destmsg.setVcorrelationid(srcmsg.getVcorrelationid());

            destmsg.setVdeliverymode(srcmsg.getVdeliverymode());

            destmsg.setVpriority(srcmsg.getVpriority());

            destmsg.setVexpiration(srcmsg.getVexpiration());

            destmsg.setVjms_type(srcmsg.getVjms_type());

            destmsg.setVredelivered(srcmsg.isVredelivered());

            destmsg.setVtimestamp(srcmsg.getVtimestamp());

            destmsg.setAdditionalHeaders(srcmsg.getAdditionalHeaders());
    }

  public static String getMessageTypeFromRealFilePath(String filepath) {
      if (filepath == null) return null;

              if (filepath.indexOf("_" + QBrowserV2.TEXTMESSAGE) != -1) {

                 return QBrowserV2.TEXTMESSAGE;

              } else
              if (filepath.indexOf("_" + QBrowserV2.BYTESMESSAGE) != -1) {

                 return QBrowserV2.BYTESMESSAGE;

              } else
              if (filepath.indexOf("_" + QBrowserV2.MAPMESSAGE) != -1) {

                  return QBrowserV2.MAPMESSAGE;

              } else
              if (filepath.indexOf("_" + QBrowserV2.STREAMMESSAGE) != -1) {

                  return QBrowserV2.STREAMMESSAGE;

              } else
              if (filepath.indexOf("_" + QBrowserV2.OBJECTMESSAGE) != -1) {

                  return  QBrowserV2.OBJECTMESSAGE;

              } else if (filepath.indexOf("_" + QBrowserV2.MESSAGE) != -1) {

                  return QBrowserV2.MESSAGE;

              }

              return null;
      
  }

  public static void populateHeadersOfLocalMessageContainer(MessageContainer srcmsg, LocalMessageContainer destmsg) throws JMSException {

            destmsg.setMessage(srcmsg.getMessage());
            destmsg.setVmsgid(srcmsg.getVmsgid());

            destmsg.setVdest(srcmsg.getVdest());
            destmsg.setVreplyto(srcmsg.getVreplyto());

            destmsg.setVcorrelationid(srcmsg.getVcorrelationid());

            destmsg.setVdeliverymode(srcmsg.getVdeliverymode());

            destmsg.setVpriority(srcmsg.getVpriority());

            destmsg.setVexpiration(srcmsg.getVexpiration());

            destmsg.setVjms_type(srcmsg.getVjms_type());

            destmsg.setVredelivered(srcmsg.isVredelivered());

            destmsg.setVtimestamp(srcmsg.getVtimestamp());

            destmsg.setAdditionalHeaders(srcmsg.getAdditionalHeaders());
    }

  public static void populateHeadersOfLocalMessageContainerWithoutMessage(LocalMessageContainer srcmsg, LocalMessageContainer destmsg) throws JMSException {

            destmsg.setReal_file_path(srcmsg.getReal_file_path());
            destmsg.setVmsgid(srcmsg.getVmsgid());

            destmsg.setVdest(srcmsg.getVdest());
            destmsg.setVreplyto(srcmsg.getVreplyto());

            destmsg.setVcorrelationid(srcmsg.getVcorrelationid());

            destmsg.setVdeliverymode(srcmsg.getVdeliverymode());

            destmsg.setVpriority(srcmsg.getVpriority());

            destmsg.setVexpiration(srcmsg.getVexpiration());

            destmsg.setVjms_type(srcmsg.getVjms_type());

            destmsg.setVredelivered(srcmsg.isVredelivered());

            destmsg.setVtimestamp(srcmsg.getVtimestamp());

            destmsg.setAdditionalHeaders(srcmsg.getAdditionalHeaders());
    }

   public static void copyUserProperties(ArrayList userprops ,Message message) {
        for (int i = 0 ; i < userprops.size(); i++) {

            try {
                Property userprop = (Property) userprops.get(i);
                String key = userprop.getKey();
                switch (userprop.validated_type) {
                    case Property.INVALID_TYPE_INT:
                       //VALIDではない、セットスキップ
                       break;

                    case Property.STRING_TYPE_INT:
                       message.setStringProperty(key, userprop.getProperty_valueASString());
                       break;

                    case Property.BOOLEAN_TYPE_INT:
                       message.setBooleanProperty(key, userprop.getProperty_valueASBoolean());
                       break;

                    case Property.INT_TYPE_INT:
                       message.setIntProperty(key, userprop.getProperty_valueASInt());
                       break;

                    case Property.BYTE_TYPE_INT:
                       message.setByteProperty(key, userprop.getProperty_valueASByte());
                       break;

                    case Property.DOUBLE_TYPE_INT:
                       message.setDoubleProperty(key, userprop.getProperty_valueASDouble());
                       break;

                    case Property.FLOAT_TYPE_INT:
                       message.setFloatProperty(key, userprop.getProperty_valueASFloat());
                       break;

                    case Property.LONG_TYPE_INT:
                       message.setLongProperty(key, userprop.getProperty_valueASLong());
                       break;

                    case Property.SHORT_TYPE_INT:
                       message.setShortProperty(key, userprop.getProperty_valueASShort());
                       break;

                    default :
                       break;

                }

            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

      public static String toHexDump(byte[] buf, int length) {

        // Buffer must be an even length
        if (buf.length % 2 != 0) {
            //throw new IllegalArgumentException();
            byte[] newarray = new byte[buf.length + 1];
            System.arraycopy(buf, 0, newarray, 0, buf.length);
            buf = newarray;
            
        }

        int value;
        StringBuffer sb = new StringBuffer(buf.length * 2);

        /* Assume buf is in network byte order (most significant byte
         * is buf[0]). Convert two byte pairs to a short, then
         * display as a hex string.
         */
        int n = 0;
        while (n < buf.length && n < length) {
            value = buf[n + 1] & 0xFF;		// Lower byte
            value |= (buf[n] << 8) & 0xFF00;	// Upper byte
            String s = Integer.toHexString(value);
            // Left bad with 0's
            sb.append(pad[4 - s.length()]);
            sb.append(s);
            n += 2;

            if (n % 16 == 0) {
                sb.append("\n");
            } else {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

        /**
         * Pad a string to the specified width, right justified.
         * If the string is longer than the width you get back the
         * original string.
         */
        static String pad(String s, int width) {

            // Very inefficient, but we don't care
            StringBuffer sb = new StringBuffer();
            int padding = width - s.length();

            if (padding <= 0) {
                return s;
            }

            while (padding > 0) {
                sb.append(" ");
                padding--;
            }
            sb.append(s);
            return sb.toString();
        }

    public static void popupErrorMessageDialog(Throwable e, JComponent parent) {

        if (errDialog != null && errDialog.isShowing()) {
            errDialog.dispose();
        }
        errDialog = new JDialog();
        errDialog.setLocation(120, 120);
        TextArea ta = new TextArea("", 10, 50, TextArea.SCROLLBARS_NONE);
        ta.setEditable(true);
        ta.append(e.getMessage());
        ta.append("\n");
        ta.append(e.toString());

        JPanel mainmsg = new JPanel();
        mainmsg.add(ta);

        errDialog.getContentPane().setLayout(new BorderLayout());

        JButton confirmbutton = new JButton("OK");
        confirmbutton.addActionListener(new ErrorConfirmedListener());

        JPanel buttonpanel = new JPanel();
        buttonpanel.setLayout(new BorderLayout());
        buttonpanel.add(BorderLayout.EAST, confirmbutton);

        errDialog.getContentPane().add(BorderLayout.SOUTH, buttonpanel);
        errDialog.getContentPane().add(BorderLayout.NORTH, mainmsg);
        errDialog.pack();
        errDialog.setLocationRelativeTo(parent);
        errDialog.setVisible(true);
    }

     public static String eliminateEndFileSeparator(String orig) {
         if (orig == null) return null;
         if (orig.endsWith(File.separator)) {
             return orig.substring(0, orig.length() - 1);
         } else {
             return orig;
         }
     }



     public static String eliminateDameMoji(String orig) {
        if (orig == null) return null;

        StringBuilder sb = new StringBuilder();

        char[] chars = orig.toCharArray();
        for (int i = 0; i < chars.length ; i++) {
            if ((chars[i] == '\\') ||
                (chars[i] == '/')  ||
                (chars[i] == ':')  ||
                (chars[i] == '*')  ||
                (chars[i] == '?')  ||
                (chars[i] == '\"') ||
                (chars[i] == '<')  ||
                (chars[i] == '>')  ||
                (chars[i] == '|')) {

                //iranai

            } else {
                sb.append(chars[i]);
            }

        }

        return sb.toString();
    }

    static class ErrorConfirmedListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            errDialog.setVisible(false);
        }
    }

    public static long messageBodySizeOfLong(Message m) throws Exception {

        if (m instanceof TextMessage) {
            long result = 0;

            try {
                TextMessage temp = (TextMessage) m;
                result = temp.getText().length();

                if ((result == 0) && (temp.getText().length() != 0)) {
                    result = 1;
                }
            } catch (Exception e) {
                //NOP
                //e.printStackTrace();
            }
            return result;
        } else if (m instanceof BytesMessage) {
            long result = 0;
            try {
                BytesMessage temp = (BytesMessage) m;
                //temp.reset();
                result = temp.getBodyLength();

                if ((result == 0) && (temp.getBodyLength() != 0)) {
                    result = 1;
                }
            } catch (Exception e) {
                //NOP
                //e.printStackTrace();
            }
            return result;
        } else if (m instanceof MapMessage) {
            return -1;
        } else if (m instanceof ObjectMessage) {
            return -1;
        } else if (m instanceof StreamMessage) {
            return -1;
        } else if (m instanceof Message) {
            return -1;
        } else {
            return -1;
        }
    }


    public static String messageBodyLengthAsString(long lengthr) {
        String result = "N/A";
        boolean kbflag = false;
        try {

            if (lengthr > 1023) {
                lengthr = lengthr / 1024;
                kbflag = true;
            }

            if (lengthr != -1) {
                result = displayFloatValueKirisute(lengthr, 2);
            }

            //.0は切る
            if (result.endsWith(".0")) {
                result = result.substring(0, result.length() - 2);
            }

            if (kbflag) {
                result = result + " KB";
            } else {
                result = result + " byte";
            }

        } catch (Exception e) {
            //NOP
            //e.printStackTrace();
        }

        return result;

    }

   public static boolean isTopic(String title) {
        if ((title == null) || (title.length() == 0)) {
            return false;
        }

        if ((title.toLowerCase().indexOf(TOPIC_SUFFIX_LOWER) != -1) || title.trim().toLowerCase().startsWith(TOPIC_PREFIX_LOWER)) {
            return true;
        } else {
            return false;
        }
    }

   public static boolean isQueue(String title) {
        if ((title == null) || (title.length() == 0)) {
            return false;
        }

        if ((title.toLowerCase().indexOf(QUEUE_SUFFIX_LOWER) != -1) || title.trim().toLowerCase().startsWith(QUEUE_PREFIX_LOWER)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isLocalStore(String title) {
        if ((title == null) || (title.length() == 0)) {
            return false;
        }

        if (title.toLowerCase().indexOf(LOCALSTORE_SUFFIX_LOWER) != -1) {
            return true;
        } else {
            return false;
        }
    }

}
