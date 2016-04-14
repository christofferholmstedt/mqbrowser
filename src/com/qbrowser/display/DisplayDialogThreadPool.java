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

package com.qbrowser.display;

import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author takemura
 */
public class DisplayDialogThreadPool {

    static HashMap display_threads = new HashMap();

    public static void addDisplayThread(Object runner, Object thread) {
        if (runner != null && thread != null) {
            display_threads.put(runner, thread);
        }
    }

    public static void removeDisplayThread(Object runner) {
        if (runner != null) {
            display_threads.remove(runner);
        }
    }

    public static void cleanupDisplayThreads() {

     Iterator ids = display_threads.values().iterator();
     while (ids.hasNext()) {
         Thread thethread = (Thread)ids.next();
            try {

                thethread.stop();
            } catch (Throwable thex) {
                //NOP
                //thex.printStackTrace();
            }
     }

    }

}
