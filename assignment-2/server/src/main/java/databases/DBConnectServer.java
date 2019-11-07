package databases;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

public class DBConnectServer {
  private static DBConnectServer instance;
  private static ComboPooledDataSource dataSource;

  private DBConnectServer() throws SQLException, PropertyVetoException {
    dataSource = new ComboPooledDataSource();
    dataSource.setUser("root");     //用户名
    dataSource.setPassword("0120352wsz"); //密码
//    dataSource.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/Skiers");//数据库地址
    dataSource.setJdbcUrl("jdbc:mysql://database-2.cdgot1ydqh5o.us-west-2.rds.amazonaws.com:3306/Skiers");//数据库地址
    dataSource.setDriverClass("com.mysql.jdbc.Driver");
    dataSource.setInitialPoolSize(17); // 初始化时获取连接数，取值应在minPoolSize与maxPoolSize之间。Default: 3
    dataSource.setMinPoolSize(1);  //  连接池中保留的最小连接数
    dataSource.setMaxPoolSize(16); // 连接池中保留的最大连接数。Default: 15

    dataSource.setMaxStatements(50);//最长等待时间

    dataSource.setMaxIdleTime(0);//最大空闲时间,60秒内未使用则连接被丢弃。若为0则永不丢弃。Default: 0
    dataSource.setAcquireIncrement(3);// 当连接池中的连接耗尽的时候c3p0一次同时获取的连接数。Default: 3
    dataSource.setAcquireRetryAttempts(30);// 定义在从数据库获取新连接失败后重复尝试的次数。Default: 30
  }

  public static final DBConnectServer getInstance() {
    if (instance == null) {
      try {
        instance = new DBConnectServer();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return instance;
  }

  public synchronized final Connection getConnection() {
    Connection conn = null;
    try {
      conn = dataSource.getConnection();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return conn;
  }
}
