# KeEventBus 
   KeEventBus 是一个事件总线框架，用法非常简单
   
 集成只要在app的gradle中加入以下代码
 
    compile 'com.zengke.eventbus:eventbus:1.0.1'
    
 用法如下:
 
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getInstance().register(this);
     }
     
    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEvent(String str) {
        textView.setText(str);
    }
     
     .....
     
     @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getInstance().unregister(this);
    }
    
    在接收的地方将activity或者fragment注入EventBus中，然后通过Subscribe注解就可以接收到发送者post过来的事件
    并且支持三种不同的线程模式，PostThread，BackgroundThread和MainThread，通过这三种不同的枚举类型可以指定接收者在哪个线程工作。
      发送一个事件一行代码就可以搞定
        EventBus.getInstance().post(object);
        
       是不是觉得很方便,so easy!
