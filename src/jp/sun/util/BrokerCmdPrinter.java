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

package jp.sun.util;

import com.sun.messaging.jmq.util.MultiColumnPrinter;
import com.sun.messaging.jmq.admin.util.Globals;

public class BrokerCmdPrinter extends MultiColumnPrinter {
    
    public static StringBuffer sb;

    public BrokerCmdPrinter(int numCol, int gap, String border, int align, boolean sort) {
	super(numCol, gap, border, align, sort);
    }

    public BrokerCmdPrinter(int numCol, int gap, String border, int align) {
	super(numCol, gap, border, align);
    }

    public BrokerCmdPrinter(int numCol, int gap, String border) {
	super(numCol, gap, border);
    }

    public BrokerCmdPrinter(int numCol, int gap) {
	super(numCol, gap);
    }

    public void doPrint(String str) {
        //Globals.stdOutPrint(str);
        sb.append(str);
    }

    public void doPrintln(String str) {
        //Globals.stdOutPrintln(str);
        sb.append(str).append("\n");
    }
}
