package Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DEMO {
    private static final String JDBC_DRIVER = "org.mariadb.jdbc.Driver";
    private static final String DB_URI = "jdbc:mariadb://127.0.0.1:3306/";
    private static final String DB_NAME = "stu";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "********";
    
    private static Connection con = null;
    private static PreparedStatement ps = null; 
    private static ResultSet rs = null;
    
    //静态代码块，在类装载时执行且只执行一次
    static {
    	try {
			Class.forName(JDBC_DRIVER);   //防止驱动多次注册
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public static void main(String[] args) {
        con = DEMO.getConnection();
        
        //updateResult();
        //selectAllStud();
        //insertInStud();
        callProcedure();
        
        closeConnection(con, ps, rs);
    }

    public static Connection getConnection() {
        Connection con = null;

        try {
            con = DriverManager.getConnection(DB_URI + DB_NAME, DB_USERNAME, DB_PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return con;
    }
    
    public static void selectAllStud() {
    	 String sql = "SELECT Sno,Sname,Sage,Sresult FROM stud ORDER BY Sresult ASC";
    	 
         try {
             ps = con.prepareStatement(sql);
             rs = ps.executeQuery();

             while (rs.next()) {
                 System.out.println(rs.getString(1) + " " + rs.getString(2) + " " + rs.getString(3) + " " + rs.getString("Sresult"));//也可以用列名来选择列
             }
         } catch (SQLException e) {
             e.printStackTrace();
         }
        
    }
    
    public static void insertInStud() {
   	 	String sql = "INSERT INTO stud VALUES('1906300024',?,19,58.7,Now())";
   	 	
   	 	@SuppressWarnings("resource")
		Scanner in = new Scanner(System.in);
   	 	String a = in.next();
   	 	int status = 0;
   	 	
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, a);      //预编译后进行输入，按照?顺序从1开始编号
            status = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(status > 0 ? "插入成功" : "插入失败");
   }
    
    public static void updateResult() {
    	String sql = "UPDATE stud SET Sresult = ? WHERE Sno = ?";
   	 	int status = 0;
   	 	
   	 	try {
   	 		con.setAutoCommit(false);  //取消JDBC事务的自动提交，保证当前执行事务的原子性
			ps = con.prepareStatement(sql);
			
			ps.setString(1, "114");
			ps.setString(2, "1906300042");
			status = ps.executeUpdate();
			   //取消JDBC事务的自动提交是为防止这里出现错误下面的修改不会提交但是上面的修改会提交，造成严重数据错误
			ps.setString(1, "59.9");
			ps.setString(2, "1906300018");
			status = ps.executeUpdate();
			
			con.commit(); //手动统一提交
			
		} catch (Exception e) {
			if(con != null) {
				try {
					con.rollback(); //若是执行出现错误则手动回滚事务
				} catch (Exception e2) {
					e2.printStackTrace();
					// TODO: handle exception
				}
			}
			e.printStackTrace();
			// TODO: handle exception
		}
   	 	
   	 	System.out.println(status > 0 ? "修改成功" : "修改失败");
    }
    
    public static void callProcedure() {
    	
   	 	String sql = "CALL get_target_result(?)";
   	 	@SuppressWarnings("resource")
   	 	Scanner in = new Scanner(System.in);
   	 	String a = in.next();
   	 
        try {
            ps = con.prepareCall(sql);  //编译存储过程用的函数
            ps.setString(1, a);
            rs = ps.executeQuery(); //这里看存储过程的操作来选择是Query还是Update
            
            while (rs.next()) {
                System.out.println(rs.getString(1) + " " + rs.getString(2));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
       
   }

    private static void closeConnection(Connection con, PreparedStatement ps, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        try {
            if (ps != null) {
                ps.close();
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        try {
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
