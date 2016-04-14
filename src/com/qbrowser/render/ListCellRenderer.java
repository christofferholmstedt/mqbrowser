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
package com.qbrowser.render;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class ListCellRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;
    private static final Color evenColor = new Color(248, 248, 255);

    public ListCellRenderer() {
    }

    @Override
    public Component getTableCellRendererComponent(JTable arg0, Object arg1, boolean arg2, boolean arg3, int arg4, int arg5) {
//arg4 = row arg5 = column

        setHorizontalAlignment(LEFT);

        if (arg1 instanceof JComboBox) {
            //JComboBox c = (JComboBox) arg1;
            //return super.getTableCellRendererComponent(arg0, c.getSelectedItem(), arg2, arg3, arg4, arg5);
            return (JComboBox) arg1;
        } else if (arg1 instanceof JComponent) {
            return (JComponent) arg1;
        } else {
            if (arg2) {

                super.setForeground(arg0.getSelectionForeground());
                super.setBackground(arg0.getSelectionBackground());


            } else {

                super.setForeground(arg0.getForeground());
                super.setBackground((arg4 % 2 == 0) ? evenColor : arg0.getBackground());
            }
            return super.getTableCellRendererComponent(arg0, arg1, arg2, arg3, arg4, arg5);
        }
    }
}
