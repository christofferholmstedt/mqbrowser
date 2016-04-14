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
import com.qbrowser.icons.QBrowserIconsFactory;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

public class PropTableCellEditor2 extends DefaultCellEditor {
	private static final long serialVersionUID = 1L;
	private Component comp;
	private Object val;

    JDialog filechooseDialog = null;
    JPanel filechoosemsgPanel = null;
    JTextField filechoose_file_path = null;
    JPanel temppanel = null;
    static JFileChooser file_chooser = null;
    static File _currentFolder = null;
    static List<String> _recentList = new ArrayList<String>();
    //public static ResourceBundle resources = ResourceBundle.getBundle("com.qbrowser.resourcebase");
    static ResourceBundle resources = QBrowserV2.resources;
    JLabel errlabel = null;
    JButton filechoose_okbutton = null;

	public PropTableCellEditor2(){
		super(new JTextField());
	}

	@Override
	public Object getCellEditorValue() {
		if(val instanceof Number){
			try{
				if(val instanceof Integer){
					return Integer.parseInt(super.getCellEditorValue().toString());
				}else if(val instanceof Float){
					return Float.parseFloat(super.getCellEditorValue().toString());
				}else if(val instanceof Double){
					return Double.parseDouble(super.getCellEditorValue().toString());
				}else if(val instanceof Long){
					return Long.parseLong(super.getCellEditorValue().toString());
				}else{
					return Double.parseDouble(super.getCellEditorValue().toString());
				}
			}catch(NumberFormatException ne){
				JOptionPane.showMessageDialog(comp,"NumberFormatException","",JOptionPane.ERROR_MESSAGE);
				return val;
			}
		}else{
			if(comp instanceof JCheckBox){
				if(((JCheckBox)comp).isSelected()){
					return Boolean.TRUE;
				}else{
					return Boolean.FALSE;
				}
			}else if(comp instanceof JTextField){
				return ((JTextField)comp).getText();
			}else if(comp instanceof JComboBox){
				return (JComboBox)comp;
			}else if(comp instanceof JButton){
				return (JButton)comp;
			}else if(comp instanceof JLabel){
				return (JLabel)comp;
			}else{
				return super.getCellEditorValue();
			}
		}
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		if(value instanceof JButton){
			JButton tmp=new JButton(((JButton)value).getText());
			ActionListener[] ac=((JButton)value).getActionListeners();
			for(int i=0;i<ac.length;i++){
				tmp.addActionListener(ac[i]);
			}
			comp=tmp;
			val=value;
			return comp;
		}else if(value instanceof JComboBox){
			JComboBox tmp=new JComboBox(((JComboBox)value).getModel());
			ActionListener[] ac=((JComboBox)value).getActionListeners();
			for(int i=0;i<ac.length;i++){
				tmp.addActionListener(ac[i]);
			}
			comp=tmp;
			val=value;
			return comp;
		}else if(value instanceof JLabel){
			JLabel tmp=(JLabel)value;
			comp=tmp;
			val=tmp;
			return comp;
		}else{
            showFileChooseWindow();
            stopCellEditing();

            filechooseDialog.toFront();
			val=value;
			comp=super.getTableCellEditorComponent(table,value,isSelected,row,column);
			return comp;
		}
	}

    public void showFileChooseWindow() {

        // Create popup
        if (filechooseDialog == null) {
            filechooseDialog = new JDialog();
            filechooseDialog.setIconImage(QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Upload).getImage());


            filechooseDialog.addWindowListener(new WindowAdapter() {

                @Override
                public void windowClosing(WindowEvent e) {
                }
            });

            filechooseDialog.getContentPane().setLayout(new BorderLayout());

            filechoosemsgPanel = new JPanel();
            filechoosemsgPanel.setLayout(new BorderLayout());

            JPanel filechoosemsg = new JPanel();
            filechooseDialog.setSize(200, 300);
            filechooseDialog.setTitle(resources.getString("qkey.msg.msg198"));

            filechoose_file_path = new JTextField(30);
            filechoose_file_path.addCaretListener(new FileChooserpathInputListener());

            file_chooser = new JFileChooser();

            JButton file_choose_button = (JButton)createBrowseButton();

            JLabel filechooselabel = new JLabel(resources.getString("qkey.msg.msg199"));
            JPanel expl = new JPanel();
            expl.setLayout(new BorderLayout());

            JPanel tqboxpanel = new JPanel();
            tqboxpanel.setLayout(new BorderLayout());
            expl.add(BorderLayout.NORTH, tqboxpanel);
            expl.add(BorderLayout.CENTER, filechooselabel);


            filechoosemsg.add(filechoose_file_path);
            filechoosemsg.add(file_choose_button);
            filechoosemsgPanel.add(BorderLayout.NORTH, expl);
            filechoosemsgPanel.add(BorderLayout.CENTER, filechoosemsg);
            filechoose_okbutton = new JButton("              OK              ");
            filechoose_okbutton.addActionListener(new FileChooseOKListener());
            JButton cancelbutton = new JButton("         " + resources.getString("qkey.msg.msg129") + "             ");
            cancelbutton.addActionListener(new FileChooseCancelListener());

            JPanel pbuttonpanel = new JPanel();
            pbuttonpanel.setLayout(new BorderLayout());
            pbuttonpanel.add(BorderLayout.WEST, filechoose_okbutton);
            pbuttonpanel.add(BorderLayout.CENTER, cancelbutton);

            temppanel = new JPanel();
            temppanel.setLayout(new BorderLayout());

            errlabel = new JLabel();
            temppanel.add(BorderLayout.CENTER, errlabel);
            temppanel.add(BorderLayout.SOUTH, pbuttonpanel);

            filechoosemsgPanel.add(BorderLayout.SOUTH, temppanel);


            filechooseDialog.getContentPane().add(BorderLayout.NORTH, filechoosemsgPanel);
            filechooseDialog.pack();


        }

        //filechooseDialog.setLocationRelativeTo(QBrowserV2.newmessageFrame);
        filechooseDialog.setLocation(QBrowserV2.newmessageFrame.getX() + 520, QBrowserV2.newmessageFrame.getY() + 500);
        filechooseDialog.setVisible(true);




    }


    private AbstractButton createBrowseButton() {
        final JButton button = new JButton("...");
        button.setMnemonic('B');
        button.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (filechoose_file_path.getText().length() > 0) {
                    _currentFolder = file_chooser.getFileSystemView().createFileObject(filechoose_file_path.getText());
                }
                file_chooser.setCurrentDirectory(_currentFolder);
                //file_chooser.setRecentList(_recentList);
                file_chooser.setFileHidingEnabled(true);
                int result = file_chooser.showOpenDialog(button.getTopLevelAncestor());
                if (result == FolderChooser.APPROVE_OPTION) {
                    _currentFolder = file_chooser.getSelectedFile();
                    if (_recentList.contains(_currentFolder.toString())) {
                        _recentList.remove(_currentFolder.toString());
                    }
                    _recentList.add(0, _currentFolder.toString());
                    File selectedFile = file_chooser.getSelectedFile();
                    if (selectedFile != null) {
                        filechoose_file_path.setText(selectedFile.toString());
                    }
                    else {
                        filechoose_file_path.setText("");
                    }
                }
            }
        });
        button.setRequestFocusEnabled(false);
        button.setFocusable(false);
        return button;
    }


    class FileChooseCancelListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            filechooseDialog.setVisible(false);

        }
    }

    class FileChooseOKListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            //ファイル選択結果をここへ。
            filechooseDialog.setVisible(false);

        }

    }

    class FileChooserpathInputListener implements CaretListener {

        public void caretUpdate(CaretEvent e) {

            filechoose_okbutton.setEnabled(false);

            if (filechoose_file_path.getText().trim().endsWith("\\")) {
                errlabel.setText(resources.getString("qkey.msg.msg115"));
                errlabel.setForeground(Color.RED);
                errlabel.updateUI();
                temppanel.updateUI();
                filechoosemsgPanel.updateUI();
                filechooseDialog.pack();
                return;
            }


            //downloadfilepath
            try {
                File df = new File(filechoose_file_path.getText().trim());
                if ((df != null)) {



                    if (df.isDirectory()) {

                        errlabel.setText(resources.getString("qkey.msg.msg117"));
                        errlabel.setForeground(Color.RED);
                        errlabel.updateUI();
                        temppanel.updateUI();
                        filechoosemsgPanel.updateUI();
                        filechooseDialog.pack();
                        return;

                    }

                    if (!df.exists()) {
                        errlabel.setText(resources.getString("qkey.msg.msg200"));
                        errlabel.setForeground(Color.RED);
                        errlabel.updateUI();
                        temppanel.updateUI();
                        filechoosemsgPanel.updateUI();
                        filechooseDialog.pack();
                        return;
                    } else {
                        errlabel.setText("");
                        errlabel.updateUI();
                        temppanel.updateUI();
                        filechoosemsgPanel.updateUI();
                        filechooseDialog.pack();
                        filechoose_okbutton.setEnabled(true);

                        return;
                    }

                }
            } catch (Exception fe) {
                //fe.printStackTrace();
                filechoose_okbutton.setEnabled(false);
            }
        }
    }


}
