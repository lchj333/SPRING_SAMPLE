package com.spring.sample;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.spring.sample.config.DispatcherConfig;

@ExtendWith(SpringExtension.class)
//@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DispatcherConfig.class)
public class DispatcherConfigTest {
	
	@Autowired
	private Sample sample;
	
	@Test
	void rootContextComponentScanTest() {
		//설정 주입 되었는지 테스트
		assertNotNull(sample);
	}
}
