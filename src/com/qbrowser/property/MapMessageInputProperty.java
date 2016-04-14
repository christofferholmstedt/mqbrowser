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

/**
 *
 * @author takemura
 */
public class MapMessageInputProperty extends Property {

    private Object type_combo_box;
    private Object filechoose_button;

    /**
     * @param property_type the property_type to set
     */
    public void setProperty_type_combobox(Object box) {
        type_combo_box = box;
    }

    /**
     * @return the filechoose_button
     */
    public Object getFilechoose_button() {
        return filechoose_button;
    }

    /**
     * @param filechoose_button the filechoose_button to set
     */
    public void setFilechoose_button(Object filechoose_button) {
        this.filechoose_button = filechoose_button;
    }

    /**
     * @return the type_combo_box
     */
    public Object getType_combo_box() {
        return type_combo_box;
    }




}
