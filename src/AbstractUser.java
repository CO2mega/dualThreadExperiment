import java.sql.SQLException;
import java.io.IOException;

/**
 * TODO �����û��࣬Ϊ���û������ṩģ��
 *
 * @author gongjing
 * @date 2016/10/13
 */
public abstract class AbstractUser {
	private String name;
	private String password;
	private String role;
	static final double EXCEPTION_PROBABILITY=0.9;
	
	AbstractUser(String name,String password,String role){
		this.name=name;
		this.password=password;
		this.role=role;				
	}
	
	
	/**
	 * TODO �޸��û�������Ϣ
	 * 
	 * @param password ����
	 * @return boolean �޸��Ƿ�ɹ�
	 * @throws SQLException   
	*/
	public boolean changeSelfInfo(String password) throws SQLException{
		if (DataProcessing.updateUser(name, password, role)){
			this.password=password;
			System.out.println("�޸ĳɹ�");
			return true;
		}else {
			return false;
		}
	}	
	
	/**
	 * TODO ���ص����ļ�
	 * 
	 * @param filename �ļ���
	 * @return boolean �����Ƿ�ɹ�
	 * @throws IOException   
	*/
	public static boolean downloadFile(String filename) throws IOException{
		double ranValue=Math.random();
		if (ranValue>EXCEPTION_PROBABILITY) {
			throw new IOException( "Error in accessing file" );}
		System.out.println("�����ļ�... ...");
		return true;
	}
	
	/**
	 * TODO չʾ�����ļ��б�
	 * 
	 * @param 
	 * @return void
	 * @throws SQLException 
	*/
	public void showFileList() throws SQLException{
		double ranValue=Math.random();
		if (ranValue>EXCEPTION_PROBABILITY) {
			throw new SQLException( "Error in accessing file DB" );}
		System.out.println("�б�... ...");
	}
	
	
	
	/**
	 * TODO չʾ�˵�����������Ը���
	 *   
	 * @param 
	 * @return void
	 * @throws  
	*/
	public abstract void showMenu();
	
	/**
	 * TODO �˳�ϵͳ
	 *   
	 * @param 
	 * @return void
	 * @throws  
	*/
	public static void exitSystem(){
		System.out.println("ϵͳ�˳�, ллʹ�� ! ");
		System.exit(0);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
	

}
