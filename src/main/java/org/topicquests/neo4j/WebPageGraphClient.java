/**
 * 
 */
package org.topicquests.neo4j;

import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;

import static org.neo4j.driver.v1.Values.parameters;

import org.neo4j.driver.v1.Record;
import org.topicquests.support.ResultPojo;
import org.topicquests.support.RootEnvironment;
import org.topicquests.support.api.IResult;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

/**
 * @author jackpark
 * Social network for web pages
 * 
 * @see https://github.com/neo4j/neo4j-java-driver/blob/2.0/examples/src/main/java/org/neo4j/docs/driver/PassBookmarkExample.java
 * @see https://github.com/neo4j/neo4j-java-driver/blob/2.0/examples/src/main/java/org/neo4j/docs/driver/ResultConsumeExample.java
 * @see https://github.com/neo4j/neo4j-java-driver/blob/2.0/examples/src/main/java/org/neo4j/docs/driver/ReadWriteTransactionExample.java
 * 
 */
public class WebPageGraphClient extends BaseNeoClient {

	/**
	 * @param env
	 */
	public WebPageGraphClient(RootEnvironment env) {
		super(env);
	}

	/**
	 * Add a new graph node (WebPage)
	 * @param title
	 * @param url
	 * @return
	 */
	public IResult addWebPage(final String title, final String url) {
		IResult result = new ResultPojo();
		try ( Session session = driver.session() )
        {
            session.writeTransaction( new TransactionWork<Void>()
            {
                @Override
                public Void execute( Transaction tx )
                {
                   return  _addWebPage(result, tx, title, url);
                }
            } );
        }
		
		return result;
	}
	

	private Void _addWebPage( IResult result, final Transaction tx, final String title, final String url ) {
        tx.run( "CREATE (:WebPage {title: $title, url: $url})", parameters( "title", title, "url", url ) );
        return null;
    }
	
	/**
	 * <p>Link two graph nodes (WebPages)</p>
	 * <p>It is possible that one or the other <code>url</code> is not yet in the graph</p>
	 * @param url1
	 * @param url2
	 * @return
	 */
	public IResult linkWebPages(final String url1, final String url2 ) {
		IResult result = new ResultPojo();
		try ( Session session = driver.session() )
        {
            session.writeTransaction( new TransactionWork<Void>()
            {
                @Override
                public Void execute( Transaction tx )
                {
                   return  _linkWebPages(result, tx, url1, url2);
                }
            } );
        }
		return result;
	}
	
	private Void _linkWebPages( IResult result, final Transaction tx, final String url1, final String url2 ) {
		environment.logDebug("Linking "+url1+" "+url2);
		try {
			StatementResult s = tx.run( "MATCH (a:WebPage {url: $url_1}) " +
	                        			"MATCH (b:WebPage {url: $url_2}) " +
	                        "CREATE (a)-[:LINKS_TO]->(b)",
	                parameters( "url_1", url1, "url_2", url2 ) );
		} catch (Exception e) {
			e.printStackTrace();
			environment.logError(e.getMessage()+" "+url1+" "+url2, e);
			result.addErrorString(e.getMessage()+" "+url1+" "+url2);
		}
		return null;
	}
	
	/**
	 * List all the node-links_to-node pairs
	 * @return
	 */

	public IResult listLinksTo( ) {
		IResult result = new ResultPojo();
		try ( Session session = driver.session() )
        {
            session.writeTransaction( new TransactionWork<Void>()
            {
                @Override
                public Void execute( Transaction tx )
                {
                   return  _listLinksTo(result, tx);
                }
            } );
        }

		return result;
	}
	
	/**
	 * List all the node-links_to-node pairs
	 * @param tx
	 * @return
	 */
	private Void _listLinksTo( IResult result, final Transaction tx ) {
		JSONArray ja = new JSONArray();
		result.setResultObject(ja);
        StatementResult rx = tx.run( "MATCH (a)-[:LINKS_TO]->(b) RETURN a.url, b.url" );
        while ( rx.hasNext() )
        {
            Record record = rx.next();
            JSONObject jo = new JSONObject();
            jo.put("from", record.get( "a.url" ).asString());
            jo.put("to", record.get( "b.url" ).toString());
            ja.add(jo);
        }
        return null;
	}
}
