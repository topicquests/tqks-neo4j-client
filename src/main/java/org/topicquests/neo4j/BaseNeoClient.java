/**
 * 
 */
package org.topicquests.neo4j;

import org.topicquests.support.RootEnvironment;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;

import static org.neo4j.driver.v1.Values.parameters;
/**
 * @author jackpark
 * <p>This is a Base Client, to be extended by Application clients</p>
 * 
 * @see https://github.com/neo4j/neo4j-java-driver/blob/2.0/examples/src/main/java/org/neo4j/docs/driver/HostnameVerificationExample.java
 * @see https://github.com/neo4j/neo4j-java-driver/blob/2.0/examples/src/main/java/org/neo4j/docs/driver/ReadWriteTransactionExample.java
 * 
 */
public class BaseNeoClient {
	protected RootEnvironment environment;
	private final String URL, UNAME, PWD;
	protected final Driver driver;
	
	/**
	 * @param env
	 */
	public BaseNeoClient(RootEnvironment env) {
		environment = env;
		UNAME = environment.getStringProperty("UName");
		PWD = environment.getStringProperty("Pwd");
		URL = environment.getStringProperty("Neo4J");
		driver = GraphDatabase.driver( URL, AuthTokens.basic( UNAME, PWD ) );
		environment.logDebug("NeoClient "+driver);
	}
	
	public boolean canConnect() {
        StatementResult result = driver.session().run( "RETURN 1" );
        return result.single().get( 0 ).asInt() == 1;
    }
	
	//public Transaction getWriteTransaction() {
	//	Transaction result = 
	//}
	
	public void close() {
		driver.close();
	}

}
