package com.eshangke.framework.test;

import android.test.InstrumentationTestCase;
import android.util.Base64;

/**
 * Created by shims on 15/12/25.
 */
public class ExampleTest extends InstrumentationTestCase {
    public void test() throws Exception {
        byte[] enc= Base64.encode("恭喜发财".getBytes(), Base64.DEFAULT);
        byte[] dec=Base64.decode(enc, Base64.DEFAULT);
        String a=new String(dec);
        System.out.println(a);

    }
}
