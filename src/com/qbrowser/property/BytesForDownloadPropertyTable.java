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
import javax.jms.MapMessage;
import javax.jms.Message;
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
public class BytesForDownloadPropertyTable extends  DefaultTableModel {

    //public static ResourceBundle resources = ResourceBundle.getBundle("com.qbrowser.resourcebase");
    static ResourceBundle resources = QBrowserV2.resources;
    JDialog downloadDialog = null;
    JPanel downloadmsgPanel = null;
    JPanel temppanel = null;
    JTextField download_file_path = null;
    static FolderChooser _folderChooser;
    static List<String> _recentList = new ArrayList<String>();
    static File _currentFolder = null;
    String download_target_key = null;
    MapMessage download_target_message = null;
    JDialog msgDialog = null;
    JButton msgconfirmbutton = null;

    JPanel oya = null;

    public void setOya(JPanel value) {
        oya = value;
    }

    private javax.jms.Message innermessage = null;

        final String[] columnNames =
                {resources.getString("qkey.proptable.header.key"),
            resources.getString("qkey.proptable.header.prop_type"),
            resources.getString("qkey.proptable.header.download")};

        LinkedList list = null;

        ArrayList type_selection = null;

        public BytesForDownloadPropertyTable() {
            init();
            add_one_row(new Property());
            
        }

 public BytesForDownloadPropertyTable(int number_of_initial_rows) {
            init();
            for (int i = 0 ; i < number_of_initial_rows; i++) {
               add_one_row(new Property());
            }

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
                    return String.class;
                case 1:
                    return String.class;
                case 2:
                    return Object.class;

            }
            return Object.class;
        }
        

                @Override
                public boolean isCellEditable(int row, int column) {
                    return true;
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

            Property p = (Property) list.get(row);
            if (p == null) {
                return;
            }

            try {
                switch (column) {
                    case 0:
                        // Property KEY
                        if (value instanceof String) {
                          p.setKey((String)value);
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

            Property p = (Property) list.get(row);

            if (p == null) {
                return "";
            }

            try {
                switch (column) {
                    case 0:
                        // Property KEY
                        return p.getKey();
                    case 1:
                        // Property TYPE
                        return p.getProperty_type();

                    case 2:
                        // Property TYPE
                        return p.getProperty_value();

                    default:
                        return "Bad column value: " + column;
                }
            } catch (Exception e) {
                return ("Error: " + e);
            }
        }

    public int load(MessageContainer mc) {

        if (mc == null)
            return 0;

        Message msgvalue = mc.getMessage();

        if (msgvalue == null)
            return 0;

        MapMessage msg = null;

        if (msgvalue instanceof MapMessage) {
            msg = (MapMessage)msgvalue;
        } else {
            return 0;
        }

        list = new LinkedList();

        try {
            for (Enumeration enu = msg.getMapNames();
                    enu.hasMoreElements();) {
                String name = (enu.nextElement()).toString();
                Object obj = msg.getObject(name);
                if (obj instanceof byte[]) {
                      Property ap = new Property();
                      ap.setKey(name);
                      ap.setProperty_type(Property.BYTES_TYPE);
                      JButton db = new JButton("download");
                      db.addActionListener(new DownloadButtonListener(name, msg));
                      ap.setProperty_value(db);
                      list.add(ap);
                }

            }

            this.innermessage = msg;
            
        } catch (JMSException ex) {
            ex.printStackTrace();
        }

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

        public void add_one_row(Property value) {
            list.add(value);
            fireTableDataChanged();
        }

        public void add_one_empty_row() {
            list.add(new Property());
            fireTableDataChanged();
        }

        public void deletePropertyAtRow(int row) {
            if (list == null) {
                return;
            }

            list.remove(row);
            fireTableDataChanged();

        }

        public Property getPropertyAtRow(int row) {
            if (list == null) {
                return null;
            }
            return ((Property) list.get(row));
        }

    public void showDownloadWindow() {

        // Create popup
        if (downloadDialog == null) {
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
            downloadDialog.setTitle(resources.getString("qkey.msg.msg191"));

            download_file_path = new JTextField(30);
            _folderChooser = new FolderChooser();
            _folderChooser.setAvailableButtons(_folderChooser.getAvailableButtons() & ~FolderChooser.BUTTON_DELETE);
            _folderChooser.setNavigationFieldVisible(true);
            JButton file_choose_button = (JButton)createBrowseButton();

            JLabel downloadlabel = new JLabel(resources.getString("qkey.msg.msg192"));
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
            okbutton1.addActionListener(new DownloadOKListener());
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

        }

        downloadDialog.setLocationRelativeTo(oya);
        downloadDialog.setVisible(true);



    }

    class DownloadButtonListener implements ActionListener {

        

        public DownloadButtonListener(String key, MapMessage msg) {
            download_target_key = key;
            download_target_message = msg;
        }

        public void actionPerformed(ActionEvent e) {
            showDownloadWindow();
            
        }
    }

    class DownloadCancelListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            downloadDialog.setVisible(false);

        }
    }

    class DownloadOKListener implements ActionListener {

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
                long timestamp = download_target_message.getJMSTimestamp();
                Destination dest = download_target_message.getJMSDestination();

                byte[] targetbin = download_target_message.getBytes(download_target_key);

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

                String target_file_name = timestamp + "_" + desttype + "_" + destname + "_" + download_target_key + "_" + System.currentTimeMillis() + ".bin";

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
