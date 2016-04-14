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

package com.qbrowser.property;

import com.jidesoft.swing.FolderChooser;
import com.qbrowser.QBrowserV2;
import com.qbrowser.container.MessageContainer;
import com.qbrowser.icons.QBrowserIconsFactory;
import com.qbrowser.util.QBrowserUtil;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import javax.jms.Queue;
import java.util.ResourceBundle;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.StreamMessage;
import javax.jms.Topic;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author takemura
 */
public class StreamMessageAllPropertiesTable extends  DefaultTableModel {

    //public static ResourceBundle resources = ResourceBundle.getBundle("com.qbrowser.resourcebase");
    static ResourceBundle resources = QBrowserV2.resources;
    JDialog downloadDialog = null;
    JPanel downloadmsgPanel = null;
    JPanel temppanel = null;
    JTextField download_file_path = null;
    static FolderChooser _folderChooser;
    JDialog downloadDialog2 = null;
    JPanel downloadmsgPanel2 = null;
    JPanel temppanel2 = null;
    JTextField download_file_path2 = null;
    static FolderChooser _folderChooser2;
    static List<String> _recentList = new ArrayList<String>();
    static File _currentFolder = null;
    JDialog msgDialog = null;
    JButton msgconfirmbutton = null;

    JPanel oya = null;

    public void setOya(JPanel value) {
        oya = value;
    }

    private javax.jms.StreamMessage innermessage = null;

        final String[] columnNames =
                {resources.getString("qkey.proptable.header.smkey"),
            resources.getString("qkey.proptable.header.prop_type"),
            resources.getString("qkey.proptable.header.prop_value"),
            resources.getString("qkey.proptable.header.download")
            };

        LinkedList list = null;

        ArrayList type_selection = null;

        public StreamMessageAllPropertiesTable() {
            init();
            add_one_row(new StreamMessageAllProperties());
            
        }

        public StreamMessageAllPropertiesTable(int number_of_initial_rows) {
            init();
            for (int i = 0 ; i < number_of_initial_rows; i++) {
               add_one_row(new StreamMessageAllProperties());
            }

        }

        public StreamMessage getInnerMessage() {
            return innermessage;
        }

    @Override
        public int getRowCount() {
            if (list == null) {
                return 0;
            } else {
                return list.size();
            }
        }

    @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        
        @Override
        public Class<?> getColumnClass(int column) {
            switch (column) {
                case 0:
                    return Integer.class;
                case 1:
                    return String.class;
                case 2:
                    return Object.class;
                case 3:
                    return Object.class;

            }
            return Object.class;
        }
        

                @Override
                public boolean isCellEditable(int row, int column) {

                    if (column == 0) {
                        return false;
                    }

                    Object robj = getValueAt(row, column);

                    if (robj instanceof JButton) {
                        return true;
                    } else if (column == 2) {

                        //Stringは基本的にtrueだが、n/aはfalse
                        if (robj instanceof JTextField) {
                            return true;
                        } else
                        if (robj instanceof String) {
                             if (((String)robj).startsWith(resources.getString("qkey.msg.msg186"))) {
                                 return false;
                           } else {
                                 return true;
                             }
                        } else {
                            //String以外はfalse
                            return false;
                        }
                    } else {
                        return false;
                    }
                }

                public Class<?> getCellClassAt(int row, int column) {
                    return getColumnClass(column);
                }

        @Override
        public void setValueAt(Object value, int row, int column) {
            if (list == null) {
                return;
            }

            if (list.size() < row) {
                return;
            }

            StreamMessageAllProperties p = (StreamMessageAllProperties) list.get(row);
            if (p == null) {
                return;
            }

            try {
                switch (column) {
                    case 0:
                        // Property KEY
                        if (value instanceof Integer) {
                           p.setSmKey(((Integer)value).intValue());
                        }

                        if (value instanceof String) {
                           try {
                            p.setSmKey(Integer.parseInt((String)value));
                           } catch (NumberFormatException nfe) {
                               //NOT SET
                           }
                        }
                        return;
                    case 1:
                        // Property TYPE
                        if (value instanceof String) {
                          p.setProperty_type((String)value);
                        }
                        return;

                    case 2:
                        // Property value
                          p.setProperty_value(value);

                    case 3:
                        // Property value
                          if (value instanceof JButton)
                          p.setDownload_button(value);

                        return;


                    default:
                        return;
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
                return;
            }



        }

        @Override
        public Object getValueAt(int row, int column) {
            if (list == null) {
                return null;
            }

            StreamMessageAllProperties p = (StreamMessageAllProperties) list.get(row);

            if (p == null) {
                return "";
            }

            try {
                switch (column) {
                    case 0:
                        // Property KEY
                        return p.getSmKey();
                    case 1:
                        // Property TYPE
                        return p.getProperty_type();

                    case 2:
                        // Property Value
                        if (p.getProperty_value() instanceof JTextField) {
                          return ((JTextField)p.getProperty_value());
                        } else if (p.getProperty_value() instanceof byte[]) {
                          String bytes_exp = QBrowserUtil.bytesLengthAsString((byte[])p.getProperty_value());
                          String data_length_exp = resources.getString("qkey.msg.msg186") + " : " + bytes_exp;
                          return data_length_exp;
                        } else {
                          return p.getProperty_value();
                        }

                    case 3:
                        // Property TYPE
                        Object no4 = p.getDownload_button();
                        if (no4 != null) {
                            //System.out.println(no4.getClass().getName());
                            if (no4 instanceof JButton) {
                                return no4;
                            } else {
                                return "";
                            }
                        } else {
                            return "";
                        }

                    default:
                        return "Bad column value: " + column;
                }
            } catch (Exception e) {
                return ("Error: " + e);
            }
        }

    private String optimizeString(String source) {

        if (source.length() > 1000) {
            return source.substring(0, 1000) + "...." +  System.getProperty("line.separator") + resources.getString("qkey.msg.msg211");
        } else {
            return source;
        }

    }

    public int load(MessageContainer mc) {

        if (mc == null)
            return 0;

        Message msgvalue = mc.getMessage();

        if (msgvalue == null)
            return 0;

        StreamMessage msg = null;

        if (msgvalue instanceof StreamMessage) {
            msg = (StreamMessage)msgvalue;
        } else {
            return 0;
        }

        list = new LinkedList();

        try {

              msg.reset();

                Object obj = null;
                int rowcount = 0;

                while ((obj = ((StreamMessage)msg).readObject()) != null) {
                    
                rowcount++;

                String name = String.valueOf(rowcount);

                if (obj instanceof String) {
                      StreamMessageAllProperties ap = new StreamMessageAllProperties();
                      ap.setSmKey(rowcount);
                      ap.setKey(name);
                      ap.setProperty_type(Property.STRING_TYPE);
                      //ストリングタイプの場合はJTextFieldでラップする。
                      //カーソルが入った時点で大きなエディタを表示する
                      JTextField jtf1 = new JTextField();

                      //最大出力1000文字まで：それ以降はファイルにダウンロードして。
                      //改行はあらためて表示。
                      jtf1.setText(optimizeString((String)obj));
                      jtf1.setToolTipText(resources.getString("qkey.msg.msg231"));

                      //jtf1.setText((String)obj);
                      ap.setProperty_value(jtf1);
                      //あとで、アクションリスナを追加する（親のサブクラスを使いたいから）

                      JButton db = new JButton("download");
                      db.addActionListener(new DownloadButtonListener2(name, msg));
                      ap.setDownload_button(db);
                      
                      list.add(ap);
                } else if (obj instanceof Integer) {
                      StreamMessageAllProperties ap = new StreamMessageAllProperties();
                      ap.setSmKey(rowcount);
                      ap.setKey(name);
                      ap.setProperty_type(Property.INT_TYPE);
                      ap.setProperty_value(obj);
                      list.add(ap);
                } else if (obj instanceof Boolean) {
                      StreamMessageAllProperties ap = new StreamMessageAllProperties();
                      ap.setSmKey(rowcount);
                      ap.setKey(name);
                      ap.setProperty_type(Property.BOOLEAN_TYPE);
                      ap.setProperty_value(obj);
                      list.add(ap);
                } else if (obj instanceof Byte) {
                      StreamMessageAllProperties ap = new StreamMessageAllProperties();
                      ap.setSmKey(rowcount);
                      ap.setKey(name);
                      ap.setProperty_type(Property.BYTE_TYPE);
                      ap.setProperty_value(obj);
                      list.add(ap);                
                } else if (obj instanceof Character) {
                      StreamMessageAllProperties ap = new StreamMessageAllProperties();
                      ap.setSmKey(rowcount);
                      ap.setKey(name);
                      ap.setProperty_type(Property.CHARACTER_TYPE);
                      ap.setProperty_value(obj);
                      list.add(ap);

                } else if (obj instanceof Double) {
                      StreamMessageAllProperties ap = new StreamMessageAllProperties();
                      ap.setSmKey(rowcount);
                      ap.setKey(name);
                      ap.setProperty_type(Property.DOUBLE_TYPE);
                      ap.setProperty_value(obj);
                      list.add(ap);
                } else if (obj instanceof Float) {
                      StreamMessageAllProperties ap = new StreamMessageAllProperties();
                      ap.setSmKey(rowcount);
                      ap.setKey(name);
                      ap.setProperty_type(Property.FLOAT_TYPE);
                      ap.setProperty_value(obj);
                      list.add(ap);
                } else if (obj instanceof Long) {
                      StreamMessageAllProperties ap = new StreamMessageAllProperties();
                      ap.setSmKey(rowcount);
                      ap.setKey(name);
                      ap.setProperty_type(Property.LONG_TYPE);
                      ap.setProperty_value(obj);
                      list.add(ap);
                } else if (obj instanceof Short) {
                      StreamMessageAllProperties ap = new StreamMessageAllProperties();
                      ap.setSmKey(rowcount);
                      ap.setKey(name);
                      ap.setProperty_type(Property.SHORT_TYPE);
                      ap.setProperty_value(obj);
                      list.add(ap);                     
                } else if (obj instanceof byte[]) {
                      StreamMessageAllProperties ap = new StreamMessageAllProperties();
                      ap.setSmKey(rowcount);
                      ap.setKey(name);
                      ap.setProperty_type(Property.BYTES_TYPE);
                      JButton db = new JButton("download");
                      db.addActionListener(new DownloadButtonListener(name, msg));
                      ap.setProperty_value(obj);
                      ap.setDownload_button(db);
                      list.add(ap);
                }

            }

        } catch (JMSException ex) {
            //ex.printStackTrace();
        } catch (Throwable thex) {

        }

            fireTableDataChanged();

            this.innermessage = msg;

        return list.size();
    }

        /**
         * Load and enumeration of messages into the table
         */
        public int load(Enumeration e) {
            if (e == null) {
                return 0;
            }

            list = new LinkedList();

            while (e.hasMoreElements()) {
                list.add(e.nextElement());
            }

            fireTableDataChanged();

            return list.size();
        }

        void init() {

            list = new LinkedList();
            
        }

        public void add_one_row(StreamMessageAllProperties value) {
            list.add(value);
            fireTableDataChanged();
        }

        public void add_one_empty_row() {
            list.add(new StreamMessageAllProperties());
            fireTableDataChanged();
        }

        public void deletePropertyAtRow(int row) {
            if (list == null) {
                return;
            }

            list.remove(row);
            fireTableDataChanged();

        }

        public StreamMessageAllProperties getPropertyAtRow(int row) {
            if (list == null) {
                return null;
            }
            return ((StreamMessageAllProperties) list.get(row));
        }

    public void cleanupAllPropertyDownloadPanels() {
        //System.out.println("cleanupAllPropertyDownloadPanels called.");
        if (downloadDialog != null) {
            downloadDialog.dispose();
            downloadDialog = null;
        }

        if (downloadDialog2 != null) {
            downloadDialog2.dispose();
            downloadDialog2 = null;
        }
    }

    public void showDownloadWindowForBytesProperty(String key, StreamMessage msg, String title, String desc) {


        if (downloadDialog != null && downloadDialog.isShowing()) {
            downloadDialog.dispose();
        }
            downloadDialog = new JDialog();
            downloadDialog.setIconImage(QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Download).getImage());


            downloadDialog.addWindowListener(new WindowAdapter() {

                @Override
                public void windowClosing(WindowEvent e) {
                }
            });

            downloadDialog.getContentPane().setLayout(new BorderLayout());

            downloadmsgPanel = new JPanel();
            downloadmsgPanel.setLayout(new BorderLayout());

            JPanel downloadmsg = new JPanel();
            downloadDialog.setSize(200, 300);
            downloadDialog.setTitle(title);

            download_file_path = new JTextField(30);

        if (_folderChooser == null) {
            _folderChooser = new FolderChooser();
            _folderChooser.setAvailableButtons(_folderChooser.getAvailableButtons() & ~FolderChooser.BUTTON_DELETE);
            _folderChooser.setNavigationFieldVisible(true);
        }
            JButton file_choose_button = (JButton)createBrowseButton();

            JLabel downloadlabel = new JLabel(desc);
            JPanel expl = new JPanel();
            expl.setLayout(new BorderLayout());

            JPanel tqboxpanel = new JPanel();
            tqboxpanel.setLayout(new BorderLayout());
            expl.add(BorderLayout.NORTH, tqboxpanel);
            expl.add(BorderLayout.CENTER, downloadlabel);


            downloadmsg.add(download_file_path);
            downloadmsg.add(file_choose_button);
            downloadmsgPanel.add(BorderLayout.NORTH, expl);
            downloadmsgPanel.add(BorderLayout.CENTER, downloadmsg);
            JButton okbutton1 = new JButton("              OK              ");
            okbutton1.addActionListener(new DownloadOKListener(key, msg));
            JButton cancelbutton = new JButton("         " + resources.getString("qkey.msg.msg129") + "             ");
            cancelbutton.addActionListener(new DownloadCancelListener());

            JPanel pbuttonpanel = new JPanel();
            pbuttonpanel.setLayout(new BorderLayout());
            pbuttonpanel.add(BorderLayout.WEST, okbutton1);
            pbuttonpanel.add(BorderLayout.CENTER, cancelbutton);

            temppanel = new JPanel();
            temppanel.setLayout(new BorderLayout());

            temppanel.add(BorderLayout.SOUTH, pbuttonpanel);

            downloadmsgPanel.add(BorderLayout.SOUTH, temppanel);


            downloadDialog.getContentPane().add(BorderLayout.NORTH, downloadmsgPanel);
            downloadDialog.pack();

        

        downloadDialog.setLocationRelativeTo(oya);
        downloadDialog.setVisible(true);



    }

    public void showDownloadWindowForStringProperty(String key, StreamMessage msg, String title, String desc) {

        if (downloadDialog2 != null && downloadDialog2.isShowing()) {
            downloadDialog2.dispose();
        }

            downloadDialog2 = new JDialog();
            downloadDialog2.setIconImage(QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Download).getImage());


            downloadDialog2.addWindowListener(new WindowAdapter() {

                @Override
                public void windowClosing(WindowEvent e) {
                }
            });

            downloadDialog2.getContentPane().setLayout(new BorderLayout());

            downloadmsgPanel2 = new JPanel();
            downloadmsgPanel2.setLayout(new BorderLayout());

            JPanel downloadmsg = new JPanel();
            downloadDialog2.setSize(200, 300);
            downloadDialog2.setTitle(title);

            download_file_path2 = new JTextField(30);

        if (_folderChooser2 == null) {
            _folderChooser2 = new FolderChooser();
            _folderChooser2.setAvailableButtons(_folderChooser2.getAvailableButtons() & ~FolderChooser.BUTTON_DELETE);
            _folderChooser2.setNavigationFieldVisible(true);
        }
            JButton file_choose_button = (JButton)createBrowseButton2();

            JLabel downloadlabel = new JLabel(desc);
            JPanel expl = new JPanel();
            expl.setLayout(new BorderLayout());

            JPanel tqboxpanel = new JPanel();
            tqboxpanel.setLayout(new BorderLayout());
            expl.add(BorderLayout.NORTH, tqboxpanel);
            expl.add(BorderLayout.CENTER, downloadlabel);


            downloadmsg.add(download_file_path2);
            downloadmsg.add(file_choose_button);
            downloadmsgPanel2.add(BorderLayout.NORTH, expl);
            downloadmsgPanel2.add(BorderLayout.CENTER, downloadmsg);
            JButton okbutton1 = new JButton("              OK               ");
            okbutton1.addActionListener(new DownloadOKListener2(key,msg));

            JButton cancelbutton = new JButton("         " + resources.getString("qkey.msg.msg129") + "             ");
            cancelbutton.addActionListener(new DownloadCancelListener());

            JPanel pbuttonpanel = new JPanel();
            pbuttonpanel.setLayout(new BorderLayout());
            pbuttonpanel.add(BorderLayout.WEST, okbutton1);
            pbuttonpanel.add(BorderLayout.CENTER, cancelbutton);

            temppanel2 = new JPanel();
            temppanel2.setLayout(new BorderLayout());

            temppanel2.add(BorderLayout.SOUTH, pbuttonpanel);

            downloadmsgPanel2.add(BorderLayout.SOUTH, temppanel2);


            downloadDialog2.getContentPane().add(BorderLayout.NORTH, downloadmsgPanel2);
            downloadDialog2.pack();

        

        downloadDialog2.setLocationRelativeTo(oya);
        downloadDialog2.setVisible(true);



    }

    class DownloadButtonListener implements ActionListener {

        String download_target_key = null;
        StreamMessage download_target_message = null;

        public DownloadButtonListener(String key, StreamMessage msg) {
            download_target_key = key;
            download_target_message = msg;
        }

        public void actionPerformed(ActionEvent e) {
            
                showDownloadWindowForBytesProperty(download_target_key, download_target_message, resources.getString("qkey.msg.msg191"), resources.getString("qkey.msg.msg192"));
            
        }
    }

    //ForStringProperty
    class DownloadButtonListener2 implements ActionListener {

        String download_target_key = null;
        StreamMessage download_target_message = null;

        public DownloadButtonListener2(String key, StreamMessage msg) {
            download_target_key = key;
            download_target_message = msg;
        }

        public void actionPerformed(ActionEvent e) {
                showDownloadWindowForStringProperty(download_target_key, download_target_message , resources.getString("qkey.msg.msg228"), resources.getString("qkey.msg.msg192"));
        }
    }

    class DownloadCancelListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            downloadDialog.setVisible(false);

        }
    }

    class DownloadOKListener implements ActionListener {

        String key;
        StreamMessage msg;

        public DownloadOKListener(String key_value, StreamMessage msg_value) {

            key = key_value;
            msg = msg_value;

        }

        public void actionPerformed(ActionEvent e) {

            ByteArrayInputStream bis = null;
            java.io.FileOutputStream fo = null;

            //指定されたフォルダ最終チェック
            File inputf = new File(download_file_path.getText());
            try {

                JLabel errlabel = new JLabel(resources.getString("qkey.msg.msg193"));
                if (!inputf.exists()) {
                    errlabel.setForeground(Color.RED);
                    temppanel.add(BorderLayout.CENTER, errlabel);
                    downloadmsgPanel.updateUI();
                    downloadDialog.pack();

                } else {
                    temppanel.add(BorderLayout.CENTER, new JLabel(""));
                    temppanel.updateUI();
                    downloadmsgPanel.updateUI();
                    downloadDialog.pack();

                    downloadDialog.setVisible(false);

                    //ダウンロード処理実施！
                //ダウンロード先ファイル名作成
                long timestamp = msg.getJMSTimestamp();
                Destination dest = msg.getJMSDestination();

                //ターゲットまで読み飛ばし
                byte[] targetbin = null;
        try {

              msg.reset();

                Object obj = null;
                int rowcount = 0;

                while ((obj = ((StreamMessage)msg).readObject()) != null) {

                rowcount++;

                String name = String.valueOf(rowcount);

                if ((key.equals(name)) && (obj instanceof byte[])) {
                      targetbin = (byte[])obj;
                }

            }

        } catch (JMSException ex) {
            //ex.printStackTrace();
        } catch (Throwable thex) {

        }
                

                

                String destname = "";
                String desttype = "QUEUE";

                if (dest != null) {
                   if (dest instanceof Queue) {
                     destname = ((Queue) dest).getQueueName();
                   } else if (dest instanceof Topic) {
                     destname = ((Topic) dest).getTopicName();
                     desttype = "TOPIC";
                   }
                }

                String target_file_name = timestamp + "_" + desttype + "_" + destname + "_" + key + "_" + System.currentTimeMillis() + ".bin";

                File efile = new File(inputf.getAbsolutePath() + File.separator + target_file_name);
                byte[] bibi = new byte[1024];

                bis = new ByteArrayInputStream(targetbin);
                fo = new FileOutputStream(efile);

                int len = 0;
                long readfilesize = 0;
                int count = 100;

                TextArea ta = new TextArea("", 6, 60, TextArea.SCROLLBARS_VERTICAL_ONLY);
                ta.setEditable(false);
                ta.setBackground(Color.WHITE);

                popupMessageDialog(resources.getString("qkey.msg.msg194"), ta,
                            QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Download));
                


                while ((len = bis.read(bibi, 0, bibi.length)) != -1) {
                    fo.write(bibi, 0, len);
                    readfilesize += len;
                    if (++count > 100) {
                        ta.append(readfilesize + " " + resources.getString("qkey.msg.msg098") + targetbin.length + " " + resources.getString("qkey.msg.msg099") + "\n");
                        ta.setCaretPosition(ta.getText().length());
                        count = 0;
                    }

                }

                if (count != 0) {
                        ta.append(readfilesize + " " + resources.getString("qkey.msg.msg098") + targetbin.length + " " + resources.getString("qkey.msg.msg099") + "\n");
                        ta.setCaretPosition(ta.getText().length());
                }

                ta.append(resources.getString("qkey.msg.msg195"));
                ta.append(resources.getString("qkey.msg.msg196"));
                ta.append(efile.getAbsolutePath());

                }

            } catch (Exception ee) {
                ee.printStackTrace();
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
          }

        }
    }

    //for String property
    class DownloadOKListener2 implements ActionListener {

        String key;
        StreamMessage msg;

        public DownloadOKListener2(String key_value, StreamMessage msg_value) {
            key = key_value;
            msg = msg_value;
        }

        public void actionPerformed(ActionEvent e) {

            ByteArrayInputStream bis = null;
            java.io.FileOutputStream fo = null;

            //指定されたフォルダ最終チェック
            File inputf = new File(download_file_path2.getText());
            try {

                JLabel errlabel = new JLabel(resources.getString("qkey.msg.msg193"));
                if (!inputf.exists()) {
                    errlabel.setForeground(Color.RED);
                    temppanel2.add(BorderLayout.CENTER, errlabel);
                    downloadmsgPanel2.updateUI();
                    downloadDialog2.pack();

                } else {
                    temppanel2.add(BorderLayout.CENTER, new JLabel(""));
                    temppanel2.updateUI();
                    downloadmsgPanel2.updateUI();
                    downloadDialog2.pack();

                    downloadDialog2.setVisible(false);

                    //ダウンロード処理実施！
                //ダウンロード先ファイル名作成
                long timestamp = msg.getJMSTimestamp();
                Destination dest = msg.getJMSDestination();

                String targetstring = null;

        try {

              msg.reset();

                Object obj = null;
                int rowcount = 0;

                while ((obj = ((StreamMessage)msg).readObject()) != null) {

                rowcount++;

                String name = String.valueOf(rowcount);

                if ((key.equals(name)) && (obj instanceof String)) {
                      targetstring = (String)obj;
                }

            }

        } catch (JMSException ex) {
            //ex.printStackTrace();
        } catch (Throwable thex) {

        }

                String destname = "";
                String desttype = "QUEUE";

                if (dest != null) {
                   if (dest instanceof Queue) {
                     destname = ((Queue) dest).getQueueName();
                   } else if (dest instanceof Topic) {
                     destname = ((Topic) dest).getTopicName();
                     desttype = "TOPIC";
                   }
                }

                String target_file_name = timestamp + "_" + desttype + "_" + destname + "_" + key + "_" + System.currentTimeMillis() + ".txt";

                File efile = new File(inputf.getAbsolutePath() + File.separator + target_file_name);
                byte[] bibi = new byte[1024];

                bis = new ByteArrayInputStream(targetstring.getBytes());
                fo = new FileOutputStream(efile);

                int len = 0;
                long readfilesize = 0;
                int count = 100;

                TextArea ta = new TextArea("", 6, 60, TextArea.SCROLLBARS_VERTICAL_ONLY);
                ta.setEditable(false);
                ta.setBackground(Color.WHITE);

                popupMessageDialog(resources.getString("qkey.msg.msg194"), ta,
                            QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Download));



                while ((len = bis.read(bibi, 0, bibi.length)) != -1) {
                    fo.write(bibi, 0, len);
                    readfilesize += len;
                    if (++count > 100) {
                        ta.append(readfilesize + " " + resources.getString("qkey.msg.msg098") + targetstring.length() + " " + resources.getString("qkey.msg.msg099") + "\n");
                        ta.setCaretPosition(ta.getText().length());
                        count = 0;
                    }

                }

                if (count != 0) {
                        ta.append(readfilesize + " " + resources.getString("qkey.msg.msg098") + targetstring.length() + " " + resources.getString("qkey.msg.msg099") + "\n");
                        ta.setCaretPosition(ta.getText().length());
                }

                ta.append(resources.getString("qkey.msg.msg195"));
                ta.append(resources.getString("qkey.msg.msg196"));
                ta.append(efile.getAbsolutePath());
                
                }

            } catch (Exception ee) {
                ee.printStackTrace();
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
          }

        }
    }

    private AbstractButton createBrowseButton() {
        final JButton button = new JButton("...");
        button.setMnemonic('B');
        button.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (download_file_path.getText().length() > 0) {
                    _currentFolder = _folderChooser.getFileSystemView().createFileObject(download_file_path.getText());
                }
                _folderChooser.setCurrentDirectory(_currentFolder);
                _folderChooser.setRecentList(_recentList);
                _folderChooser.setFileHidingEnabled(true);
                int result = _folderChooser.showOpenDialog(button.getTopLevelAncestor());
                if (result == FolderChooser.APPROVE_OPTION) {
                    _currentFolder = _folderChooser.getSelectedFile();
                    if (_recentList.contains(_currentFolder.toString())) {
                        _recentList.remove(_currentFolder.toString());
                    }
                    _recentList.add(0, _currentFolder.toString());
                    File selectedFile = _folderChooser.getSelectedFile();
                    if (selectedFile != null) {
                        download_file_path.setText(selectedFile.toString());
                    }
                    else {
                        download_file_path.setText("");
                    }
                }
            }
        });
        button.setRequestFocusEnabled(false);
        button.setFocusable(false);
        return button;
    }

     private AbstractButton createBrowseButton2() {
        final JButton button = new JButton("...");
        button.setMnemonic('B');
        button.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (download_file_path2.getText().length() > 0) {
                    _currentFolder = _folderChooser2.getFileSystemView().createFileObject(download_file_path2.getText());
                }
                _folderChooser2.setCurrentDirectory(_currentFolder);
                _folderChooser2.setRecentList(_recentList);
                _folderChooser2.setFileHidingEnabled(true);
                int result = _folderChooser2.showOpenDialog(button.getTopLevelAncestor());
                if (result == FolderChooser.APPROVE_OPTION) {
                    _currentFolder = _folderChooser2.getSelectedFile();
                    if (_recentList.contains(_currentFolder.toString())) {
                        _recentList.remove(_currentFolder.toString());
                    }
                    _recentList.add(0, _currentFolder.toString());
                    File selectedFile = _folderChooser2.getSelectedFile();
                    if (selectedFile != null) {
                        download_file_path2.setText(selectedFile.toString());
                    }
                    else {
                        download_file_path2.setText("");
                    }
                }
            }
        });
        button.setRequestFocusEnabled(false);
        button.setFocusable(false);
        return button;
    }

        public JDialog popupMessageDialog(String title, TextArea ta, ImageIcon icon) {

        if (msgDialog != null && msgDialog.isShowing()) {
            msgDialog.dispose();
        }
        msgDialog = new JDialog();
        msgDialog.setIconImage(icon.getImage());
        msgDialog.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
            }
        });
        msgDialog.setTitle(title);
        msgDialog.setLocation(250, 150);

        JPanel mainmsg = new JPanel();
        mainmsg.add(ta);

        msgDialog.getContentPane().setLayout(new BorderLayout());

        msgconfirmbutton = new JButton("OK");
        msgconfirmbutton.addActionListener(new MsgConfirmedListener());

        msgDialog.getContentPane().add(BorderLayout.NORTH, mainmsg);
        msgDialog.getContentPane().add(BorderLayout.SOUTH, msgconfirmbutton);
        msgDialog.pack();
        msgDialog.setLocationRelativeTo(oya);
        msgDialog.setVisible(true);
        return msgDialog;
    }

    class MsgConfirmedListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            msgDialog.setVisible(false);
        }
    }

    }
