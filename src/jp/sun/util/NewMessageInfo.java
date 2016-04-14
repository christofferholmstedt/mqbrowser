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

import com.qbrowser.property.Property;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
/*
 * NewMessageInfo.java
 *
 * Created on 2007/08/24, 16:04
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author Administrator
 */
public class NewMessageInfo {

    private ArrayList headerinfos = null;
    private ArrayList userproperties = null;
    private String  dest = null;
    private String  dest_type = null;
    private String  body_inputtype = null;
    private String  body_text = null;
    private File    body_file = null;
    private byte[]  body_bytes = null;
    private int    soufukosu = 1;


    /** Creates a new instance of NewMessageInfo */
    public NewMessageInfo() {

        headerinfos = new ArrayList();
        userproperties = new ArrayList();

    }

    public ArrayList getHeaderinfos() {
        return headerinfos;
    }

    public void setHeaderinfos(ArrayList headerinfos) {
        this.headerinfos = headerinfos;
    }

    public ArrayList getUserproperties() {
        return userproperties;
    }

    public void setUserproperties(ArrayList userproperties) {
        this.userproperties = userproperties;
    }

    public void addUserproperty(Property prop) {
        userproperties.add(prop);
    }

    public void addHeaderinfo(Property prop) {
        headerinfos.add(prop);
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public String getDest_type() {
        return dest_type;
    }

    public void setDest_type(String dest_type) {
        this.dest_type = dest_type;
    }

    public String getBody_text() {
        return body_text;
    }

    public void setBody_text(String body_text) {
        this.body_text = body_text;
    }

    public File getBody_file() {
        return body_file;
    }

    public void setBody_file(File body_file) {
        this.body_file = body_file;
    }

    public String getBody_inputtype() {
        return body_inputtype;
    }

    public void setBody_inputtype(String body_inputtype) {
        this.body_inputtype = body_inputtype;
    }

    public int getSoufukosu() {
        return soufukosu;
    }

    public void setSoufukosu(int soufukosu) {
        this.soufukosu = soufukosu;
    }

    /**
     * @return the body_bytes
     */
    public byte[] getBody_bytes() {
        return body_bytes;
    }

    /**
     * @param body_bytes the body_bytes to set
     */
    public void setBody_bytes(byte[] body_bytes) {
        this.body_bytes = body_bytes;
    }

}
