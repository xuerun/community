package com.rxue;

import com.rxue.service.AlphaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author RunXue
 * @create 2022-03-24 16:03
 * @Description
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = NewCoderApplication.class)
public class TransactionTests {
    @Autowired
    private AlphaService alphaService;

    @Test
    public void testSave1(){
        Object o = alphaService.save1();
        System.out.println(o);
    }

    @Test
    public void testSave2(){
        Object o = alphaService.save2();
        System.out.println(o);
    }
}
