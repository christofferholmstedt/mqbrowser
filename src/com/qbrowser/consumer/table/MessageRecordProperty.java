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

package com.qbrowser.consumer.table;

import javax.swing.JButton;

/**
 *
 * @author takemura
 */
public class MessageRecordProperty {
    private String destName;
    private String consumerThreadStatus;
    private int count;
    private JButton button;

    /**
     * @return the destName
     */
    public String getDestName() {
        return destName;
    }

    /**
     * @param destName the destName to set
     */
    public void setDestName(String destName) {
        this.destName = destName;
    }

    /**
     * @return the consumerThreadStatus
     */
    public String getConsumerThreadStatus() {
        return consumerThreadStatus;
    }

    /**
     * @param consumerThreadStatus the consumerThreadStatus to set
     */
    public void setConsumerThreadStatus(String consumerThreadStatus) {
        this.consumerThreadStatus = consumerThreadStatus;
    }

    /**
     * @return the count
     */
    public int getCount() {
        return count;
    }

    /**
     * @param count the count to set
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * @return the button
     */
    public JButton getButton() {
        return button;
    }

    /**
     * @param button the button to set
     */
    public void setButton(JButton button) {
        this.button = button;
    }

}
