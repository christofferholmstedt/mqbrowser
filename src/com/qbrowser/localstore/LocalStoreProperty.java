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
import java.io.File;
import java.util.HashMap;

/**
 *
 * @author takemura
 */
public class LocalStoreProperty {
    private String destName;
    private String real_file_directory;
    private boolean valid;
    private HashMap fromDests = new HashMap();
    private HashMap toDests = new HashMap();
    //このローカルストアにどこから書き込まれたか、そのカウントを個別に。
    private HashMap eachCount = new HashMap();

    public void incrementEachCount(String from_dest_with_suffix) {
        Integer count = (Integer)eachCount.get(from_dest_with_suffix);
        if (count == null) {
            count = new Integer(0);
        }

        int sum = count.intValue() + 1;
        eachCount.put(from_dest_with_suffix, new Integer(sum));
    }

    public int getEachCount(String from_dest_with_suffix) {
        Integer count = (Integer)eachCount.get(from_dest_with_suffix);
        if (count == null) {
            count = new Integer(0);
        }

        return count.intValue();
    }

    @Override
    public String toString() {
        return "destName = " + destName + " real_file_directory = " + real_file_directory + " isValid = " + valid + " fromDests = " + fromDests + " toDests = " + toDests;
    }

    /**
     * @return the destName
     */
    public String getDestName() {
        return destName;
    }

    public String getDestNameWithSuffix() {
        return destName + QBrowserV2.LOCAL_STORE_SUFFIX;
    }

    public void addToDests(String destName, String selector) {
        toDests.put(destName, selector);
    }

    public void addFromDests(String destName, String selector) {
        fromDests.put(destName, selector);
    }

    public void removeFromDests(String destName) {
        fromDests.remove(destName);
    }

    public boolean doesThisLSCurrentllySubscribingTo(String targetdest_with_suffix) {
        Object obj = fromDests.get(targetdest_with_suffix);
        if (obj != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param destName the destName to set
     */
    public void setDestName(String destName) {
        this.destName = destName;
    }

    /**
     * @return the real_file_directory
     */
    public String getReal_file_directory() {
        if (real_file_directory != null) {
            if (!real_file_directory.trim().endsWith(File.separator)) {
                real_file_directory = real_file_directory + File.separator;
            }
        }
        return real_file_directory;
    }

    /**
     * @param real_file_directory the real_file_directory to set
     */
    public void setReal_file_directory(String real_file_directory) {
        this.real_file_directory = real_file_directory;
    }

    /**
     * @return the valid
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * @param valid the valid to set
     */
    public void setValid(boolean valid) {
        this.valid = valid;
    }

    /**
     * @return the fromDests
     */
    public HashMap getFromDests() {
        return fromDests;
    }

    /**
     * @param fromDests the fromDests to set
     */
    public void setFromDests(HashMap fromDests) {
        this.fromDests = fromDests;
    }

    /**
     * @return the toDests
     */
    public HashMap getToDests() {
        return toDests;
    }

    /**
     * @param toDests the toDests to set
     */
    public void setToDests(HashMap toDests) {
        this.toDests = toDests;
    }

}
