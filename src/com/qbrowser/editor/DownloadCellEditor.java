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

package com.qbrowser.editor;

import com.qbrowser.QBrowserV2;
import java.awt.Component;
import java.awt.event.ActionListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class DownloadCellEditor extends DefaultCellEditor {
	private static final long serialVersionUID = 1L;
	private Component comp;
	private Object val;

	public DownloadCellEditor(){
		super(new JTextField());
	}

	@Override
	public Object getCellEditorValue() {

        Object obj = super.getCellEditorValue();

        if (comp instanceof JTextArea) {
            //String typeでJTextAreaが入っている場合はそのまま
            return (JTextArea) comp;
        } else if (comp instanceof JComboBox) {
            return (JComboBox) comp;
        } else if (comp instanceof JButton) {
            return (JButton) comp;
        } else if (comp instanceof JLabel) {
            return (JLabel) comp;
        } else if (comp instanceof JTextField) {
            JTextField jtf1 = new JTextField();
            JTextField jtf2 = (JTextField)comp;
            jtf1.setText(jtf2.getText());
            comp = jtf1;
            return (JTextField)comp;
        } else

        if (obj instanceof String) {

            String strval = (String)obj;
            JTextField jtf1 = new JTextField();
            jtf1.setText(strval);
            comp = jtf1;
            return (JTextField)comp;

        } else {

            //これはない。
            return obj;
        }




	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {

        super.delegate.setValue(value);

		if(value instanceof JButton){
			JButton tmp=new JButton(((JButton)value).getText());
			ActionListener[] ac=((JButton)value).getActionListeners();
			for(int i=0;i<ac.length;i++){
				tmp.addActionListener(ac[i]);
			}
            //Integer qbid = (Integer)((JButton)value).getClientProperty(QBrowserV2.QBBUTTONID);
            tmp.putClientProperty(QBrowserV2.QBBUTTONROWPOSITION, new Integer(row));
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
			val=value;
			comp=super.getTableCellEditorComponent(table,value,isSelected,row,column);
			return comp;
		}
	}

}
