package pruebas;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import factory.ConnectionFactory;

public class PruebaDelete {

	public static void main(String[] args) throws SQLException {
		
		Connection con = new ConnectionFactory().recuperaConexion();
		
		Statement statement = con.createStatement();
		
		statement.execute("DELETE FROM PRODUCTO WHERE ID = 99");
		
		System.out.println(statement.getUpdateCount());//el id 99 no existe, el resultado debe ser 0
		
	
		
	}
}
