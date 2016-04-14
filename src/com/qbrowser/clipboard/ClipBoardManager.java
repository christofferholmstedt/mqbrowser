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

package com.qbrowser.clipboard;

import com.qbrowser.QBrowserV2;
import java.awt.Toolkit;
import java.awt.datatransfer.*;

/**
 *
 * @author takemura
 */
public class ClipBoardManager {

    public void copyToClipBoard(String target_string) {
            Clipboard systemcClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable t = new StringSelection(target_string);

            systemcClipboard.setContents(t, null);

    }

    public void clearClipBoard() {
            Clipboard systemcClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable t = new StringSelection("");

            systemcClipboard.setContents(t , null);

    }

    public String getClipBoardData() {
           final Clipboard systemcClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            final Transferable clipboardContent = systemcClipboard.getContents(null);

            if (clipboardContent == null)
            {
               //donothing...
            }
            else
            {
               if (clipboardContent.isDataFlavorSupported(DataFlavor.stringFlavor))
               {
                  try {
                     return (String) clipboardContent.getTransferData(DataFlavor.stringFlavor);
                  } catch (Exception unsuppe) {
                      unsuppe.printStackTrace();
                  }
               }
            }

            return null;
    }

    public boolean hasClipBoardValidData() {
        final Clipboard systemcClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        final Transferable clipboardContent = systemcClipboard.getContents(null);

        if (clipboardContent == null) {
            return false;
        } else {
            if (clipboardContent.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                
                  try {
                     final String source_paths = (String) clipboardContent.getTransferData(DataFlavor.stringFlavor);
                     if (source_paths != null && source_paths.indexOf(QBrowserV2.MAGIC_SEPARATOR) != -1) {
                         return true;
                     }
                  } catch (Exception unsuppe) {
                      unsuppe.printStackTrace();
                  }
                  return false;
            } else {
                return false;
            }
        }
    }

}
