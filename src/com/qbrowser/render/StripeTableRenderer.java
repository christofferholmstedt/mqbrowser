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
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class StripeTableRenderer extends DefaultTableCellRenderer {
  private static final Color evenColor = new Color(238, 238, 255);
  public Component getTableCellRendererComponent(JTable table, Object value,
                           boolean isSelected, boolean hasFocus,
                           int row, int column) {
    super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    if(isSelected) {

      setForeground(table.getSelectionForeground());
      setBackground(table.getSelectionBackground());


    }else{

      setForeground(table.getForeground());
      setBackground((row%2==0)?evenColor:table.getBackground());
    }
    setHorizontalAlignment(CENTER);
    return this;
  }
}