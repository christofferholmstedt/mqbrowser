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

package com.qbrowser.display;

import java.awt.TextArea;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author takemura
 */
 public class DisplayMsgDialogRunner implements Runnable {

        String title;
        TextArea ta;
        ImageIcon icon;
        String sourceid;
        JDialog cm;
        boolean start = false;
        JPanel textPanel;
        JFrame oya;

        public DisplayMsgDialogRunner(String sourceidv ,String titlev, TextArea tav, ImageIcon iconv, JFrame voya) {
            title = titlev;
            ta = tav;
            icon = iconv;
            sourceid = sourceidv;
            oya = voya;
        }

        public boolean isStarted() {
            return start;
        }

        public JDialog getMessageDialog() {
            return cm;
        }

        public JPanel getTextPanel() {
            return textPanel;
        }

        public void run() {

            try {

                textPanel = createSimpleTextAreaPane(ta);
                cm = DisplayMsgDialogFactory.popupDisposalMessageDialog(sourceid, title, textPanel , icon, oya);
                start = true;

            } catch (Exception e) {
                e.printStackTrace();
            }

            DisplayDialogThreadPool.removeDisplayThread(this);


        }

    JPanel createSimpleTextAreaPane(TextArea textArea) {
        JPanel panel = new JPanel();
        panel.add(textArea);
        return panel;
    }

    }

