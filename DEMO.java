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
    
    //��̬����飬����װ��ʱִ����ִֻ��һ��
    static {
    	try {
			Class.forName(JDBC_DRIVER);   //��ֹ�������ע��
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
                 System.out.println(rs.getString(1) + " " + rs.getString(2) + " " + rs.getString(3) + " " + rs.getString("Sresult"));//Ҳ������������ѡ����
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
            ps.setString(1, a);      //Ԥ�����������룬����?˳���1��ʼ���
            status = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(status > 0 ? "����ɹ�" : "����ʧ��");
   }
    
    public static void updateResult() {
    	String sql = "UPDATE stud SET Sresult = ? WHERE Sno = ?";
   	 	int status = 0;
   	 	
   	 	try {
   	 		con.setAutoCommit(false);  //ȡ��JDBC������Զ��ύ����֤��ǰִ�������ԭ����
			ps = con.prepareStatement(sql);
			
			ps.setString(1, "114");
			ps.setString(2, "1906300042");
			status = ps.executeUpdate();
			   //ȡ��JDBC������Զ��ύ��Ϊ��ֹ������ִ���������޸Ĳ����ύ����������޸Ļ��ύ������������ݴ���
			ps.setString(1, "59.9");
			ps.setString(2, "1906300018");
			status = ps.executeUpdate();
			
			con.commit(); //�ֶ�ͳһ�ύ
			
		} catch (Exception e) {
			if(con != null) {
				try {
					con.rollback(); //����ִ�г��ִ������ֶ��ع�����
				} catch (Exception e2) {
					e2.printStackTrace();
					// TODO: handle exception
				}
			}
			e.printStackTrace();
			// TODO: handle exception
		}
   	 	
   	 	System.out.println(status > 0 ? "�޸ĳɹ�" : "�޸�ʧ��");
    }
    
    public static void callProcedure() {
    	
   	 	String sql = "CALL get_target_result(?)";
   	 	@SuppressWarnings("resource")
   	 	Scanner in = new Scanner(System.in);
   	 	String a = in.next();
   	 
        try {
            ps = con.prepareCall(sql);  //����洢�����õĺ���
            ps.setString(1, a);
            rs = ps.executeQuery(); //���￴�洢���̵Ĳ�����ѡ����Query����Update
            
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
