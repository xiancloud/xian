package info.xiancloud.core.util.io;
/*
 * $RCSfile: StringIO.java,v $
 *
 * Copyright (c) 2007 Sun Microsystems, Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistribution of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in
 *   the documentation and/or other materials provided with the
 *   distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF
 * USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR
 * ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed, licensed or
 * intended for use in the design, construction, operation or
 * maintenance of any nuclear facility.
 *
 * $Revision: 1.4 $
 * $Date: 2007/02/09 17:20:42 $
 * $State: Exp $
 */

import java.io.*;
import java.net.URL;

/**
 * Utility class with static methods to read the entire contents of a
 * file, URL, InputStream, or Reader into a single String that is
 * returned to the user.
 *
 * @since Java 3D 1.4
 */
public class StringIO {
    /**
     * Read the entire contents of the specified file and return a
     * single String object containing the contents of the file.
     *
     * @param fileName the name of the file from which to read
     *
     * @return a String containing the contents of the input file
     *
     * @throws IOException if the specified file cannot be opened, or
     * if an I/O error occurs while reading the file
     */
    public static String readFully(String fileName) throws IOException {
        return readFully(new File(fileName));
    }

    /**
     * Read the entire contents of the specified file and return a
     * single String object containing the contents of the file.
     * This method does not return until the end of the input file
     * is reached.
     *
     * @param file a File from which to read
     *
     * @return a String containing the contents of the input file
     *
     * @throws IOException if the specified file cannot be opened, or
     * if an I/O error occurs while reading the file
     */
    public static String readFully(File file) throws IOException {
        return readFully(new FileReader(file));
    }

    /**
     * Read the entire contents of the specified URL and return a
     * single String object containing the contents of the URL.
     * This method does not return until an end of stream is reached
     * for the URL.
     *
     * @param url a URL from which to read
     *
     * @return a String containing the contents of the input URL
     *
     * @throws IOException if the specified URL cannot be opened, or
     * if an I/O error occurs while reading the URL
     */
    public static String readFully(URL url) throws IOException {
        return readFully(url.openStream());
    }

    /**
     * Read the entire contents of the specified InputStream and return a
     * single String object containing the contents of the InputStream.
     * This method does not return until the end of the input
     * stream is reached.
     *
     * @param stream an InputStream from which to read
     *
     * @return a String containing the contents of the input stream
     *
     * @throws IOException if an I/O error occurs while reading the input stream
     */
    public static String readFully(InputStream stream) throws IOException {
        return readFully(new InputStreamReader(stream));
    }

    /**
     * Read the entire contents of the specified Reader and return a
     * single String object containing the contents of the InputStream.
     * This method does not return until the end of the input file or
     * stream is reached.
     *
     * @param reader a Reader from which to read
     *
     * @return a String containing the contents of the stream
     *
     * @throws IOException if an I/O error occurs while reading the input stream
     */
    public static String readFully(Reader reader) throws IOException {
        char[] arr = new char[8*1024]; // 8K at a time
        StringBuilder buf = new StringBuilder();
        int numChars;

        while ((numChars = reader.read(arr, 0, arr.length)) > 0) {
            buf.append(arr, 0, numChars);
        }

        return buf.toString();
    }


    /**
     * Do not construct an instance of this class.
     */
    private StringIO() {
    }
}
