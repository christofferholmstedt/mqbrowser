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

/**
 *
 * @author takemura
 */
public class ActiveMQPropertyUtil extends PropertyUtil {

    public static void validateJMSHeaderValueType(String key, Object value) throws QBrowserPropertyException {

            //キーがnullは、後でコレクト除外されるので無視でOK
            if (key == null)
            return;

            //キーだけあって、Valueが入っていないときはエラー
            if ((key != null) && (value == null)) {

                throw new QBrowserPropertyException("Q0005" + QBrowserV2.MAGIC_SEPARATOR + key + QBrowserV2.MAGIC_SEPARATOR + "null");


            } else if (key.equals("JMSExpiration")) {

                   testInt(key, value);

            } else if (key.equals("JMSPriority")) {

                   testInt(key, value);

            } else if (key.equals("JMSCorrelationID")) {

                   testString(key, value);

            } else if (key.equals("JMSReplyTo")) {
                   testString(key, value);
            } else if (key.equals("JMSType")) {
                   testString(key, value);
            } else if (key.equals("Message Group Sequence Number")) {
                   testInt(key, value);
            } else if (key.equals("Message Group")) {
                   testString(key, value);
            } else {
                throw new QBrowserPropertyException("Q0006"+ QBrowserV2.MAGIC_SEPARATOR + key + QBrowserV2.MAGIC_SEPARATOR + value);
            }


    }

}
