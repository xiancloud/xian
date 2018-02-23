/*
 * Copyright 2013-2014, ApiFest project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.apifest.oauth20.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class that generates random strings.
 *
 * @author Rossitsa Borissova
 * @deprecated 考虑使用xianframe内的random util
 */
public final class RandomGenerator {

    private static final String SALT = "apifestrulez";

    private static char[] charsSymbols = new char[56];
    private static char[] charsDigits = new char[16];
    private static char[] digits = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 0 };

    private static Logger log = LoggerFactory.getLogger(RandomGenerator.class);

    static {
        for (int idx = 0; idx < 26; ++idx) {
            charsSymbols[idx] = (char) ('a' + idx);
        }
        for (int idx = 0; idx < 26; ++idx) {
            charsSymbols[idx + 26] = (char) ('A' + idx);
        }
        charsSymbols[52] = ('_');
        charsSymbols[53] = ('-');
        charsSymbols[54] = ('#');
        charsSymbols[55] = ('=');

        for (int idx = 0; idx < 6; ++idx) {
            charsDigits[idx] = (char) ('a' + idx);
        }
        for (int idx = 6; idx < 16; ++idx) {
            charsDigits[idx] = (char) ('0' + idx - 6);
        }
    }

    /**
     * Generates random string that contains chars (a-z, A-Z) and some symbols(_,-,#,=).
     *
     * @param lenght the length of the generated string
     * @return random string
     */
    public static String generateCharsSymbolsString(int lenght) {
        StringBuffer buf = new StringBuffer(lenght);
        SecureRandom rand = new SecureRandom();
        for (int i = 0; i < lenght; i++) {
            buf.append(charsSymbols[rand.nextInt(charsSymbols.length)]);
        }
        return buf.toString();
    }

    /**
     * Generates random string.
     *
     * @return random string
     */
    public static String generateRandomString() {
        return generateRandomString("SHA-256");
    }

    /**
     * Generates short random string.
     *
     * @return random string
     */
    public static String generateShortRandomString() {
        return generateRandomString("SHA-1");
    }

    private static String generateRandomString(String algorithm) {
        SecureRandom rand = new SecureRandom();
        int random = rand.nextInt();
        long time = System.currentTimeMillis();
        long id = Thread.currentThread().getId();
        MessageDigest md = null;
        String result = null;
        try {
            md = MessageDigest.getInstance(algorithm);
            String input = random + time + id + SALT;
            byte [] hashed = md.digest(input.getBytes("UTF-8"));
            result = new BigInteger(1, hashed).toString(16);
        } catch(NoSuchAlgorithmException e) {
            log.error("cannot generate random string", e);
        } catch (UnsupportedEncodingException e) {
            log.error("cannot generate random string", e);
        }
        return result;
    }

    /**
     * Generates random string that contains digits only.
     *
     * @param length the length of the generated string
     * @return random string
     */
    public static String generateDigitsString(int length) {
        StringBuffer buf = new StringBuffer(length);
        SecureRandom rand = new SecureRandom();
        for (int i = 0; i < length; i++) {
            buf.append(rand.nextInt(digits.length));
        }
        return buf.toString();
    }

}
