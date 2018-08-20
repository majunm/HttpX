package proxy;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class IProxy<T> implements InvocationHandler {
    public T delegate;

    public static IProxy of() {
        return new IProxy();
    }

    /**
     * 绑定委托对象并返回一个代理类
     *
     * @param delegate
     * @return
     */
    public T bind(T delegate) {
        this.delegate = delegate;
        return (T) Proxy.newProxyInstance(delegate.getClass().getClassLoader(), delegate.getClass().getInterfaces(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (checkParams()) {
            start(args);
            Object result = method.invoke(delegate, args);
            end(args);
            return result;
        } else {
            return null;
        }
    }

    public boolean checkParams() {
        return true;
    }

    public void start(Object[] args) {
        //System.out.println("开始" + args);
    }

    public void end(Object[] args) {
        //System.out.println("结束" + args);
    }
}

