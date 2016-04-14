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

import javax.swing.JComboBox;

/**
 *
 * @author takemura
 */
public class InputProperty extends Property {
        private Object type_combo_box;

    /**
     * @return the type_combo_box
     */
    public Object getType_combo_box() {
        return type_combo_box;
    }

    /**
     * @param type_combo_box the type_combo_box to set
     */
    public void setType_combo_box(Object type_combo_box) {
        this.type_combo_box = type_combo_box;
    }

    @Override
    public void selfValidate() throws QBrowserPropertyException {

        JComboBox jcb = (JComboBox)getType_combo_box();
        if (jcb != null)
        setProperty_type((String)jcb.getSelectedItem());
        super.selfValidate();

    }



}
