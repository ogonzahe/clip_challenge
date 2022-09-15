package com.clip;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class AppTest {

    @Test
    public void testNothing() {
        assertThat(true).isNotNull();
    }
}
