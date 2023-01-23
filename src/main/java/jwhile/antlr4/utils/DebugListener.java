package jwhile.antlr4.utils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

import jwhile.antlr4.generated.WhileBaseListener;
import jwhile.antlr4.generated.WhileListener;

public class DebugListener implements InvocationHandler {

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		System.out.println("" + method.getName() + " args "+args);
		return 42;
	}

	public static WhileListener getInstance() {
		WhileListener proxyInstance = (WhileListener) Proxy.newProxyInstance(WhileListener.class.getClassLoader(),
				new Class[] { WhileListener.class }, new DebugListener());
		return proxyInstance;
	}
}
