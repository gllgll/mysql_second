package com.example.mysql_second;

import android.util.Log;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DBUtils {

    //    定义日志过滤标签
    private static final String TAG = "DBUtils";

    //    定义全局变量conn
    private static Connection conn = null;


//数据库连接函数
//    这个函数返回数据库的连接对象conn，这里写函数返回类型是Connection,因为conn是Connection类型的
//   其中的String name这个参数是在调用这个连接函数连接数据库的时候要传入数据库名dbname

    public static Connection getConnection(String dbname) {

//        定义一些启动JDBC需要的参数，分别是ip地址，端口号，用户名和密码，以及创建与数据库连接时要用到的url
        String ip = "10.0.2.2";
        int port = 3306;
        String user = "root";
        String password = "123456";
        String url = "jdbc:mysql://" + ip + ":" + port + "/" + dbname + "?useUnicode=true&characterEncoding=UTF-8";
//        "?useUnicode=true&characterEncoding=UTF-8"这一串是为了往数据库中添加中文信息的时候不会出现??或者乱码，大概意思应该是一些utf8的字符串规定
//以上完成注册JDBC前的准备事项

//        开始注册JDBC驱动，只有注册了JDBC驱动才能连接数据库
        try {

//            注册语句Class.forName("com.mysql.jdbc.Driver");
//            是固定的，可能会因为mysql或者connector的jar包版本不同而有略微变化,要用try/catch语句包含
            Class.forName("com.mysql.jdbc.Driver");
            Log.d(TAG, "加载JDBC驱动成功");
        } catch (ClassNotFoundException e) {
            Log.d(TAG, "加载JDBC驱动失败");
        }


//        注册完后创建与mysql的连接对象conn,通过DriverManager类的getConnection方法创建,三个参数分别是上文定义的url,user,password,同样要用try/catch包含
        try {
            conn = DriverManager.getConnection(url, user, password);
            Log.d(TAG, "数据库连接成功");

        } catch (SQLException e) {
            Log.d(TAG, "数据库连接失败");
        }

//        以上完成对conn的创建
//        这里一定要把创建的conn返回出去,因为在下面定义增删改查函数时需要调用这个连接函数，而且连接函数的返回类型是Connection，即conn的类型，只有return出去了，调用这个函数才能返回我们需要的conn
        return conn;
    }

    //下面开始定义查询函数，查询函数是增删改查里最复杂的，有了这个函数在进行增删改的操作时就能直接调用查询函数进行实时更新
//数据库查询函数
//返回类型是List<HashMap<String,Object>>，因为等下在主函数中我是用simpleAdapter的方法往listview中添加数据的，所以这里直接返回List嵌套map的数据类型，免得还要一步步转数据类型
//这个函数也有一个参数，同样是数据库名，这些参数都是可以自定义的
    public static List<HashMap<String, Object>> getinfo(String dbname) throws SQLException {

//       先定义一个List<HashMap<String,Object>>类型的数据并实例化
        List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

//        调用连接函数，传入数据库名的形参，获得conn对象，因为getConnection的返回类型就是Connection及conn
        Connection conn = getConnection(dbname);

//        由conn对象创建执行sql语句的对象（Statement类型),调用方法createStatement()
        Statement sta = conn.createStatement();

//        定义sql语句
        String sql = "select * from emp ";

//        调用Statement对象执行sql语句,返回结果result是ResultSet类型，就是结果集，具体百度
        ResultSet result = sta.executeQuery(sql);

//        判断一下是否为空
        if (result == null) {
            return null;
        }

//        条件是当结果集是否有下一行，这是一个相当于指针的东西，第一次调用时会把第一行设置为当前行，第二次回吧第二行设置为当前行，以此类推，直到没有下一行，循环结束
        while (result.next()) {
//            每次循环都会新实例化一个HashMap对象，用于将遍历到的数据填进去
            HashMap<String, Object> map = new HashMap<>();
//            往map中填数据，map的数据类型相当于键值对
//            键是name，值是result.getString("empname"),意思是结果集指针所在行的字段名中的数据
            map.put("name", result.getString("empname"));
//            每次循环完就添加到list中，最终list的样子是：[{name=xx},{name=aaa},.......]
            list.add(map);


        }
//        最后记得把list返回出去，不然拿不到这个list
        return list;
    }

//以下是增删改函数，这些函数都不需要返回什么数据，所以都是void类型的函数，因为只要每次增删改完了以后，用查询函数重查一遍返回一个新的list就可以了，显示出来的数据就是已经增删改过后的数据
//数据库添加函数

    //    传入参数数据库名，可自由diy
    public static void insert(String dbname) throws SQLException {
//        同样先调用连接数据库函数，拿到连接对象
        Connection conn = getConnection(dbname);

//        同样创建sql语句执行对象
        Statement sta = conn.createStatement();

//        定义sql语句
        String sql = "insert into emp values(66666665,'csdn博客',98765,1)";

//        Statement对象执行sql语句
        sta.execute(sql);
    }

//    以下不再赘述，一样

    //数据库删除函数
    public static void delete(String dbname, String empname) throws SQLException {
        Connection conn = getConnection(dbname);

        Statement sta = conn.createStatement();

        String sql = "delete from emp where empname=" + "'" + empname + "'";
        sta.execute(sql);
    }


    //数据库更新函数
    public static void update(String dbname, String empname) throws SQLException {
        Connection conn = getConnection(dbname);

        Statement sta = conn.createStatement();

        String sql = "update emp set empname='阿巴阿巴阿巴阿巴阿巴阿巴' where empname=" + "'" + empname + "'";
        sta.executeUpdate(sql);
    }
}

