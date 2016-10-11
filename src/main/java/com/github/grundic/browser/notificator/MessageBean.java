/*
 * The MIT License
 *
 * Copyright (c) 2016 Grigory Chernyshev.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.github.grundic.browser.notificator;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * User: g.chernyshev
 * Date: 09/10/16
 * Time: 16:10
 */
public class MessageBean {
    public String title;
    public String body;
    public String tag;
    public String icon;

    public void md5Tag(){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            String hash = this.title + this.body;
            md.update(hash.getBytes());
            byte[] enc = md.digest();

            this.tag = new sun.misc.BASE64Encoder().encode(enc);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            this.tag = this.title + this.body;
        }
    }
}
