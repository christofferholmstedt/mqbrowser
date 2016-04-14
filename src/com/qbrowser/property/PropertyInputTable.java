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
import com.qbrowser.container.MessageContainer;
import java.awt.event.ItemListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author takemura
 */
public class PropertyInputTable extends  DefaultTableModel {

    //public static ResourceBundle resources = ResourceBundle.getBundle("com.qbrowser.resourcebase");
    static ResourceBundle resources = QBrowserV2.resources;

        final String[] columnNames =
                {resources.getString("qkey.proptable.header.key"),
            resources.getString("qkey.proptable.header.prop_type"),
            resources.getString("qkey.proptable.header.prop_value")};

        LinkedList list = null;

        ArrayList type_selection = null;



        public PropertyInputTable() {
            init();
            
        }

        public PropertyInputTable(int number_of_initial_rows) {
            init();
            for (int i = 0 ; i < number_of_initial_rows; i++) {
               InputProperty anewprop = new InputProperty();
               JComboBox jcb = PropertyUtil.getUserPropTypeComboBox();
               anewprop.setType_combo_box(jcb);
               add_one_row(anewprop);
            }

        }

        public int findRowNumberFromJComboBox(JComboBox src) {
            //全件なめて指定されたJComboBoxのIDと同じのが
            //どの行に入っているかを探し出す
            if (list == null) return -1;

            for (int i = 0 ; i < list.size(); i++) {
                InputProperty ip = (InputProperty)list.get(i);
                if (src.equals(ip.getType_combo_box())) {
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
                    return String.class;
                case 1:
                    return Object.class;
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

            InputProperty p = null;
            try {
                p = (InputProperty) list.get(row);
            } catch (Exception ipe) {
                ipe.printStackTrace();
                return;
            }
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
                        if (value instanceof JComboBox) {
                          p.setProperty_type((String)((JComboBox)value).getSelectedItem());
                        } else
                        if (value instanceof String) {
                          p.setProperty_type((String)value);
                        }
                        return;
                        //return type_selection;
                    case 2:
                        // property VALUE
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

        public int findRowNumberFromJTextArea(JTextArea src) {
            //どの行に入っているかを探し出す
            if (list == null) return -1;

            for (int i = 0 ; i < list.size(); i++) {
                InputProperty ip = (InputProperty)list.get(i);
                if (src.equals(ip.getProperty_value())) {
                    return i;
                }
            }

            return -1;
        }

        @Override
        public Object getValueAt(int row, int column) {
            if (list == null) {
                return null;
            }

            if (list.size() < row) {
                return null;
            }

            InputProperty p = (InputProperty) list.get(row);

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

                        return p.getType_combo_box();
                        //return type_selection;
                    case 2:
                        // property VALUE
                        return p.getProperty_value();

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

        public int load(MessageContainer mc) {
            
            if (mc == null)
                return 0;
            
            Message message = mc.getMessage();

            if (message == null) {
                return 0;
            }

            list = new LinkedList();
            int count = -1;
            try {
                for (Enumeration enu = message.getPropertyNames();
                        enu.hasMoreElements();) {

                    count++;
                    String name = (enu.nextElement()).toString();
                    Object propvalueobj = message.getObjectProperty(name);
                    InputProperty prop = new InputProperty();
                    prop.setKey(name);

                    if (propvalueobj instanceof String) {
                           JTextArea jobj = new JTextArea(1,10);
                           jobj.setText((String)propvalueobj);
                           jobj.setToolTipText(resources.getString("qkey.msg.msg230"));
                           prop.setProperty_value(jobj);

                    } else if (propvalueobj instanceof Boolean) {
                           JComboBox jcbt = new JComboBox();
                           jcbt.addItem("true");
                           jcbt.addItem("false");
                           if (((Boolean)propvalueobj).booleanValue()) {
                               jcbt.setSelectedIndex(0);
                           } else {
                               jcbt.setSelectedIndex(1);
                           }
                           prop.setProperty_value(jcbt);

                    } else {
                      prop.setProperty_value(propvalueobj);
                    }
                    prop.autoComplementTypeNme();
                    JComboBox jcb = PropertyUtil.getUserPropTypeComboBox();
                    jcb.setSelectedItem(prop.getProperty_type());
                    prop.setType_combo_box(jcb);
                    list.add(prop);
                //System.out.println(msg.getObjectProperty(name).getClass().getName()); 
                }

            } catch (JMSException jmse) {
                //NOP
            }

            fireTableDataChanged();

            return list.size();
        }

        void init() {

            list = new LinkedList();
            
        }

        public JTextArea getStringTextAreaAt(int row) {
            InputProperty ip = getPropertyAtRow(row);
            Object obj = ip.getProperty_value();
            if (obj instanceof JTextArea) {
                return (JTextArea)obj;
            } else {
                return null;
            }
        }

        public void setMouseListenerInTextAreaAt(int row, MouseListener lsnr) {
            JTextArea jta = getStringTextAreaAt(row);
            if (jta != null)
            jta.addMouseListener(lsnr);
        }

        public void add_one_row(InputProperty value) {
            list.add(value);
            fireTableDataChanged();
        }

        public void add_one_empty_row() {
            InputProperty ip = new InputProperty();
            JComboBox jcb = PropertyUtil.getUserPropTypeComboBox();
            ip.setType_combo_box(jcb);
            list.add(ip);
            fireTableDataChanged();
        }

        public JComboBox getTypeComboBoxAt(int row) {

            InputProperty ip = getPropertyAtRow(row);
            return (JComboBox)ip.getType_combo_box();

        }

        public void setItemListenerInComboBoxAt(int row, ItemListener lsnr) {
            JComboBox jcb = getTypeComboBoxAt(row);
            if (jcb != null)
            jcb.addItemListener(lsnr);

        }

        public void deletePropertyAtRow(int row) {
            if (list == null) {
                return;
            }

            list.remove(row);
            fireTableDataChanged();

        }

        public InputProperty getPropertyAtRow(int row) {
            if (list == null) {
                return null;
            }

            if (list.size() < row) {
                return null;
            }

            return ((InputProperty) list.get(row));
        }


       
    }
