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
public class Property {

    String key;
    String property_type;
    Object property_value;
    /*
     *  uptc.addItem("String");
        uptc.addItem("Int");
        uptc.addItem("Boolean");
        uptc.addItem("Byte");
        uptc.addItem("Double");
        uptc.addItem("Float");
        uptc.addItem("Long");
        uptc.addItem("Short");
     */
    public static final String STRING_TYPE = "String";
    public static final String INT_TYPE = "Int";
    public static final String BOOLEAN_TYPE = "Boolean";
    public static final String BYTE_TYPE = "Byte";
    public static final String DOUBLE_TYPE = "Double";
    public static final String FLOAT_TYPE = "Float";
    public static final String LONG_TYPE = "Long";
    public static final String SHORT_TYPE = "Short";
    public static final String BYTES_TYPE = "Bytes";
    public static final String PASSTHROUGH_TYPE = "Pass_through";
    public static final String CHARACTER_TYPE = "Char";


    public static final int INVALID_TYPE_INT = 0;
    public static final int STRING_TYPE_INT = 1;
    public static final int INT_TYPE_INT = 2;
    public static final int BOOLEAN_TYPE_INT = 3;
    public static final int BYTE_TYPE_INT = 4;
    public static final int DOUBLE_TYPE_INT = 5;
    public static final int FLOAT_TYPE_INT = 6;
    public static final int LONG_TYPE_INT = 7;
    public static final int SHORT_TYPE_INT = 8;
    public static final int BYTES_TYPE_INT = 9;
    public static final int PASSTHROUGH_TYPE_INT = 10;
    public static final int CHARACTER_TYPE_INT = 11;

    public int validated_type = INVALID_TYPE_INT;

    

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return the property_type
     */
    public String getProperty_type() {
        return property_type;
    }

    /**
     * @param property_type the property_type to set
     */
    public void setProperty_type(String property_type) {
        this.property_type = property_type;
    }

    /**
     * @return the property_value
     */
    public Object getProperty_value() {
        return property_value;
    }

    public boolean getProperty_valueASBoolean() throws Exception {
        if (property_value instanceof JComboBox) {
            String bstv = (String)((JComboBox)property_value).getSelectedItem();
            return Boolean.parseBoolean(bstv);
        } else if (property_value instanceof String) {
            return Boolean.parseBoolean((String)property_value);
        }
        return ((Boolean) property_value).booleanValue();
    }

    public byte getProperty_valueASByte() throws Exception {
        if (property_value instanceof String) {
            return Byte.parseByte((String)property_value);
        } else if (property_value instanceof JTextField) {
            return Byte.parseByte(((JTextField)property_value).getText());
        }
        return ((Byte) property_value).byteValue();
    }

    public char getProperty_valueASCharacter() throws Exception {
        if (property_value instanceof String) {
            return  ((String)property_value).toCharArray()[0];
        } else if (property_value instanceof JTextField) {
            return ((JTextField)property_value).getText().toCharArray()[0];
        }
        return ((Character) property_value).charValue();
    }

    public byte[] getProperty_valueASBytes() throws Exception {
        if (property_value instanceof String) {
            return ((String)property_value).getBytes();
        }

        return (byte[])property_value;
    }

    public double getProperty_valueASDouble() throws Exception {
        if (property_value instanceof String) {
            return Double.parseDouble((String)property_value);
        } else if (property_value instanceof JTextField) {
            return Double.parseDouble(((JTextField)property_value).getText());
        }

        return ((Double) property_value).doubleValue();
    }

    public float getProperty_valueASFloat() throws Exception {
        if (property_value instanceof String) {
            return Float.parseFloat((String)property_value);
        } else if (property_value instanceof JTextField) {
            return Float.parseFloat(((JTextField)property_value).getText());
        }

        return ((Float) property_value).floatValue();
    }

    public int getProperty_valueASInt() throws Exception {

        //Integerオブジェクト以外に、Stringオブジェクト+パース成功でも許す
        if (property_value instanceof String) {

            return Integer.parseInt((String)property_value);
        } else if (property_value instanceof JTextField) {
            return Integer.parseInt(((JTextField)property_value).getText());
        }

        return ((Integer) property_value).intValue();
    }

    public long getProperty_valueASLong() throws Exception {
        if (property_value instanceof String) {
            return Long.parseLong((String)property_value);
        } else if (property_value instanceof JTextField) {
            return Long.parseLong(((JTextField)property_value).getText());
        }
        return ((Long) property_value).longValue();
    }

    public Object getProperty_valueASObject() throws Exception {
        return ((Object) property_value);
    }

    public short getProperty_valueASShort() throws Exception {
        if (property_value instanceof String) {
            return Short.parseShort((String)property_value);
        } else if (property_value instanceof JTextField) {
            return Short.parseShort(((JTextField)property_value).getText());
        }
        return ((Short) property_value).shortValue();
    }

    public String getProperty_valueASString() throws Exception {

        if (property_value instanceof JTextArea) {
           return ((JTextArea) property_value).getText();
        } else
        if (property_value instanceof JTextField) {
           return ((JTextField) property_value).getText();
        } else {
           return ((String) property_value).toString();
        }
    }

    /**
     * @param property_value the property_value to set
     */
    public void setProperty_value(Object property_value) {
        this.property_value = property_value;
    }

    public void selfValidate() throws QBrowserPropertyException {

           //KEY名が空白なものは、空白エントリとみなす。
           if ((key == null) || (key.length() == 0))
           return;

           if (property_type == null) {
               throw new QBrowserPropertyException("Q0001" + QBrowserV2.MAGIC_SEPARATOR + key + QBrowserV2.MAGIC_SEPARATOR + property_type +  QBrowserV2.MAGIC_SEPARATOR + property_value );
           }

           if (property_value == null) {
               throw new QBrowserPropertyException("Q0002" + QBrowserV2.MAGIC_SEPARATOR + key + QBrowserV2.MAGIC_SEPARATOR + property_type +  QBrowserV2.MAGIC_SEPARATOR + property_value );
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
                       throw new QBrowserPropertyException("Q0002" + QBrowserV2.MAGIC_SEPARATOR + key + QBrowserV2.MAGIC_SEPARATOR + property_type +  QBrowserV2.MAGIC_SEPARATOR + property_value_fordisplay );
                   }
                   validated_type = STRING_TYPE_INT;
               } catch (Exception cce) {

                   throw new QBrowserPropertyException("Q0010" + QBrowserV2.MAGIC_SEPARATOR + key + QBrowserV2.MAGIC_SEPARATOR + property_type +  QBrowserV2.MAGIC_SEPARATOR + property_value_fordisplay );
               }
           } else if (this.property_type.equals(Property.INT_TYPE)) {
               try {
                   getProperty_valueASInt();
                   validated_type = INT_TYPE_INT;
               } catch (Exception cce) {
                   throw new QBrowserPropertyException("Q0011" + QBrowserV2.MAGIC_SEPARATOR + key + QBrowserV2.MAGIC_SEPARATOR + property_type +  QBrowserV2.MAGIC_SEPARATOR + property_value_fordisplay );
               }
           } else if (this.property_type.equals(Property.BOOLEAN_TYPE)) {
               try {
                   if (property_value instanceof String) {
                       String pvs = (String)property_value;
                       if (pvs.equalsIgnoreCase("true") || pvs.equalsIgnoreCase("false")) {
                           //OK
                       } else {
                           throw new QBrowserPropertyException("Q0012" + QBrowserV2.MAGIC_SEPARATOR + key + QBrowserV2.MAGIC_SEPARATOR + property_type + QBrowserV2.MAGIC_SEPARATOR + property_value_fordisplay );
                       }
                   } else if (property_value instanceof JComboBox) {
                       //OK 規定でしかもtrue/falseしかないので
                   }
                   validated_type = BOOLEAN_TYPE_INT;
               } catch (Exception cce) {
                   throw new QBrowserPropertyException("Q0012"+ QBrowserV2.MAGIC_SEPARATOR + key + QBrowserV2.MAGIC_SEPARATOR + property_type +  QBrowserV2.MAGIC_SEPARATOR + property_value_fordisplay );
               }
           } else if (this.property_type.equals(Property.BYTE_TYPE)) {
               try {
                   getProperty_valueASByte();
                   validated_type = BYTE_TYPE_INT;
               } catch (Exception cce) {
                   throw new QBrowserPropertyException("Q0013" + QBrowserV2.MAGIC_SEPARATOR + key + QBrowserV2.MAGIC_SEPARATOR + property_type +  QBrowserV2.MAGIC_SEPARATOR + property_value_fordisplay );
               }
           } else if (this.property_type.equals(Property.BYTES_TYPE)) {
               try {
                   
                   String filepath = getProperty_valueASString();
                   File ifile = new File(filepath);
                   if (!ifile.exists()) {
                       throw new QBrowserPropertyException("Q0022" + QBrowserV2.MAGIC_SEPARATOR + key + QBrowserV2.MAGIC_SEPARATOR + property_type + QBrowserV2.MAGIC_SEPARATOR + property_value_fordisplay );
                   } else if (!ifile.isFile()) {
                       throw new QBrowserPropertyException("Q0023" + QBrowserV2.MAGIC_SEPARATOR + key + QBrowserV2.MAGIC_SEPARATOR + property_type + QBrowserV2.MAGIC_SEPARATOR + property_value_fordisplay );
                   }
                   validated_type = BYTES_TYPE_INT;
               } catch (Exception cce) {
                   if (cce.getMessage().startsWith("Q0")) {
                     throw new QBrowserPropertyException(cce.getMessage());
                   } else {
                     throw new QBrowserPropertyException("Q0018"+ QBrowserV2.MAGIC_SEPARATOR + key + QBrowserV2.MAGIC_SEPARATOR + property_type +  QBrowserV2.MAGIC_SEPARATOR + property_value_fordisplay );
                   }
               }
           } else if (this.property_type.equals(Property.DOUBLE_TYPE)) {
               try {
                   getProperty_valueASDouble();
                   validated_type = DOUBLE_TYPE_INT;
               } catch (Exception cce) {
                   throw new QBrowserPropertyException("Q0014"+ QBrowserV2.MAGIC_SEPARATOR + key + QBrowserV2.MAGIC_SEPARATOR + property_type +  QBrowserV2.MAGIC_SEPARATOR + property_value_fordisplay );
               }
           } else if (this.property_type.equals(Property.FLOAT_TYPE)) {
               try {
                   this.getProperty_valueASFloat();
                   validated_type = FLOAT_TYPE_INT;
               } catch (Exception cce) {
                   throw new QBrowserPropertyException("Q0015"+ QBrowserV2.MAGIC_SEPARATOR + key + QBrowserV2.MAGIC_SEPARATOR + property_type +  QBrowserV2.MAGIC_SEPARATOR + property_value_fordisplay );
               }
           } else if (this.property_type.equals(Property.LONG_TYPE)) {
               try {
                   getProperty_valueASLong();
                   validated_type = LONG_TYPE_INT;
               } catch (Exception cce) {
                   throw new QBrowserPropertyException("Q0016"+ QBrowserV2.MAGIC_SEPARATOR + key + QBrowserV2.MAGIC_SEPARATOR + property_type + QBrowserV2.MAGIC_SEPARATOR + property_value_fordisplay );
               }
           } else if (this.property_type.equals(Property.SHORT_TYPE)) {
               try {
                   
                   getProperty_valueASShort();
                   validated_type = SHORT_TYPE_INT;
               } catch (Exception cce) {
                   throw new QBrowserPropertyException("Q0017" + QBrowserV2.MAGIC_SEPARATOR + key + QBrowserV2.MAGIC_SEPARATOR + property_type +  QBrowserV2.MAGIC_SEPARATOR + property_value_fordisplay );
               }
           } else if (this.property_type.equals(Property.CHARACTER_TYPE)) {
               try {

                   getProperty_valueASCharacter();
                   validated_type = CHARACTER_TYPE_INT;
               } catch (Exception cce) {
                   throw new QBrowserPropertyException("Q0024" + QBrowserV2.MAGIC_SEPARATOR + key + QBrowserV2.MAGIC_SEPARATOR + property_type +  QBrowserV2.MAGIC_SEPARATOR + property_value_fordisplay );
               }
           } else {
               throw new QBrowserPropertyException("Q0003" + QBrowserV2.MAGIC_SEPARATOR + key + QBrowserV2.MAGIC_SEPARATOR + property_type +  QBrowserV2.MAGIC_SEPARATOR + property_value_fordisplay );
           }

    }

        public void autoComplementTypeNme() {
            //OBJECTからタイプを自動判定する
            if (property_value != null) {


                if (property_value instanceof JTextArea) {

                    property_type = STRING_TYPE;

                } else
                if (property_value instanceof String) {

                    property_type = STRING_TYPE;

                } else if (property_value instanceof Integer) {

                    property_type = INT_TYPE;

                } else if (property_value instanceof Boolean) {

                    property_type = BOOLEAN_TYPE;

                } else if (property_value instanceof JComboBox) {

                    property_type = BOOLEAN_TYPE;

                } else if (property_value instanceof Byte) {

                    property_type = BYTE_TYPE;

                } else if (property_value instanceof Double) {

                    property_type = DOUBLE_TYPE;

                } else if (property_value instanceof Float) {

                    property_type = FLOAT_TYPE;

                } else if (property_value instanceof Long) {

                    property_type = LONG_TYPE;

                } else if (property_value instanceof Short) {

                    property_type = SHORT_TYPE;

                } else if (property_value instanceof byte[]) {

                    property_type = BYTES_TYPE;

                }

                

            } 
        }

}
