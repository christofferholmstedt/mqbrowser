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

import javax.swing.JTextArea;

/**
 *
 * @author takemura
 */
public class ReadOnlyPropertyTable extends PropertyTable {

                @Override
                public boolean isCellEditable(int row, int column) {

                    if (column == 2) {

                      Object obj = this.getValueAt(row, column);
                      if (obj instanceof JTextArea) {
                          return true;
                      } else {
                          return false;
                      }

                    } else {

                      return false;

                    }
                }
}
