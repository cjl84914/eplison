package com.geetion.epsilon.core;

import com.geetion.epsilon.core.handler.SogouTrainHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EpsilonServerApplicationTests {


    @Autowired
    private SogouTrainHandler sogouTrainHandler;


    @Test
    public void test2() {
        sogouTrainHandler.execute("");
    }

}
