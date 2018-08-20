


#### 相信大家看关于Retrofit的文章,无不列外,比如以下封装形式
```
public interface UserServiceApi {
    @POST("api/login")
    Call<Result<User>> login(@Body WechatLoginRequest request);

    @POST("version/version")
    Call<Result<User>> update(@Body Update request);

    @POST("api/logout")
    Call<Result> logout(@Body BaseRequest request);

    @POST("api/user/info")
    Call<Result<User>> getOtherUserInfo(@Body BaseRequest request);
}
```
#### 了解源码就知道,Retrofit会为你拼接参数,既然这样,每个接口都有请求体,我为什么不把请求参数封装到请求体上呢?这样无论多少请求接口,写个通用的就可以了!返回String即可,它是万能的啊,不是吗~
```
public interface Api {
    String PATH = "path";

    @POST("{" + PATH + "}")
    Observable<String> runPost(@Path(value = PATH, encoded = true) String path,
                               @Body String json);

    /**
     * form表单提交
     */
    @GET("{" + PATH + "}")
    Observable<String> runGetII(@Path(value = PATH, encoded = true) String path, @QueryMap Map<String, Object> json);
}
```
#### 创建注解

```
/**
 * @ReqTags("")
 *
 * @ReqTags(value="注册",extdParams={"user/register"},extdCmdParams={"1"})
 */
@Inherited
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ReqTags {
    String value() default "";

    /**
     * 扩展string参数,如有需要
     * @return
     */
    String[] extdParams() default {};

    /**
     * 扩展命令,如有需要
     */
    int[] extdCmdParams() default {};
}
```
#### 封装个通用请求体,抽取共性
```
public interface PostfixImpl {
    /**
     * 后缀
     */
    String postfix();

    /**
     * 跳过携带参数吗,默认false
     * @return
     */
    boolean skipParams();
}

```
#### 通用请求体

```
@ReqTags("")
public abstract class CommonRequest implements Serializable, PostfixImpl {
    // 这里可以构造一些通用参数,如有需要的话
    // public String channel;//来源渠道
    public CommonRequest() {
    }

    @Override
    public boolean skipParams() {
        return false; // 不跳过携带参数
    }
}
```

##### 比如有如下接口
##### 登录接口:https://api.github.com/user/login
##### 注册接口:https://api.github.com/user/register
##### 列表接口:https://api.github.com/user/list
##### 更新接口:https://api.github.com/user/update
##### 测试接口:https://api.github.com/user/test
##### 注册请求体
```
@ReqTags("注册")
public class RegisterReq extends CommonRequest {
    public String username;
    public String password;
    public String repassword;

    public RegisterReq(String username, String password, String repassword) {
        this.username = username;
        this.password = password;
        this.repassword = repassword;
    }

    public RegisterReq() {
    }

    @Override
    public String postfix() {
        return "user/register";
    }
}
```
##### 登录请求体
```
@ReqTags("登录")
public class LoginReq extends CommonRequest {
    public String username;
    public String password;
    public String repassword;

    public LoginReq(String username, String password, String repassword) {
        this.username = username;
        this.password = password;
        this.repassword = repassword;
    }

    public LoginReq() {
    }

    @Override
    public String postfix() {
        return "user/login";
    }
}
```
##### 列表请求体
```
@ReqTags("列表")
public class ListReq extends CommonRequest {
    public int page=1;

    public ListReq() {
    }
    @Override
    public String postfix() {
        return "user/list";
    }
}
```
##### 更新请求体
```
@ReqTags("更新")
public class UpdateReq extends CommonRequest {

    public UpdateReq() {
    }

    @Override
    public String postfix() {
        return "user/update";
    }
}
```
##### 测试请求体
```
@ReqTags("测试")
public class TstReq extends CommonRequest {
    public String username;

    @Override
    public String postfix() {
        return "user/test";
    }
     @Override
    public boolean skipParams() {
        return true; // 不带请求参数,返回true
    }
}
```

##### 发起请求

```
final RegisterReq json = new RegisterReq("12132322333", "123456", "123456");

 HttpRequestFactory.doPost(json, new ResultCallbackAdapt<HttpCommObjResp<RegisterResp>>() {
            @Override
            public void doOnResponse(HttpCommObjResp<RegisterResp> response) {
                System.out.println(response + "");
                mContent.setText("\n方式一返回:" + response);
            }

            @Override
            public void doOnError(ApiException ex) {
                System.out.println(ex + "");
                mContent.setText("\n方式一返回:" + ex);
            }
        }, iLoading);

        // 方式二
        HttpRequestFactory.doPost(json, new ResultCallbackAdapt<String>() {
            @Override
            public void doOnResponse(String response) {
                System.out.println(response + "");
                mContent.setText("\n方式二返回:" + response);
            }

            @Override
            public void doOnError(ApiException ex) {
                System.out.println(ex + "");
                mContent.setText("\n方式二返回:" + ex);
            }
        });
```

初始化函数

```
public class MyApplication extends Application
{
	@Override
    public void onCreate()
    {
        super.onCreate();
         HttpRequestFactory.doCreateHttpReqManager(this, ResLibConfig.DEBUG ? "http://www.wanandroid.com/" : "http://www.wanandroid.com/")
                 .registerCallbacks(new HttpHeadersImpl() {
                     @Override
                     public Map<String, String> generateHeaders() {
                         Map<String, String> keys = new HashMap<>();
                         //        keys.put("versionCode", "" + IUtil.obtainAppVersionCode(ResLibConfig.CONTEXT));
                         //        keys.put("version", IUtil.obtainAppVersion(ResLibConfig.CONTEXT));
                         //        keys.put("channel", "");
                         //        keys.put("imei", "");
                         //        keys.put("platform", "2");//1:ios 2:Android
                         //        keys.put("model", IUtil.MODEL);
                         //        keys.put("vendor", IUtil.BRAND);
                         return keys; // 请求头注入,如有需要
                     }
                 }).registerCallbacks(new HttpInterceptor() {
             @Override
             public void doInterceptor(int... cmds) {
                 if (cmds != null && cmds.length > 0) {
                     int code = cmds[0];
                     switch (code) {
                         case HttpInterceptor.KICKED_OFF_LINE:
                             //Tools.showToast("用户已在其他设备登录");
                             int value = AUTO_INCREMENT.incrementAndGet();
                             if (value == 1) {
                                 // relogin(); // 调用一次
                             }
                             Logger.e("http", "用户已在其他设备登录|" + value + "|");
                             break;
                         case HttpInterceptor.PROHIBIT_USED:
                             //Tools.showToast("您被禁止使用");
                             break;
                         case HttpInterceptor.PROHIBIT_USEDII:
                             //Tools.showToast("用户被封禁,不能登录");
                             break;
                         case HttpInterceptor.UN_LOGIN:
                             //Tools.showToast("请先登录");
                             //relogin(false);
                             break;
                     }
                 }
             }
         }).asEncryptFuncs(new EncryptFuncs() {
             @Override
             public String encrypt(String plainText) {
                 //return AesEncryptionUtil.encrypt(plainText);
                 return "";
             }

             @Override
             public String decrypt(String cipherText) {
                 //return AesEncryptionUtil.decrypt(cipherText);
                 return "";
             }

             /**
              * {@link ReqTags}
              * {@link kaiqi.cn.httpx.req.RegisterReq}
              * 请给予tag,否则取消不掉请求,拦截不了加密解密赛~
              */
             @Override
             public boolean accept(String httpReaTag) {
                 if ("注册".equals(httpReaTag)) {
                     // return false; // 你们都同意,我注册第一个不答应
                 }
                 return true; // 同意加密
             }
         }).asSubmmitForm(true); // true = 表单提交 false = json提交

    }
}

```



##### tips:关于用HttpCommObjResp<RegisterResp> 还是用 HttpCommObjsResp<RegisterResp>

##### 形如格式:
```
{
 "code":200,
 "msg":"成功",
 "data":{}
}
```
##### data = {} 是对象,所以用HttpCommObjResp<RegisterResp>

##### 形如格式:
```
{
 "code":200,
 "msg":"成功",
 "data":[]
}
```
##### data = [] 是数组对象,所以用HttpCommObjsResp<RegisterResp>

##### 我以为所有的接口返回都是这样的,其实并不是
##### 以下是注册返回json

```
{
	"data": {
		"collectIds": [],
		"email": "",
		"icon": "",
		"id": 9073,
		"password": "123456",
		"token": "",
		"type": 0,
		"username": "19910222"
	},
	"errorCode": 0,
	"errorMsg": ""
}
```

##### 为什么不是code,msg??

##### 不过不要怕,照样无缝链接网络调用,提供json,表单两种提交方式|下载,上传这个没上心写!!!


###### 因为暂时支持这几种返回格式的组合,不满足格式的话,如有需要,请修改源码
~~~
{
    "msg":"",
    "code"100,
    "data":{}
}
{
    "errorMsg":"",
    "errorCode"100,
    "data":{}
}
{
    "message":"",
    "error"100,
    "infos":{}
}
{
    ....
    "results":{}
}
~~~

#####  所以放心的用吧
```
 HttpRequestFactory.doPost(json, new ResultCallbackAdapt<String>() {
              @Override
              public void doOnResponse(String response) {
                  System.out.println(response + "");
                  mContent.setText("\n方式二返回:" + response);
              }

              @Override
              public void doOnError(ApiException ex) {
                  System.out.println(ex + "");
                  mContent.setText("\n方式二返回:" + ex);
              }
          });
```

###### 关于取消请求

```
HttpRequestFactory.cancel("注册");//取消单个
HttpRequestFactory.cancel("注册","首页文章列表"); // 取消多个

```

##### 关于加密解密,暂时只支持json加密解密!!!
##### 关于上传,公司项目图片现在都走阿里云,实在是没测试接口了,欢迎大佬们提供测试接口,让我来完善这个库~
##### 代码还有很多不足之处,请指教


[混淆配置](混淆配置.md)

##### 当日志满天飞,你说我有格式化日志工具类,美倒是美,接口多了,不好找啊,
##### 那怎么样才可以找我想要的接口信息呢?
##### 问的好,请看日志截图,一图胜千言~是不是很清爽,看我所看,爱我所爱.

![日志摘要1](log1.jpg)
![日志摘要2](log2.jpg)


> 为什么要这样写?看下服务端代马

```
	@RequestMapping(value = "/showUser", method = { RequestMethod.GET,
			RequestMethod.POST }, produces = "application/json;charset=UTF-8")
	public JSONObject toIndex(@RequestBody(required = false) String data) {
		return callHttpReqTask(data, TO_INDEX);
	}

	@RequestMapping(value = "/addUser", method = { RequestMethod.GET,
			RequestMethod.POST }, produces = "application/json;charset=UTF-8")
	public JSONObject addUser(@RequestBody(required = false) String data) {
		return callHttpReqTask(data, ADD_USER);
	}
```
##### 每一个请求都有一个RequestMapping
##### 我在每一个请求体上构造一个后缀,岂不是和后台相映成趣.好处呢?
##### 我宁愿构造javabean也不愿意这样传参....
##### 摘至代马,来至哪里不重要~

```
Map<String, String> params = new HashMap<String, String>();
params.put("name", "tsy");
params.put("name", "tsy");
params.put("name", "tsy");
params.put("name", "tsy");
params.put("name", "tsy");
params.put("name", "tsy");
params.put("name", "tsy");
params.put("name", "tsy");
params.put("name", "tsy"); // 每一个接口都初始化 hashmap~  What Fuck ~有点累阿,交给gson多好
MyOkHttp.get().post(this, "http://baidu.com/", params, new JsonResponseHandler() {
    @Override
    public void onSuccess(int statusCode, JSONObject response) {
        LogUtils.v(TAG, statusCode + " " + response);
    }

    @Override
    public void onFailure(int statusCode, String error_msg) {
        LogUtils.v(TAG, statusCode + " " + error_msg);
    }
});
```
##### 构造javabean的好处,key已经写好了,你只需要关注value即可,我觉得比写hashmap快一丢丢~

```
@ReqTags("接口姓甚名谁") // 他还有2个兄弟拓展参数,保留着没用,如果你愿意,可以对他动手脚
public class X extends CommonRequest{
  public String name;
  public String pahone;
  public X(String name,String phone){}
  @Override
  public String postfix() {
        return "user/register";
  }
}

HttpRequestFactory.doPost(new X("xx","yy"), new ResultCallbackAdapt<HttpCommObjResp<RegisterResp>>() {
            @Override
            public void doOnResponse(HttpCommObjResp<RegisterResp> response) {
                System.out.println(response + "");
                mContent.setText("\n方式一返回:" + response);
            }

            @Override
            public void doOnError(ApiException ex) {
                System.out.println(ex + "");
                mContent.setText("\n方式一返回:" + ex);
            }
        ); //不要loading时
        //}, iLoading); // loading,这东西如有需要

        get请求呢,改个名字....即可!!饥渴~~
HttpRequestFactory.doGet(new X("xx","yy"), new ResultCallbackAdapt<HttpCommObjResp<RegisterResp>>() {
            @Override
            public void doOnResponse(HttpCommObjResp<RegisterResp> response) {
                System.out.println(response + "");
                mContent.setText("\n方式一返回:" + response);
            }

            @Override
            public void doOnError(ApiException ex) {
                System.out.println(ex + "");
                mContent.setText("\n方式一返回:" + ex);
            }
        ); //不要loading时
        //}, iLoading); // loading,这东西如有需要

```

#####  调用堆栈不深,适合新手阅读源码,有可取之处,给我加star吧

#####  转载注明出处 [HttpX](https://github.com/majunm/HttpX)
##### 如有需要 [加密解密库](https://github.com/majunm/encryptanddecrypt)

[![](https://jitpack.io/v/majunm/HttpX.svg)](https://jitpack.io/#majunm/HttpX)


> 使用方式,如有需要
```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

```
	dependencies {
    	        implementation 'com.github.majunm:HttpX:v1.0.0-gamma'
    	}
```
