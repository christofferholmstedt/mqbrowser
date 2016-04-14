/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.qbrowser.key;

/**
 *
 * @author takemura
 */
public final class OSDetector
{

    public static final boolean windows = -1 != System.getProperty("os.name").toLowerCase().indexOf("windows");
    public static final boolean linux = -1 != System.getProperty("os.name").toLowerCase().indexOf("linux");
    public static final boolean mac = -1 != System.getProperty("os.name").toLowerCase().indexOf("mac");

    public OSDetector()
    {
    }

    public static boolean isMac()
    {
        return mac;
    }

    public static boolean isLinux()
    {
        return linux;
    }

    public static boolean isWindows()
    {
        return windows;
    }

}
