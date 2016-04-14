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

import com.qbrowser.QBrowserV2;
import java.io.File;
import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author takemura
 */
public class StreamMessageInputProperty extends Property {

    private int smKey;
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

    /**
     * @return the smKey
     */
    public int getSmKey() {
        return smKey;
    }

    /**
     * @param smKey the smKey to set
     */
    public void setSmKey(int smKey) {
        this.smKey = smKey;
    }


    @Override
    public void selfValidate() throws QBrowserPropertyException {


           if (property_type == null) {
               throw new QBrowserPropertyException("Q0001" + QBrowserV2.MAGIC_SEPARATOR + smKey + QBrowserV2.MAGIC_SEPARATOR + property_type +  QBrowserV2.MAGIC_SEPARATOR + property_value );
           }

           if (property_value == null) {
               throw new QBrowserPropertyException("Q0002" + QBrowserV2.MAGIC_SEPARATOR + smKey + QBrowserV2.MAGIC_SEPARATOR + property_type +  QBrowserV2.MAGIC_SEPARATOR + property_value );
           }

           String property_value_fordisplay = null;
           if (property_value instanceof JTextField) {
               property_value_fordisplay = ((JTextField)property_value).getText();
           } else if (property_value instanceof JTextArea) {
               property_value_fordisplay = ((JTextArea)property_value).getText();
           } else {
               property_value_fordisplay = new String(property_value.toString());
           }

           //特例として、既にvalidated_typeにpassthroughが指定されているものについては、チェックを飛ばす。
           if (this.validated_type == Property.PASSTHROUGH_TYPE_INT) {
               return;
           } else

           if (this.property_type.equals(Property.PASSTHROUGH_TYPE)) {
               validated_type = PASSTHROUGH_TYPE_INT;
               return;
           } else
           if (this.property_type.equals(Property.STRING_TYPE)) {
               try {
                   String val = getProperty_valueASString();
                   if (val.length() == 0) {
                       throw new QBrowserPropertyException("Q0002" + QBrowserV2.MAGIC_SEPARATOR + smKey + QBrowserV2.MAGIC_SEPARATOR + property_type +  QBrowserV2.MAGIC_SEPARATOR + property_value_fordisplay );
                   }
                   validated_type = STRING_TYPE_INT;
               } catch (Exception cce) {

                   throw new QBrowserPropertyException("Q0010" + QBrowserV2.MAGIC_SEPARATOR + smKey + QBrowserV2.MAGIC_SEPARATOR + property_type +  QBrowserV2.MAGIC_SEPARATOR + property_value_fordisplay );
               }
           } else if (this.property_type.equals(Property.INT_TYPE)) {
               try {
                   getProperty_valueASInt();
                   validated_type = INT_TYPE_INT;
               } catch (Exception cce) {
                   throw new QBrowserPropertyException("Q0011" + QBrowserV2.MAGIC_SEPARATOR + smKey + QBrowserV2.MAGIC_SEPARATOR + property_type +  QBrowserV2.MAGIC_SEPARATOR + property_value_fordisplay );
               }
           } else if (this.property_type.equals(Property.BOOLEAN_TYPE)) {
               try {
                   if (property_value instanceof String) {
                       String pvs = (String)property_value;
                       if (pvs.equalsIgnoreCase("true") || pvs.equalsIgnoreCase("false")) {
                           //OK
                       } else {
                           throw new QBrowserPropertyException("Q0012" + QBrowserV2.MAGIC_SEPARATOR + smKey + QBrowserV2.MAGIC_SEPARATOR + property_type + QBrowserV2.MAGIC_SEPARATOR + property_value_fordisplay );
                       }
                   } else if (property_value instanceof JComboBox) {
                       //OK 規定でしかもtrue/falseしかないので
                   }
                   validated_type = BOOLEAN_TYPE_INT;
               } catch (Exception cce) {
                   throw new QBrowserPropertyException("Q0012"+ QBrowserV2.MAGIC_SEPARATOR + smKey + QBrowserV2.MAGIC_SEPARATOR + property_type +  QBrowserV2.MAGIC_SEPARATOR + property_value_fordisplay );
               }
           } else if (this.property_type.equals(Property.CHARACTER_TYPE)) {
               try {
                   getProperty_valueASCharacter();
                   validated_type = CHARACTER_TYPE_INT;
               } catch (Exception cce) {
                   throw new QBrowserPropertyException("Q0024" + QBrowserV2.MAGIC_SEPARATOR + smKey + QBrowserV2.MAGIC_SEPARATOR + property_type +  QBrowserV2.MAGIC_SEPARATOR + property_value_fordisplay );
               }
           } else if (this.property_type.equals(Property.BYTE_TYPE)) {
               try {
                   getProperty_valueASByte();
                   validated_type = BYTE_TYPE_INT;
               } catch (Exception cce) {
                   throw new QBrowserPropertyException("Q0013" + QBrowserV2.MAGIC_SEPARATOR + smKey + QBrowserV2.MAGIC_SEPARATOR + property_type +  QBrowserV2.MAGIC_SEPARATOR + property_value_fordisplay );
               }
           } else if (this.property_type.equals(Property.BYTES_TYPE)) {
               try {

                   String filepath = getProperty_valueASString();
                   File ifile = new File(filepath);
                   if (!ifile.exists()) {
                       throw new QBrowserPropertyException("Q0022" + QBrowserV2.MAGIC_SEPARATOR + smKey + QBrowserV2.MAGIC_SEPARATOR + property_type + QBrowserV2.MAGIC_SEPARATOR + property_value_fordisplay );
                   } else if (!ifile.isFile()) {
                       throw new QBrowserPropertyException("Q0023" + QBrowserV2.MAGIC_SEPARATOR + smKey + QBrowserV2.MAGIC_SEPARATOR + property_type + QBrowserV2.MAGIC_SEPARATOR + property_value_fordisplay );
                   }
                   validated_type = BYTES_TYPE_INT;
               } catch (Exception cce) {
                   if (cce.getMessage().startsWith("Q0")) {
                     throw new QBrowserPropertyException(cce.getMessage());
                   } else {
                     throw new QBrowserPropertyException("Q0018"+ QBrowserV2.MAGIC_SEPARATOR + smKey + QBrowserV2.MAGIC_SEPARATOR + property_type +  QBrowserV2.MAGIC_SEPARATOR + property_value_fordisplay );
                   }
               }
           } else if (this.property_type.equals(Property.DOUBLE_TYPE)) {
               try {
                   getProperty_valueASDouble();
                   validated_type = DOUBLE_TYPE_INT;
               } catch (Exception cce) {
                   throw new QBrowserPropertyException("Q0014"+ QBrowserV2.MAGIC_SEPARATOR + smKey + QBrowserV2.MAGIC_SEPARATOR + property_type +  QBrowserV2.MAGIC_SEPARATOR + property_value_fordisplay );
               }
           } else if (this.property_type.equals(Property.FLOAT_TYPE)) {
               try {
                   this.getProperty_valueASFloat();
                   validated_type = FLOAT_TYPE_INT;
               } catch (Exception cce) {
                   throw new QBrowserPropertyException("Q0015"+ QBrowserV2.MAGIC_SEPARATOR + smKey + QBrowserV2.MAGIC_SEPARATOR + property_type +  QBrowserV2.MAGIC_SEPARATOR + property_value_fordisplay );
               }
           } else if (this.property_type.equals(Property.LONG_TYPE)) {
               try {
                   getProperty_valueASLong();
                   validated_type = LONG_TYPE_INT;
               } catch (Exception cce) {
                   throw new QBrowserPropertyException("Q0016"+ QBrowserV2.MAGIC_SEPARATOR + smKey + QBrowserV2.MAGIC_SEPARATOR + property_type + QBrowserV2.MAGIC_SEPARATOR + property_value_fordisplay );
               }
           } else if (this.property_type.equals(Property.SHORT_TYPE)) {
               try {

                   getProperty_valueASShort();
                   validated_type = SHORT_TYPE_INT;
               } catch (Exception cce) {
                   throw new QBrowserPropertyException("Q0017" + QBrowserV2.MAGIC_SEPARATOR + smKey + QBrowserV2.MAGIC_SEPARATOR + property_type +  QBrowserV2.MAGIC_SEPARATOR + property_value_fordisplay );
               }
           } else {
               throw new QBrowserPropertyException("Q0003" + QBrowserV2.MAGIC_SEPARATOR + smKey + QBrowserV2.MAGIC_SEPARATOR + property_type +  QBrowserV2.MAGIC_SEPARATOR + property_value_fordisplay );
           }

    }




}