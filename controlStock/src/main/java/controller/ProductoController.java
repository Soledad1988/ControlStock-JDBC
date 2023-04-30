package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import factory.ConnectionFactory;

public class ProductoController {


	public int modificar(String nombre, String descripcion, Integer cantidad, Integer id) throws SQLException {
	    ConnectionFactory factory = new ConnectionFactory();
	    final Connection con = factory.recuperaConexion();
	    
	    try(con){
	    //antes
	    /*Statement statement = con.createStatement();
	    statement.execute("UPDATE PRODUCTO SET "
	            + " NOMBRE = '" + nombre + "'"
	            + ", DESCRIPCION = '" + descripcion + "'"
	            + ", CANTIDAD = " + cantidad
	            + " WHERE ID = " + id);*/
	    
	   final  PreparedStatement statement = con.prepareStatement("UPDATE PRODUCTO SET "
	            + " NOMBRE = ?"
	            + ", DESCRIPCION = ?"
	            + ", CANTIDAD =  ?"
	            + " WHERE ID =  ?");
	   
		   try(statement){
		    statement.setString(1, nombre);
			statement.setString(2, descripcion);
			statement.setInt(3, cantidad);
			statement.setInt(1, id);
		    
		    statement.execute();
	
		    int updateCount = statement.getUpdateCount();
	
		    return updateCount;
		   }
	    }
	}
	

	public int eliminar(Integer id) throws SQLException {
		
		final Connection con = new ConnectionFactory().recuperaConexion();
		
		try(con){
		//Statement statement = con.createStatement(); antes
		final PreparedStatement statement = con.prepareStatement("DELETE FROM PRODUCTO WHERE ID = ?");
		
			try(statement){
			statement.setInt(1, id);
			
			statement.execute();
			
			return statement.getUpdateCount(); //me devuelve la cantidad de filas modificadas
		
			}
		}
		
	}
	

	//vamos a desarrollar la logica para conectar a la base de datos
	public List<Map<String,String>> listar() throws SQLException {
		ConnectionFactory factory = new ConnectionFactory();
		final Connection con = factory.recuperaConexion();
		
		try(con){
		  
			final PreparedStatement statement = con.prepareStatement("SELECT ID, NOMBRE, DESCRIPCION, CANTIDAD FROM PRODUCTO");
	        
			try(statement){
			statement.execute();

	        ResultSet resultSet = statement.getResultSet();

	        List<Map<String, String>> resultado = new ArrayList<>();

	      //leemos el contendio para agregrlo a un listado
	        while (resultSet.next()) {
	            Map<String, String> fila = new HashMap<>();
	            fila.put("ID", String.valueOf(resultSet.getInt("ID")));
	            fila.put("NOMBRE", resultSet.getString("NOMBRE"));
	            fila.put("DESCRIPCION", resultSet.getString("DESCRIPCION"));
	            fila.put("CANTIDAD", String.valueOf(resultSet.getInt("CANTIDAD")));

	            resultado.add(fila);//agremaos a resultado
	        }
			
		
		return resultado;
	}}
	}
	
    public void guardar(Map<String, String> producto) throws SQLException {
		String nombre = producto.get("NOMBRE");
		String descripcion = producto.get("DESCRIPCION");
    	Integer cantidad = Integer.valueOf(producto.get("CANTIDAD"));
    	Integer maximoCantidad = 50;
    	
    	final Connection con = new ConnectionFactory().recuperaConexion();
    	
    	try(con){
    	//para tener el control de la conexion:
    	con.setAutoCommit(false);
		
		//Statement statement = con.createStatement();
		final PreparedStatement statement = con.prepareStatement("INSERT INTO PRODUCTO(nombre, descripcion,cantidad) "
				+"VALUES(?, ?, ?)",
				Statement.RETURN_GENERATED_KEYS);
		
		try(statement){
			//solo se pueden guardar 50 unidades por cada registro:
				do {
					int cantidadParaGuardar = Math.min(cantidad, maximoCantidad);
					
					ejecutaRegistro(nombre, descripcion, cantidadParaGuardar, statement);
				
					cantidad -= maximoCantidad;
				}while(cantidad>0);
				
				//la transaccion guarda todo o no guarda nada
				con.commit();//para garantizar que todos comandos del loop se ejecuten correctamente
				System.out.println("COMMIT");
			}catch(Exception e) {
				con.rollback();
				System.out.println("ROLLBACK");
			}
    	}

	}


	private void ejecutaRegistro(String nombre, String descripcion, Integer cantidad, PreparedStatement statement)
			throws SQLException {
		statement.setString(1, nombre);
		statement.setString(2, descripcion);
		statement.setInt(3, cantidad);
		
		statement.execute();
		
		//me cierra todas los conexiones
		final ResultSet resultSet = statement.getGeneratedKeys();
		
		try(resultSet){
			while(resultSet.next()) {
				System.out.println(String.format(
						"fue insertado el producto de ID %d ",
						resultSet.getInt(1)));
				
			}
		}
	}
}
