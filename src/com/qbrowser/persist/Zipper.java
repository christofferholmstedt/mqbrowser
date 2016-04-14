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

/**
 *
 * @author Administrator
 */
import java.awt.TextArea;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.*;
import java.util.zip.CRC32;

/**
 *
 * @author Administrator
 */
public class Zipper {

    private ArrayList alltargetdirs = new ArrayList();
    private HashSet alljyogai = new HashSet();


	/**
	 * 何もしないOutputStream。
	 * ファイルの圧縮後のサイズを調べるために使う。
	 */
	private class IdleOutputStream extends OutputStream {
		/*
		 * @see java.io.OutputStream#write(int)
		 */
		public void write(int b) throws IOException {
		}
	}

    /** Creates a new instance of Main */
    public Zipper() {
    }



void getAllTargetFile(File rootdir) {
    File[] files = rootdir.listFiles();
    for (int i = 0; i < files.length; i++) {

        alltargetdirs.add(files[i]);
        if (files[i].isDirectory()) {
            processDirectory(files[i]);
        }
    }
}

void processDirectory(File dir) {
    File[] files = dir.listFiles();
    for(int i = 0 ; i < files.length; i++) {
        alltargetdirs.add(files[i]);
        if (files[i].isDirectory()) {
            processDirectory(files[i]);
        }
    }
}


String extractRelativeZipPath(String original, String exclude) {

    //System.out.println("exclude:" + exclude);
    //System.out.println("original:" + original);
    int i = original.indexOf(exclude);
    //System.out.println("index:" + i);
    if (i != -1) {
        return original.substring(i + exclude.length() + 1);
    } else {
        return original;
    }
}

String convertZipEntrySeparator(String input) {
    StringBuffer result = new StringBuffer();
    char[] inputc = input.toCharArray();
    for (int i = 0 ; i < inputc.length ; i++) {

        if (inputc[i] == '\\') {
            result.append('/');
        } else {
            result.append(inputc[i]);
        }

    }

    return result.toString();
}

	/** 圧縮レベル (0～9) */
	private static final int COMPRESS_LEVEL = 0;

	/**
	 * ファイルをzipファイルに圧縮する。
	 * @param files 圧縮されるファイル
	 * @param zipFile zipファイル
	 * @throws IOException ファイル入出力エラー
	 */
	public String zipForDir(File jarnameDir, File zipFile, boolean printProgress, String extension) throws IOException {

            alltargetdirs.clear();
            getAllTargetFile(jarnameDir);

            String zipnonamaehakorenisubeshi = jarnameDir.getName() + extension;

		ZipOutputStream output =
			new ZipOutputStream(new FileOutputStream(zipFile));
		//output.setLevel(COMPRESS_LEVEL);

                int allsize = alltargetdirs.size();

                for (int i = 0; i < allsize ; i++) {
                    File tfile = (File)alltargetdirs.get(i);

                    //親フォルダと同じ名前の.jarファイルは、このフォルダの元ネタなのでzipにいれない
                    //名前を一回保存した後に、deleteでいいや。
                    //それで作成するjarはoya直下に出す


                    //親フォルダ名称チェック
                    String directparent = tfile.getParentFile().getName();
                    //System.out.println("直親:" + directparent);



                        if (tfile.isDirectory()) {

                            //String fn = extractRelativeZipPath(tfile.getAbsolutePath(), jarnameDir.getAbsolutePath());
                            //ZipEntry entry = new ZipEntry(fn + "/");
                            //output.putNextEntry(entry);

                        } else {

                            writeEntry(tfile, output, jarnameDir);

                        }


                    if (printProgress) {
                        System.out.println("Zip中・・・(" + i + "/" + allsize + ")");
                    }

		}

                output.finish();
		        output.close();
                //System.out.println("ZIPファイル ： " + zipnonamaehakorenisubeshi + "の作成が完了しました。");
                alltargetdirs.clear();
                return zipnonamaehakorenisubeshi;
	}

	/**
	 * ファイルとその階層を指定し、ZipOutputStreamにファイルを追加する。
	 * @param file ファイル
	 * @param output ZipOutputStream
	 * @param depth ファイルの階層
	 * @throws IOException ファイル入出力エラー
	 */
	private void writeEntry(File file, ZipOutputStream output, File oya)
		throws IOException {

			BufferedInputStream input =
				new BufferedInputStream(new FileInputStream(file));

                        String fn = extractRelativeZipPath(file.getAbsolutePath(), oya.getAbsolutePath());

			ZipEntry entry = new ZipEntry(this.convertZipEntrySeparator(fn));
			//entry.setCompressedSize(getCompressedSize(file));
			//entry.setCrc(getCRCValue(file));
			//entry.setMethod(ZipEntry.STORED);
			//entry.setSize(file.length());
			//entry.setTime(file.lastModified());
			output.putNextEntry(entry);



			int b;
			while ((b = input.read()) != -1) {
				output.write(b);
			}
			input.close();
			output.closeEntry();

	}

	/**
	 * ファイルのCRC-32チェックサムを取得する。
	 * @param file ファイル
	 * @return CRC-32チェックサム
	 * @throws IOException ファイル入出力エラー
	 */
	private long getCRCValue(File file) throws IOException {
		CRC32 crc = new CRC32();

		BufferedInputStream input =
			new BufferedInputStream(new FileInputStream(file));
		int b;
		while ((b = input.read()) != -1) {
			crc.update(b);
		}
		input.close();
		return crc.getValue();
	}

	/**
	 * ファイルの圧縮後のサイズを調べる。
	 * @param file ファイル
	 * @return 圧縮後のサイズ
	 * @throws IOException 入出力エラー
	 */
	private long getCompressedSize(File file) throws IOException {
		ZipEntry entry = new ZipEntry(file.getName());
		entry.setMethod(ZipEntry.DEFLATED);
		ZipOutputStream out = new ZipOutputStream(new IdleOutputStream());
		out.setLevel(COMPRESS_LEVEL);
		out.putNextEntry(entry);
		BufferedInputStream input =
			new BufferedInputStream(new FileInputStream(file));
		int b;
		while ((b = input.read()) != -1) {
			out.write(b);
		}
		input.close();
		out.closeEntry();
		out.close();
		return entry.getCompressedSize();
	}






}