# Hub
a library which can avoid check null when want to invoke a implementation by interface

# About

Android 开发中通过接口获取实现类，可用于moudle之间的通信，线上环境不用每次都判断实现类是否存在直接调用，而不会出现因实现类不存在而引发的崩溃。

```java
public interface ITestApi extends IBaseHub{
	voit test();
	boolean noReturnImpl();
}
```

可以直接使用而不用担心引发崩溃

```java
Hub.getImpl(ITestApi.class).test();
boolean retuenValue = Hub.getImpl(ITestApi.class). noReturnImpl();
```
上述调用如果 ITestApi 的实现类没注册，**release**版本不会崩溃退出应用，**debug**版本的会强制崩溃，以检查实现类不存在的原因，尤其适用于多moudle，多小组开发的项目中