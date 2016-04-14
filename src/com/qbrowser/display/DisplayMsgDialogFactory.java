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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author takemura
 */
public class DisplayMsgDialogFactory {

    static HashMap disposalPanels = new HashMap();

    public static JDialog popupDisposalMessageDialog(final String id, String title, JPanel panel, ImageIcon icon, JFrame oya) {

        System.err.println("souceid = " + id);
        //既に画面に出ているかをチェック
        //if (!disposalPanels.containsKey(id)) {

        final JDialog imsgDialog = new JDialog();
        imsgDialog.setIconImage(icon.getImage());
        imsgDialog.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                disposalPanels.remove(id);
            }
        });
        imsgDialog.setTitle(title);
        imsgDialog.setLocation(250, 150);

        imsgDialog.getContentPane().setLayout(new BorderLayout());

        JButton msgconfirmbutton = new JButton("OK");
        msgconfirmbutton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                    disposalPanels.remove(id);
                    imsgDialog.dispose();

            }

            });

        imsgDialog.getContentPane().add(BorderLayout.NORTH, panel);
        imsgDialog.getContentPane().add(BorderLayout.SOUTH, msgconfirmbutton);
        //System.out.println("X : " + oya.getX() + " Y : " + oya.getY());
        if (oya != null)
        imsgDialog.setLocation(oya.getX() + 250, oya.getY() + 250);

        imsgDialog.pack();
        imsgDialog.setVisible(true);
        disposalPanels.put(id,imsgDialog);

        return imsgDialog;

        //} else {
        //    return null;
        //}

    }
    
    public static void closeAllCurrentMsgDialog(String msgid) {

        JDialog window = (JDialog)disposalPanels.get(msgid);
        if (window != null) {
            window.dispose();
            window = null;
            disposalPanels.remove(msgid);
        }

        /*
        Collection col = disposalPanels.values();
        Iterator icol = col.iterator();
        while (icol.hasNext()) {
            JDialog window = (JDialog)icol.next();
            window.dispose();
            window = null;
        }
        */
        //disposalPanels.clear();

    }

}
