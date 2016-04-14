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

package com.qbrowser.localstore;

import com.qbrowser.QBrowserV2;
import com.qbrowser.util.QBrowserUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author takemura
 */
public class LocalStoreDef {


    HashMap local_store_properties_map = new HashMap();
   

    public LocalStoreDef() {}

    public Collection<LocalStoreProperty> getAllLocalStoreProperties() {
        return local_store_properties_map.values();
    }

    public LocalStoreProperty getLocalStoreProperty(String destName) {
        return (LocalStoreProperty)local_store_properties_map.get(destName);
    }

    public void removeLocalStoreProperty(String destName) {
        local_store_properties_map.remove(destName);
    }

    public void setLocalStoreProperty(LocalStoreProperty lsp) {
        local_store_properties_map.put(lsp.getDestName(), lsp);
    }

    public void save(File target) {
        Collection localstoredefs = local_store_properties_map.values();
        Iterator ilocalstoredefs = localstoredefs.iterator();
        PrintWriter pwr = null;

        try {

            pwr = new PrintWriter(new FileWriter(target));

            while (ilocalstoredefs.hasNext()) {
                LocalStoreProperty lsp = (LocalStoreProperty) ilocalstoredefs.next();
                pwr.print(lsp.getDestName() + QBrowserV2.MAGIC_SEPARATOR + lsp.getReal_file_directory()
                        + QBrowserV2.MAGIC_SEPARATOR + lsp.isValid() + QBrowserV2.MAGIC_SEPARATOR);

                Iterator ihm = lsp.getFromDests().entrySet().iterator();
                boolean isFirst = true;
                while (ihm.hasNext()) {
                    if (isFirst) {
                        isFirst = false;
                    } else {
                        pwr.print(",");
                    }
                    Map.Entry me = (Map.Entry) ihm.next();
                    String name1 = (String)me.getKey();
                    String sep1 = (String)me.getValue();
                    pwr.print(name1 + "(" + sep1 + ")");
                }

                pwr.print(QBrowserV2.MAGIC_SEPARATOR);

                Iterator ihm2 = lsp.getToDests().entrySet().iterator();
                boolean isFirst2 = true;
                while (ihm2.hasNext()) {
                    if (isFirst2) {
                        isFirst2 = false;
                    } else {
                        pwr.print(",");
                    }
                    Map.Entry me = (Map.Entry) ihm2.next();
                    String name2 = (String)me.getKey();
                    String sep2 = (String)me.getValue();
                    pwr.print(name2 + "(" + sep2 + ")");
                }

                pwr.println();

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (pwr != null) {
                pwr.close();
                pwr = null;
            }
        }
    }

    public void setdestName_LocalSubscriptionDest(HashMap target, LocalStoreProperty lsp) {
        String local_dest = lsp.getDestName();

        //local_destにセットされた購読先（この人たちにメッセージがselector条件で着信するとlocal_destにコピー）
        HashMap fds = lsp.getFromDests();
        Iterator ifds = fds.entrySet().iterator();
        while (ifds.hasNext()) {
            Map.Entry me = (Map.Entry)ifds.next();
            String subscriptionDest = (String)me.getKey();
            HashMap local_and_selector = (HashMap)target.get(subscriptionDest);
            if (local_and_selector == null) {
                local_and_selector = new HashMap();
            }
            String each_selector = (String)me.getValue();
            local_and_selector.put(local_dest, each_selector);

            target.put(subscriptionDest, local_and_selector);
        }
    }

    public void readAndParseLocalStoreDefFile(File ifile , HashMap destName_LocalSubscriptionDest) {
        
        BufferedReader br = null;
        
        try {

          br = new BufferedReader(new FileReader(ifile));
          String line = null;

          while ((line = br.readLine()) != null) {
              LocalStoreProperty lsp = new LocalStoreProperty();
              ArrayList ar = QBrowserUtil.parseDelimitedString(line, QBrowserV2.MAGIC_SEPARATOR);
              for (int i = 0 ; i < ar.size() ; i++) {

                  

                  switch (i) {
                      case 0: //ローカルストア名
                          lsp.setDestName((String)ar.get(i));
                          break;
                      case 1: //リアルファイルパス
                          lsp.setReal_file_directory((String)ar.get(i));
                          break;
                      case 2: //有効フラグ
                          String flag = (String)ar.get(i);
                          if ((flag != null) && (flag.equals("false"))) {
                              lsp.setValid(false);
                          } else {
                              lsp.setValid(true);
                          }
                          break;
                      case 3: //自動レコード対象宛先とセレクタ
                          ArrayList fromDests = QBrowserUtil.parseDelimitedString((String)ar.get(i), ",");
                          for (int j = 0 ; j < fromDests.size(); j++) {
                              String token = (String)fromDests.get(j);
                              parseDestString(token, lsp.getFromDests());
                          }
                          break;
                      case 4: //自動転送対象宛先とセレクタ
                          ArrayList toDests = QBrowserUtil.parseDelimitedString((String)ar.get(i), ",");
                          for (int j = 0 ; j < toDests.size(); j++) {
                              String token = (String)toDests.get(j);
                              parseDestString(token, lsp.getToDests());
                          }
                          break;
                  }

                  
              }

              setdestName_LocalSubscriptionDest(destName_LocalSubscriptionDest, lsp);
              local_store_properties_map.put(lsp.getDestName(),lsp);

          }

        } catch (Exception ie) {

        } finally {
            if (br != null) {
                try {
                  br.close();
                } catch (Exception iie) {}
                br = null;
            }
        }



    }

    void parseDestString(String orig, HashMap hm) {
        if (orig == null || hm == null) return;

        //右括弧の場所を検出
        int rightp = orig.indexOf("(");
        int leftp = orig.indexOf(")");

        if (rightp == -1 || leftp == -1) {
            return;
        }

        if (leftp - rightp < 0) {
            return;
        }

        String destName = orig.substring(0, rightp);
        String selector = orig.substring(rightp + 1, leftp);

        hm.put(destName, selector);

        return;
    }

}
