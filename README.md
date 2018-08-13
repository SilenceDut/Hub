## Hub [![](https://jitpack.io/v/SilenceDut/Hub.svg)](https://jitpack.io/#SilenceDut/Hub)
a concise di library which can avoid check null when want to invoke a implementation by interface

## Project

It had been used in project [KnowWeather](https://github.com/SilenceDut/KnowWeather) ,you can learn more.

同时已用在日活百万级别的线上项目中，目前没出现任何问题，可放心使用

## Feature

#### 1. 根据接口获取实现类，清晰直观，不需要预加载，初始化，随用随取，方便加载,卸载不需要的功能
#### 2. 跳转Activity，参数直接在方法中定义，自动处理参数，不需要繁琐的参数说明，字符串，以及注解标识，支持Activity多进程
#### 3. 不需要繁琐的判空处理


**接口化的通信方式，面向接口编程，更清晰直观，对IDE更友好（可在IDE中直接跳转），编译期发现问题。
使用简单，更少的初始化，注册，注解等，学习成本低
已在线上项目中使用久，稳定性有保证**

## Using

### 通过接口获取实现类

Android 开发中通过接口获取实现类，可用于多个module之间的接口的调用，通信，不需要繁琐的显示注册，通过注解解决module的依赖初始化问题，并且可以避免由于初始化先后顺序导致的问题
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

### 跳转Activity,参数自动解析不需要繁琐的注解，支持多进程Activity参数传递

```java
public interface IActivityTest extends IHubActivity{
    void anyMethodName(List<Map<String,Integer>> a, int b);
    void activity2(); // no need use at present
}
```

在需要调转的Activity上加上HubActivity注解，简单配置，参数名和类型和接口里的保持一致，不需要注解
```java
@HubActivity(activityApi = IActivityTest.class,methodName = "anyMethodName")
public class SecondActivity extends AppCompatActivity {
    List<Map<String,Integer>> a; // 参数名和类型保持一致
    int b; // 参数名和类型保持一致
    ...
}
```

调用

```java
Hub.getActivity(IActivityTest.class).activitySecond(mapList,9);
```

activityResult 
```java
Hub.getActivityWithExpand(IActivityTest.class).withResult(MainActivity.this,10).build().activitySecond(mapList,9);
```

## Import

**Step1.Add it in your root add abuild.gradle at the end of repositories:**

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

**ProGuard**
```java
-keep class * implements com.silencedut.hub_annotation.IFindImplClz {*;}
-keep class * implements com.silencedut.hub_annotation.IFindActivity {*;}
-keepnames interface * extends com.silencedut.hub.IHub
-keepnames interface * extends com.silencedut.hub.IHubActivity
```
