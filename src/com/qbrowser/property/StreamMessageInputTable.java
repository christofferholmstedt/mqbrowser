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

import com.qbrowser.QBrowserV2;
import java.awt.BorderLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javax.jms.JMSException;
import javax.jms.MessageEOFException;
import javax.jms.StreamMessage;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author takemura
 */
public class StreamMessageInputTable extends DefaultTableModel {

    //public static ResourceBundle resources = ResourceBundle.getBundle("com.qbrowser.resourcebase");
    static ResourceBundle resources = QBrowserV2.resources;
    JDialog filechooseDialog = null;
    JPanel filechoosemsgPanel = null;
    JPanel temppanel = null;
    JTextField filechoose_file_path = null;
    String download_target_key = null;
    StreamMessage download_target_message = null;
    JDialog msgDialog = null;
    JButton msgconfirmbutton = null;

    JPanel oya = null;

    public void setOya(JPanel value) {
        oya = value;
    }

    private javax.jms.Message innermessage = null;

        final String[] columnNames =
                {resources.getString("qkey.proptable.header.smkey"),
            resources.getString("qkey.proptable.header.prop_type"),
            resources.getString("qkey.proptable.header.prop_value"),
            resources.getString("qkey.proptable.header.filechoose")
            };

        LinkedList list = null;

        ArrayList type_selection = null;

        public StreamMessageInputTable() {
            init();
            //StreamMessageInputProperty mmip =
            add_one_row(new StreamMessageInputProperty());

        }

        public StreamMessageInputProperty createStreamMessageInputProperty() {
               StreamMessageInputProperty newmm = new StreamMessageInputProperty();
               newmm.setProperty_type_combobox(getStreamMessagePropTypeComboBox());
               return newmm;
        }

        public StreamMessageInputTable(int number_of_initial_rows) {
            init();
            for (int i = 0 ; i < number_of_initial_rows; i++) {
               //デフォルトでJComboboxを埋め込んでおく
               StreamMessageInputProperty newsm = new StreamMessageInputProperty();
               newsm.setProperty_type_combobox(getStreamMessagePropTypeComboBox());
               add_one_row(newsm);
            }

        }

        public JComboBox getTypeComboBoxAt(int row) {

            StreamMessageInputProperty ip = getPropertyAtRow(row);
            return (JComboBox)ip.getType_combo_box();

        }

        public void renumberAll() {
            for (int i = 0 ; i < list.size(); i++) {
                StreamMessageInputProperty smip = (StreamMessageInputProperty)list.get(i);
                smip.setSmKey(i+1);
            }

            fireTableDataChanged();
        }

        public JTextArea getStringTextAreaAt(int row) {
            StreamMessageInputProperty ip = getPropertyAtRow(row);
            Object obj = ip.getProperty_value();
            if (obj instanceof JTextArea) {
                return (JTextArea)obj;
            } else {
                return null;
            }
        }

        public JButton getStringButtonAt(int row) {
            StreamMessageInputProperty ip = getPropertyAtRow(row);
            Object obj = ip.getFilechoose_button();
            if ((obj instanceof JButton) && (ip.getType_combo_box() != null)
                    && (((String)((JComboBox)ip.getType_combo_box()).getSelectedItem()).equals(Property.STRING_TYPE))) {
                return (JButton)obj;
            } else {
                return null;
            }
        }

        public void setItemListenerInComboBoxAt(int row, ItemListener lsnr) {
            JComboBox jcb = getTypeComboBoxAt(row);
            if (jcb != null)
            jcb.addItemListener(lsnr);

        }

        public void setMouseListenerInTextAreaAt(int row, MouseListener lsnr) {
            JTextArea jta = getStringTextAreaAt(row);
            if (jta != null)
            jta.addMouseListener(lsnr);
        }

        public void setActionListenerInButtonAt(int row , ActionListener lsnr) {
            JButton jbt = getStringButtonAt(row);
            if (jbt != null) {
              jbt.addActionListener(lsnr);
            }
        }

        public int findRowNumberFromJComboBox(JComboBox src) {
            //全件なめて指定されたJComboBoxのIDと同じのが
            //どの行に入っているかを探し出す
            if (list == null) return -1;

            for (int i = 0 ; i < list.size(); i++) {
                StreamMessageInputProperty ip = (StreamMessageInputProperty)list.get(i);
                if (src.equals(ip.getType_combo_box())) {
                    return i;
                }
            }

            return -1;
        }

        public int findRowNumberFromJTextArea(JTextArea src) {
            //全件なめて指定されたJComboBoxのIDと同じのが
            //どの行に入っているかを探し出す
            if (list == null) return -1;

            for (int i = 0 ; i < list.size(); i++) {
                StreamMessageInputProperty ip = (StreamMessageInputProperty)list.get(i);
                if (src.equals(ip.getProperty_value())) {
                    return i;
                }
            }

            return -1;
        }

        public int findRowNumberFromJButton(JButton src) {
            //全件なめて指定されたJButtonと同じのが
            //どの行に入っているかを探し出す
            if (list == null) return -1;

            for (int i = 0 ; i < list.size(); i++) {
                StreamMessageInputProperty ip = (StreamMessageInputProperty)list.get(i);
                if (src.equals(ip.getFilechoose_button())) {
                    return i;
                }
            }

            return -1;
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
                    return Object.class;
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


                    Object obj = getValueAt(row, column);


                    if (obj instanceof JButton) {
                        return true;
                    } else if (column == 2){
                        if (obj instanceof String) {
                            String stobj = (String)obj;
                            if (stobj.equals(resources.getString("qkey.msg.msg222"))) {
                                return false;
                            } else {
                                return true;
                            }
                        } else {
                            return true;
                        }
                    } else if (column == 3){
                        return false;
                    } else {
                        return true;
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

            StreamMessageInputProperty p = (StreamMessageInputProperty) list.get(row);
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
                        if (value instanceof JComboBox) {
                          p.setProperty_type((String)((JComboBox)value).getSelectedItem());
                        } else
                        if (value instanceof String) {
                          if (p.validated_type != Property.PASSTHROUGH_TYPE_INT)
                          p.setProperty_type((String)value);
                        }
                        return;

                    case 2:
                        // Property value
                             p.setProperty_value(value);
                        return;

                    case 3:
                        // Property value
                          p.setFilechoose_button(value);

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

            StreamMessageInputProperty p = (StreamMessageInputProperty) list.get(row);

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
                        return p.getType_combo_box();

                    case 2:
                        // Property TYPE
                        if (p.validated_type == Property.PASSTHROUGH_TYPE_INT) {
                            return resources.getString("qkey.msg.msg222");
                        } else {

                            return p.getProperty_value();

                        }

                    case 3:
                        // Property TYPE
                        Object no4 = p.getFilechoose_button();
                        return no4;

                    default:
                        return "Bad column value: " + column;
                }
            } catch (Exception e) {
                return ("Error: " + e);
            }
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

        public int load(javax.jms.StreamMessage msg) {
            if (msg == null) {
                return 0;
            }

            list = new LinkedList();

            try {

            msg.reset();

                Object ro = null;
                int rowcount = 0;
                try {
                while ((ro = ((StreamMessage)msg).readObject()) != null) {

                    
                    rowcount++;
                    if (ro instanceof Boolean) {

                      StreamMessageInputProperty ap = new StreamMessageInputProperty();
                      ap.setSmKey(rowcount);
                      //ap.setProperty_type(Property.BOOLEAN_TYPE);
                      JComboBox jcbi = getStreamMessagePropTypeComboBox();
                      jcbi.setSelectedItem(Property.BOOLEAN_TYPE);
                      ap.setProperty_type_combobox(jcbi);
                      ap.setProperty_value(ro);
                      list.add(ap);

                    } else if (ro instanceof Byte) {

                      StreamMessageInputProperty ap = new StreamMessageInputProperty();
                      ap.setSmKey(rowcount);
                      JComboBox jcbi = getStreamMessagePropTypeComboBox();
                      jcbi.setSelectedItem(Property.BYTE_TYPE);
                      ap.setProperty_type_combobox(jcbi);
                      ap.setProperty_value(ro);
                      list.add(ap);

                    } else if (ro instanceof Short) {

                      StreamMessageInputProperty ap = new StreamMessageInputProperty();
                      ap.setSmKey(rowcount);
                      //ap.setProperty_type(Property.SHORT_TYPE);
                      JComboBox jcbi = getStreamMessagePropTypeComboBox();
                      jcbi.setSelectedItem(Property.SHORT_TYPE);
                      ap.setProperty_type_combobox(jcbi);
                      ap.setProperty_value(ro);
                      list.add(ap);

                    } else if (ro instanceof java.lang.Character) {

                      StreamMessageInputProperty ap = new StreamMessageInputProperty();
                      ap.setSmKey(rowcount);
                      //ap.setProperty_type(Property.SHORT_TYPE);
                      JComboBox jcbi = getStreamMessagePropTypeComboBox();
                      jcbi.setSelectedItem(Property.CHARACTER_TYPE);
                      ap.setProperty_type_combobox(jcbi);
                      ap.setProperty_value(ro);
                      list.add(ap);

                    } else if (ro instanceof java.lang.Integer) {

                      StreamMessageInputProperty ap = new StreamMessageInputProperty();
                      ap.setSmKey(rowcount);
                      JComboBox jcbi = getStreamMessagePropTypeComboBox();
                      jcbi.setSelectedItem(Property.INT_TYPE);
                      ap.setProperty_type_combobox(jcbi);
                      //ap.setProperty_type(Property.INT_TYPE);
                      ap.setProperty_value(ro);
                      list.add(ap);

                    } else if (ro instanceof java.lang.Long) {

                      StreamMessageInputProperty ap = new StreamMessageInputProperty();
                      ap.setSmKey(rowcount);
                      //ap.setProperty_type(Property.LONG_TYPE);
                      JComboBox jcbi = getStreamMessagePropTypeComboBox();
                      jcbi.setSelectedItem(Property.LONG_TYPE);
                      ap.setProperty_type_combobox(jcbi);
                      ap.setProperty_value(ro);
                      list.add(ap);

                    } else if (ro instanceof java.lang.Float) {

                      StreamMessageInputProperty ap = new StreamMessageInputProperty();
                      ap.setSmKey(rowcount);
                      //ap.setProperty_type(Property.FLOAT_TYPE);
                      JComboBox jcbi = getStreamMessagePropTypeComboBox();
                      jcbi.setSelectedItem(Property.FLOAT_TYPE);
                      ap.setProperty_type_combobox(jcbi);
                      ap.setProperty_value(ro);
                      list.add(ap);

                    } else if (ro instanceof java.lang.Double) {

                      StreamMessageInputProperty ap = new StreamMessageInputProperty();
                      ap.setSmKey(rowcount);
                      //ap.setProperty_type(Property.DOUBLE_TYPE);
                      JComboBox jcbi = getStreamMessagePropTypeComboBox();
                      jcbi.setSelectedItem(Property.DOUBLE_TYPE);
                      ap.setProperty_type_combobox(jcbi);
                      ap.setProperty_value(ro);
                      list.add(ap);

                    } else if (ro instanceof java.lang.String) {

                      StreamMessageInputProperty ap = new StreamMessageInputProperty();
                      ap.setSmKey(rowcount);
                      //ap.setProperty_type(Property.STRING_TYPE);
                      JComboBox jcbi = getStreamMessagePropTypeComboBox();
                      jcbi.setSelectedItem(Property.STRING_TYPE);
                      ap.setProperty_type_combobox(jcbi);

                      JTextArea jobj = new JTextArea(1,10);
                      jobj.setText((String)ro);
                      jobj.setToolTipText(resources.getString("qkey.msg.msg230"));
                      ap.setProperty_value(jobj);

                      JButton jbt = new JButton(resources.getString("qkey.msg.msg208"));
                      ap.setFilechoose_button(jbt);
                      list.add(ap);

                    } else if (ro instanceof byte[]) {

                      StreamMessageInputProperty ap = new StreamMessageInputProperty();
                      ap.setSmKey(rowcount);
                      //ap.setProperty_type(Property.BYTES_TYPE);
                      JComboBox jcbi = getStreamMessagePropTypeComboBox();
                      jcbi.setSelectedItem(Property.BYTES_TYPE);
                      ap.setProperty_type_combobox(jcbi);
                      ap.setProperty_type(Property.PASSTHROUGH_TYPE);
                      ap.validated_type = Property.PASSTHROUGH_TYPE_INT;
                      ap.setProperty_value(ro);
                      list.add(ap);

                    }

                }
                } catch (MessageEOFException eof) {
                    //NOP
                    //eof.printStackTrace();
                } catch (Throwable thex) {
                    //NOP
                    //thex.printStackTrace();
                }

            } catch (JMSException jme) {
                jme.printStackTrace();
            }


            fireTableDataChanged();
            return list.size();
        }

        void init() {

            list = new LinkedList();

        }

        public void add_one_row(StreamMessageInputProperty value) {
            value.setSmKey(list.size() + 1);
            list.add(value);
            fireTableDataChanged();
        }

        public void add_one_empty_row() {
            StreamMessageInputProperty smip = new StreamMessageInputProperty();
            smip.setSmKey(list.size());
            list.add(smip);
            fireTableDataChanged();
        }

        public void deletePropertyAtRow(int row) {
            if (list == null) {
                return;
            }

            list.remove(row);
            fireTableDataChanged();

        }

        public StreamMessageInputProperty getPropertyAtRow(int row) {
            if (list == null) {
                return null;
            }
            return ((StreamMessageInputProperty) list.get(row));
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

    private void initStreamMessagePropTypeComboBox(JComboBox value) {
        //StreamMessage Property Types
        value.addItem(Property.INT_TYPE);
        value.addItem(Property.STRING_TYPE);
        value.addItem(Property.BOOLEAN_TYPE);
        value.addItem(Property.BYTE_TYPE);
        value.addItem(Property.CHARACTER_TYPE);
        value.addItem(Property.BYTES_TYPE);
        value.addItem(Property.DOUBLE_TYPE);
        value.addItem(Property.FLOAT_TYPE);
        value.addItem(Property.LONG_TYPE);
        value.addItem(Property.SHORT_TYPE);
        value.setSelectedIndex(0);

    }

    private JComboBox getStreamMessagePropTypeComboBox() {

            JComboBox jcb = new JComboBox();

            initStreamMessagePropTypeComboBox(jcb);

//リスナはQBrowser本体でADDする。
            return jcb;
        }



    }

