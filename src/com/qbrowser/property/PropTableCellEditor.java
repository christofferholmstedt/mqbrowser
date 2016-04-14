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

import java.awt.Component;
import java.awt.event.ActionListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;

public class PropTableCellEditor extends DefaultCellEditor {
	private static final long serialVersionUID = 1L;
	private Component comp;
	private Object val;

	public PropTableCellEditor(){
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
			val=value;
			comp=super.getTableCellEditorComponent(table,value,isSelected,row,column);
			return comp;
		}
	}

}
