package com.tkzou.middleware.springframework.test.aop;

/**
 * @author zoutongkun
 */
public class WorldServiceImpl implements WorldService {

	@Override
	public void explode() {
		System.out.println("The Earth is going to explode");
	}
}
