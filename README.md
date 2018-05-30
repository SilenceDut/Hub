## Hub [![](https://jitpack.io/v/SilenceDut/Hub.svg)](https://jitpack.io/#SilenceDut/Hub)
a concise di library which can avoid check null when want to invoke a implementation by interface

## Project

It had been used in project [KnowWeather](https://github.com/SilenceDut/KnowWeather),you can learn more.

## About

Android 开发中通过接口获取实现类，可用于module之间的接口的调用，通信，不需要繁琐的显示注册，通过注解解决module的依赖初始化问题，并且可以避免由于初始化先后顺序导致的问题
线上环境不用每次都判断实现类是否存在直接调用，通过动态代理来规避由于实现类不存在而导致的崩溃。

**condition-不需要显示注册初始化**

```java
public interface ITestApi extends IHub{
	void test();
}
```

不需要显示的注册

```java
@HubInject(api = ITestApi.class)
public class TestImpl implements ITestApi {

    @Override
    public void onCreate() {
        //添加初始化，onCreate 在实现类被创建时自动调用的，不需要再手动调用
    }
    
    @Override
    public void test() {
        Log.d("TestImpl","test");
    }
}

```

不需要判空、强转，可以直接使用

```java
Hub.getImpl(ITestApi.class).test();
```
多接口支持。优点在于项目存在多个module时，不必要暴露所有接口,只需将需要的接口暴露

```java
@HubInject(api = {IFunctionProvider.class,IFunctionLogic.class})
class FunctionImpl implements IProvider , ILogic{
    ...
}
```

**condition-实现类不存在不引发崩溃（module间通信）**

```java
public interface NoImplApi extends IBaseHub{
    void noImpl();
    boolean noReturnImpl();
}
```

线上环境没有实现类不会引发崩溃

```java
Hub.getImpl(NoImplApi.class).test();
boolean returnValue = Hub.getImpl(NoImplApi.class). noReturnImpl();
```

## Using

**Step1.Add it in your root build.gradle at the end of repositories:**

```java
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```


**Step2. Add the dependency:**

```java
dependencies {
    implementation 'com.github.SilenceDut.Hub:hub:latestVersion'
    annotationProcessor 'com.github.SilenceDut.Hub:hub-compiler:latestVersion'
}
```
