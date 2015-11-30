package services;

import java.util.Date;
import java.util.List;

import model.Operation;

import java.sql.*;

/**
 * Provides the application with high-level methods to access the persistent
 * data store. The class hides the fact that data is stored in a RDBMS and all
 * the complex SQL machinery required to access it.
 * <p>
 * <b>Note: DO NOT alter this class' interface.</b>
 *
 * @author Jean-Michel Busca
 *
 */
public class DataStoreManager {

  //
  // CLASS FIELDS
  //
	private Connection myCon;
  // ...

  // example of a create table statement executed by createDB()
  private static final String CREATE_TABLE_DUMMY = "create table DUMMY ("
          + "ATT int, " + "primary key (ATT)" + ")";
  private static final String DROP_TABLE_ACCOUNT = "DROP TABLE IF EXISTS account";
  private static final String DROP_TABLE_OPERATION = "DROP TABLE IF EXISTS operation";
  private static final String DROP_TRIGGER_CHECK_UPDATE = "DROP TRIGGER IF EXISTS check_update";
  private static final String DROP_TRIGGER_INSERT_OPERATION = "DROP TRIGGER IF EXISTS insert_operation";

  private static final String CREATE_TABLE_OPERATION = "create table operation ("
          + "ATT int, " + "primary key (ATT)" + ")";
  private static final String CREATE_TABLE_ACCOUNT = "create table operation ("
          + "ATT int, " + "primary key (ATT)" + ")";

  /**
   * Creates a new <code>DataStoreManager</code> object that connects to the
   * specified database, using the specified login and password.
   * <p>
   * The constructor creates a dedicated SQL connection to the database. This
   * connection will later be used to execute the SQL statements required by
   * high-level methods.
   *
   * @param url
   *          the url of the database to connect to
   * @param user
   *          the login to use
   * @param password
   *          the password
   * @throws DataStoreException
   *           if an unrecoverable error occurs
   */
  public DataStoreManager(String url, String user, String password)
          throws DataStoreException {
	  
	  try {
		Class.forName("com.mysql.jdbc.Driver");
		myCon = DriverManager.getConnection(url, user, password);
	} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    // TODO Auto-generated method stub
 catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }

  /**
   * Creates the schema of the bank's database. This includes all the schema
   * elements: tables, triggers, views, etc. If the database already exists,
   * this method first deletes it using "drop" statements. The database is empty
   * after this method returns.
   * <p>
   * The method executes a sequence of hard-coded SQL statements, as shown
   * above.
   *
   * @throws DataStoreException
   *           if an unrecoverable error occurs
   */
  public void createDB() throws DataStoreException {
	  try {
		Statement stat1 = myCon.createStatement();
		PreparedStatement dropTable = myCon.prepareStatement(DROP_TABLE_ACCOUNT); 
		PreparedStatement dropTable1 = myCon.prepareStatement(DROP_TABLE_OPERATION); 
	} catch (SQLException e) {
		e.printStackTrace();
	}
	  
  }

  /**
   * Creates a new account with the specified number. This number uniquely
   * identifies bank accounts.
   *
   * @param number
   *          the number of the account
   * @return <code>true</code> if the method succeeds and <code>false</code>
   *         otherwise
   * @throws DataStoreException
   *           if an unrecoverable error occurs
   *
   */
  public boolean createAccount(int number) throws DataStoreException {
    // TODO Auto-generated method stub
    return false;
  }

  /**
   * Returns the balance of the specified account.
   *
   * @param number
   *          the number of the account
   * @return the balance of the account, or -1.0 if the account does not exist
   * @throws DataStoreException
   *           if an unrecoverable error occurs
   */
  public double getBalance(int number) throws DataStoreException {
    // TODO Auto-generated method stub
    return 0;
  }

  /**
   * Adds the specified amount to the specified account. A call to this method
   * performs a deposit if the amount is a positive value, and a withdrawal
   * otherwise. A debit operation without insufficient funds must be rejected.
   *
   * @param number
   *          the number of the account
   * @param amount
   *          the amount to add to the account's balance
   * @return the new balance of the account, or -1.0 if the withdrawal could not
   *         be performed
   * @throws DataStoreException
   *           if an unrecoverable error occurs
   */
  public double addBalance(int number, double amount) throws DataStoreException {
    // TODO Auto-generated method stub
    return 0;
  }

  /**
   * Transfers the specified amount between the specified accounts.
   *
   * @param from
   *          the number of the debited account
   * @param to
   *          the number of the credited account
   * @param amount
   *          the amount to transfert
   * @return <code>true</code> if the method succeeds and <code>false</code>
   *         otherwise
   * @throws DataStoreException
   *           if an unrecoverable error occurs
   */
  public boolean transfer(int from, int to, double amount)
          throws DataStoreException {
    // TODO Auto-generated method stub
    return false;
  }

  /**
   * Returns the list of operations on the specified account in the specified
   * time interval.
   *
   * @param number
   *          the number of the account;
   * @param from
   *          start date/time (inclusive) of time interval; from the beginning
   *          of time if <code>null</code>
   * @param to
   *          end date/time (inclusive) of time interval; to the end of time if
   *          <code>null</code>
   * @return the list of operations on the account in the time interval
   * @throws DataStoreException
   *           if an unrecoverable error occurs
   */
  public List<Operation> getOperations(int number, Date from, Date to)
          throws DataStoreException {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * Closes this manager and releases all related ressources. This method must
   * be called when this manager is no longer used.
   *
   * @throws DataStoreException
   *           if an unrecoverable error occurs
   */
  public void close() throws DataStoreException {
    // TODO Auto-generated method stub
  }

}
