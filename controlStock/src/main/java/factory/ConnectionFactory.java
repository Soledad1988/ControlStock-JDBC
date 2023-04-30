package factory;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//esta clase solo se encrgara de la conexion a la bd
public class ConnectionFactory {

	public Connection recuperaConexion() throws SQLException {
		
		return DriverManager.getConnection(
                "jdbc:mysql://localhost/control_de_stock?useTimeZone=true&serverTimeZone=UTC",
                "root",
                "Melek2010*");
	}
}
