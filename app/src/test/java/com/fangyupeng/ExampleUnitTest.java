package com.fangyupeng;

import org.junit.Test;



/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    private static volatile boolean isOK = false;
    @Test
    public void addition_isCorrect() throws InterruptedException {
        //assertEquals(4, 2 + 2);

        new Thread(() -> {
            System.out.println("1111" + Thread.currentThread());
            System.out.println("1111" + Thread.currentThread().getName());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            isOK = true;
        }).start();


        while (!isOK) {
            Thread.sleep(100);
            System.out.println("sleep");
        }
        System.out.println("222"+ Thread.currentThread());
        System.out.println("222"+ Thread.currentThread().getName());

    }

}