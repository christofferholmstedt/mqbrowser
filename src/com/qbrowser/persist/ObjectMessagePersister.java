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

package com.qbrowser.persist;

import com.qbrowser.localstore.LocalMessageContainer;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import javax.jms.ObjectMessage;

/**
 *
 * @author takemura
 */
public class ObjectMessagePersister extends MessagePersister {



    public ObjectMessagePersister() {
        super();
    }

    public ObjectMessagePersister(ObjectMessage val) {
        message = val;
    }

    public ObjectMessagePersister(LocalMessageContainer vlmc) {
        lmc = vlmc;
        message = vlmc.getMessage();
    }

    @Override
    public File persistToFile() throws Exception {
        File workfileDir = super.persistToFile();



        FileOutputStream outFile = null;
        ObjectOutputStream outObject = null;


        try {

            sbuf.append(resources.getString("qkey.msg.msg259") + "\n");
            //ta.setCaretPosition(ta.getText().length());
            //FileOutputStreamオブジェクトの生成
            outFile = new FileOutputStream(workfileDir.getAbsolutePath() + File.separator + "ObjectMessageBody");
            //ObjectOutputStreamオブジェクトの生成
            outObject = new ObjectOutputStream(outFile);
            outObject.writeObject(((ObjectMessage)message).getObject());
            sbuf.append(resources.getString("qkey.msg.msg260") + "\n");
            //ta.setCaretPosition(ta.getText().length());


        } catch (Throwable thex) {
            thex.printStackTrace();
        } finally {
            if (outObject != null) {
                outObject.close();
            }

            if (outFile != null) {
                outFile.close();
            }
        }



        return workfileDir;

    }

}
