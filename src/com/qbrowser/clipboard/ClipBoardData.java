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

package com.qbrowser.clipboard;

import com.qbrowser.container.MessageContainer;
import com.qbrowser.localstore.LocalMessageContainer;
import java.util.ArrayList;

/**
 *
 * @author takemura
 */
public class ClipBoardData {

    private ArrayList<MessageContainer> messageContainers = new ArrayList();
    private ArrayList<LocalMessageContainer> localMessageContainers = new ArrayList();

    public void addMessageContainer(MessageContainer mc) {
        messageContainers.add(mc);
    }

    public void addLocalMessageContainer(LocalMessageContainer lmc) {
        localMessageContainers.add(lmc);
    }

    public int getCountOfMessageContainer() {
        return messageContainers.size();
    }

    public int getCountOfLocalMessageContainer() {
        return localMessageContainers.size();
    }

    /**
     * @return the messageContainers
     */
    public ArrayList<MessageContainer> getMessageContainers() {
        return messageContainers;
    }

    /**
     * @return the localMessageContainers
     */
    public ArrayList<LocalMessageContainer> getLocalMessageContainers() {
        return localMessageContainers;
    }

}
