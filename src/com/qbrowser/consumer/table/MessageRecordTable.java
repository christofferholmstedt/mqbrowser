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

package com.qbrowser.consumer.table;

import com.qbrowser.QBrowserV2;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author takemura
 */
public class MessageRecordTable extends  DefaultTableModel {

    //public static ResourceBundle resources = ResourceBundle.getBundle("com.qbrowser.resourcebase");
    JPanel oya = null;
    static ResourceBundle resources = QBrowserV2.resources;

    public void setOya(JPanel value) {
        oya = value;
    }


        final String[] columnNames =
                {resources.getString("qkey.recordtable.header.dest_name"),
            resources.getString("qkey.recordtable.header.status"),
            resources.getString("qkey.recordtable.header.count"),
            resources.getString("qkey.recordtable.header.button")
            };

        LinkedList list = null;

        ArrayList type_selection = null;

        public MessageRecordTable() {
            init();        
        }

        public MessageRecordTable(int number_of_initial_rows) {
            init();
            for (int i = 0 ; i < number_of_initial_rows; i++) {
               add_one_row(new MessageRecordProperty());
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
                    return Integer.class;
                case 3:
                    return Object.class;

            }
            return Object.class;
        }
        

                @Override
                public boolean isCellEditable(int row, int column) {

                    Object robj = getValueAt(row, column);

                    if (robj instanceof JButton) {
                        return true;
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

            if (list.size() <= row) {
                return;
            }

            MessageRecordProperty p = (MessageRecordProperty) list.get(row);
            if (p == null) {
                return;
            }

            try {
                switch (column) {
                    case 0:
                        // DestName
                        if (value instanceof String) {
                          p.setDestName((String)value);
                        }
                        return;
                    case 1:
                        // Property TYPE
                        if (value instanceof String) {
                          p.setConsumerThreadStatus((String)value);
                        }
                        return;

                    case 2:
                        // Property value
                        if (value instanceof Integer) {
                          p.setCount(((Integer)value).intValue());
                        } else if (value instanceof String) {
                          int c = 0;
                          try {
                              c = Integer.parseInt((String)value);
                          } catch (NumberFormatException nfe) {

                          }
                        }

                    case 3:
                        // Property value
                          if (value instanceof JButton)
                          p.setButton((JButton)value);

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

            MessageRecordProperty p = (MessageRecordProperty) list.get(row);

            if (p == null) {
                return "";
            }

            try {
                switch (column) {
                    case 0:
                        // destName
                        return p.getDestName();
                    case 1:
                        // ConsumerThreadStatus
                        return p.getConsumerThreadStatus();

                    case 2:
                        // Property Value
                        return p.getCount();

                    case 3:
                        // Property TYPE
                        Object no4 = p.getButton();
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

        public void add_one_row(MessageRecordProperty value) {
            list.add(value);
            fireTableDataChanged();
        }

        public void add_one_empty_row() {
            list.add(new MessageRecordProperty());
            fireTableDataChanged();
        }

        public void deletePropertyAtRow(int row) {
            if (list == null) {
                return;
            }

            list.remove(row);
            fireTableDataChanged();

        }

        public MessageRecordProperty getPropertyAtRow(int row) {
            if (list == null) {
                return null;
            }
            return ((MessageRecordProperty) list.get(row));
        }


    }
