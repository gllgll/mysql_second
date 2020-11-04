package com.example.mysql_second;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    //    定义一个日志信息过滤标签，测试用
    private static final String TAG = "DBUtils";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //获取按钮组件
        Button add = findViewById(R.id.add);
        Button delete = findViewById(R.id.delete);
        Button update = findViewById(R.id.update);
        Button query = findViewById(R.id.query);


//*************查找按钮功能****************************************************************************
        query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                在点击事件里写内容
//-----------------------------------------
//        Handler部分
//        由子线程传出的数据在这里处理
                @SuppressLint("HandlerLeak")
//                先new一个Handler对象
                final Handler handler = new Handler() {
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        super.handleMessage(msg);
//                        获取listview组件，因为我们要在这个部分更新UI组件
                        final ListView listView = findViewById(R.id.listview);
//                        if的判断条件是区分msg是哪一条，即msg的ID
                        if (msg.what == 1) {
//                            获取发送过来的msg.obj对象，因为我传的是List<HashMap<String, Object>>类型的obj，所以这边同样用List<HashMap<String, Object>> list去接收，要强转
                            List<HashMap<String, Object>> list = (List<HashMap<String, Object>>) msg.obj;

//                            定义SimpleAdapter，参数分别为当前上下文，刚拿到的数据集合list，子项布局文件，数据集合中的字段信息，要添加到的子布局文件中的控件ID
                            SimpleAdapter simpleAdapter = new SimpleAdapter(MainActivity.this, list, R.layout.item_xml, new String[]{"name"}, new int[]{R.id.itemtext});
//                            为listview设置适配器
                            listView.setAdapter(simpleAdapter);
                        }


                    }
                };
//        Handler部分
//----------------------------------------------------------------------

//--------------------------------------------------------------
// 连接数据库并进行相应操作的线程
//        第二、第三部分
//                new 一个线程,接下来是数据库操作部分，要在子线程中执行
                Thread thread = new Thread(new Runnable() {
                    //                    定义一个子线程中的全局变量List<HashMap<String,Object>> list1，用于接收从DBUtils中返回的list
                    List<HashMap<String, Object>> list1 = new ArrayList<HashMap<String, Object>>();

                    @Override
                    public void run() {
//                        与数据库建立连接
                        DBUtils.getConnection("company");
                        try {
//                            以下这些要用try/catch包含
//                            调用数据库工具类的getinfo函数，用list1接收返回的list数据
                            list1 = DBUtils.getinfo("company");

//                            打印日志，测试用
                            Log.d(TAG, list1.toString());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

//                将从数据库拿到的list1对象传给message再由handler传出，再在handler中处理，可进行更新UI
//                        新建一个message对象，尽量不要直接new，而是用这种方法，因为有内存的问题存在
                        Message message = Message.obtain();

//                        设置message的辨认码，这里设为1
                        message.what = 1;

//                        把刚才接收到的list1赋给message.obj对象
                        message.obj = list1;

//                        通过handler将携带数据的message传出去，传到handler中
                        handler.sendMessage(message);
                    }
                });

//                上面线程定义完了，现在启动线程
                thread.start();

//        第二、第三部分
//------------------------------------------------
            }
        });

//***********************************************************************************


//以下增删改功能和以上相同，区别只在于要在调用连接函数和查找函数之间加一个增或删或改函数,其余结构一模一样

//*************添加按钮功能****************************************************************************
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//        由子线程传出的数据在这里处理-----------------------------------------
                @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        super.handleMessage(msg);
                        final ListView listView = findViewById(R.id.listview);
                        if (msg.what == 1) {
                            List<HashMap<String, Object>> list = (List<HashMap<String, Object>>) msg.obj;


                            SimpleAdapter simpleAdapter = new SimpleAdapter(MainActivity.this, list, R.layout.item_xml, new String[]{"name"}, new int[]{R.id.itemtext});
                            listView.setAdapter(simpleAdapter);
                        }


                    }
                };
                //-------------------------------------------

//-------------------------------------------连接数据库线程并添加
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        DBUtils.getConnection("company");
//                        连接数据库
                        try {
//                            添加数据
                            DBUtils.insert("company");
                            List<HashMap<String, Object>> list1 = new ArrayList<HashMap<String, Object>>();
                            list1 = DBUtils.getinfo("company");
                            Log.d(TAG, list1.toString());

//                            以下这些也可以new一个新的线程执行，我这里是做成添加了立刻刷新列表的效果所以添加完数据立刻获取list并通过message发送出去再添加到适配器中
                            //                将从数据库拿到的list1对象传给message再由handler传出，再在Handler中处理，可进行更新UI
                            Message message = Message.obtain();
                            message.what = 1;
                            message.obj = list1;
                            handler.sendMessage(message);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();

            }
        });


// 删除按钮功能
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//        由子线程传出的数据在这里处理
                @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        super.handleMessage(msg);
                        final ListView listView = findViewById(R.id.listview);
                        if (msg.what == 1) {
                            List<HashMap<String, Object>> list = (List<HashMap<String, Object>>) msg.obj;
                            SimpleAdapter simpleAdapter = new SimpleAdapter(MainActivity.this, list, R.layout.item_xml, new String[]{"name"}, new int[]{R.id.itemtext});
                            listView.setAdapter(simpleAdapter);
                        }
                    }
                };

//连接数据库线程并删除
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        DBUtils.getConnection("company");
                        try {
                            DBUtils.delete("company", "我等会会被删掉");
                            List<HashMap<String, Object>> list1 = new ArrayList<HashMap<String, Object>>();
                            list1 = DBUtils.getinfo("company");
                            Log.d(TAG, list1.toString());

//                            以下这些也可以new一个新的线程执行，我这里是做成添加了立刻刷新列表的效果所以添加完数据立刻获取list并通过message发送出去再添加到适配器中
                            //                将从数据库拿到的list1对象传给message再由handler传出，再在Handler中处理，可进行更新UI
                            Message message = Message.obtain();
                            message.what = 1;
                            message.obj = list1;
                            handler.sendMessage(message);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
        });

//更新按钮功能
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//        由子线程传出的数据在这里处理
                @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        super.handleMessage(msg);
                        final ListView listView = findViewById(R.id.listview);
                        if (msg.what == 1) {
                            List<HashMap<String, Object>> list = (List<HashMap<String, Object>>) msg.obj;
                            SimpleAdapter simpleAdapter = new SimpleAdapter(MainActivity.this, list, R.layout.item_xml, new String[]{"name"}, new int[]{R.id.itemtext});
                            listView.setAdapter(simpleAdapter);
                        }
                    }
                };

//连接数据库线程并更新
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        DBUtils.getConnection("company");
                        try {
                            DBUtils.update("company", "小d");
                            List<HashMap<String, Object>> list1 = new ArrayList<HashMap<String, Object>>();
                            list1 = DBUtils.getinfo("company");
                            Log.d(TAG, list1.toString());

//                            以下这些也可以new一个新的线程执行，我这里是做成添加了立刻刷新列表的效果所以添加完数据立刻获取list并通过message发送出去再添加到适配器中
                            //                将从数据库拿到的list1对象传给message再由handler传出，再在Handler中处理，可进行更新UI
                            Message message = Message.obtain();
                            message.what = 1;
                            message.obj = list1;
                            handler.sendMessage(message);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
//-------------------------------------------------

            }
        });
//***********************************************************************************


    }
}

