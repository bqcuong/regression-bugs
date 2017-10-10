package resources.others.bug30;

import java.sql.Connection;
import java.sql.SQLException;

public class DemoUsingGetterBug30 {
	
	private Connection connection;
	
	public DemoUsingGetterBug30() {
		super();
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public void badMethod(String badParam) throws SQLException {		
		// This should fire a violation
		getConnection().prepareStatement("select * from table where field='" + badParam + "'");
	}
}
