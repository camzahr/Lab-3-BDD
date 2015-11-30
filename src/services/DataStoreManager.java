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
  
	private static final String CREATE_TABLE_ACCOUNT = "CREATE TABLE account ("
			+ "aid INTEGER NULL," + "balance DOUBLE,"
			+ "PRIMARY KEY (aid)) ENGINE=InnoDB;";

	private static final String CREATE_TABLE_OPERATION = "CREATE TABLE operation ("
			+ "oid INTEGER NOT NULL AUTO_INCREMENT,"
			+ "account_id INTEGER NULL,"
			+ "amount DOUBLE,"
			+ "date TIMESTAMP,"
			+ "PRIMARY KEY (oid),"
			+ "FOREIGN KEY (account_id) REFERENCES account(aid) "
			+ "ON DELETE CASCADE" + ") ENGINE=InnoDB;";

	private static final String TRIGGERS_TABLE_CHECK_BALANCE = "CREATE TRIGGER check_balance BEFORE UPDATE "
			+ "ON account FOR EACH ROW "
			+ "BEGIN "
			+ "IF NEW.balance < 0 "
			+ "THEN "
			+ "SIGNAL SQLSTATE '11111' "
			+ "SET MESSAGE_TEXT = 'Impossible transaction !';"
			+ "END IF;"
			+ "END";

	private static final String TRIGGERS_TABLE_INSERT_OPERATION = "CREATE TRIGGER insert_operation AFTER UPDATE "
			+ "ON account FOR EACH ROW "
			+ "BEGIN "
			+ "INSERT INTO operation (account_id, amount) VALUE (NEW.aid, NEW.balance - OLD.balance); "
			+ "END";

	private static final String DROP_TABLE_OPERATION = "DROP TABLE IF EXISTS operation";
	private static final String DROP_TABLE_ACCOUNT = "DROP TABLE IF EXISTS account";
	private static final String DROP_TRIGGER_CHECK_UPDATE = "DROP TRIGGER IF EXISTS check_balance";
	private static final String DROP_TRIGGER_INSERT_OPERATION = "DROP TRIGGER IF EXISTS insert_operation";

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
		
		//DROP TRIGGER
		PreparedStatement dropTrigger = myCon.prepareStatement(DROP_TRIGGER_CHECK_UPDATE); 
		dropTrigger.executeUpdate();
		PreparedStatement dropTrigger1 = myCon.prepareStatement(DROP_TRIGGER_INSERT_OPERATION); 
		dropTrigger1.executeUpdate();
		
		//DROP TABLES
		PreparedStatement dropTable1 = myCon.prepareStatement(DROP_TABLE_OPERATION); 
		dropTable1.executeUpdate();
		PreparedStatement dropTable = myCon.prepareStatement(DROP_TABLE_ACCOUNT); 
		dropTable.executeUpdate();

		//CREATE DB
		stat1.executeUpdate(CREATE_TABLE_ACCOUNT);
		stat1.executeUpdate(CREATE_TABLE_OPERATION);
		stat1.executeUpdate(TRIGGERS_TABLE_CHECK_BALANCE);
		stat1.executeUpdate(TRIGGERS_TABLE_INSERT_OPERATION);
		
	} catch (SQLException e) {
		e.printStackTrace();
		throw new DataStoreException(e);
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
	  try {
		Statement stat1 = myCon.createStatement();
		stat1.execute("INSERT INTO `account` (`aid`, `balance`) VALUES ("+number+", 0)");
		return true;
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		throw new DataStoreException(e);
	}
    
    
    
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
	  double balance = 0;

		try {
			
			PreparedStatement getBalance = myCon.prepareStatement("SELECT balance FROM account WHERE aid = ?");
			getBalance.setInt(1, number);
			ResultSet result = getBalance.executeQuery();

			//result.beforeFirst();
			while (result.next()) { // If we don't enter the while it means there is no account with this id -> Return -1.0

				return result.getDouble("balance"); // Getting the value of the "solde" and return It
				 
			}
			result.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new DataStoreException(e);
		}
		
		return -1.0; // It Failed
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
	  
		try {
			PreparedStatement add_balance = myCon.prepareStatement("UPDATE account SET balance = ? WHERE aid = ?");
			double newBalance = 0;
			newBalance = this.getBalance(number) + amount;
			//System.out.println("New balance : "+newBalance);
			add_balance.setDouble(1, newBalance);
			add_balance.setInt(2, number);
			add_balance.executeUpdate();
			
			newBalance = this.getBalance(number);
			System.out.println("New balance AFTER : "+newBalance);
			double balance=this.getBalance(number);
			if (balance<0)
				return -1.0;
			else 
				return balance;
				
			//connexion.commit();
		} catch (SQLException e) {
			throw new DataStoreException(e);
			
			
		}
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
	  
		if(amount<0)
		{
			return false;
		}
			
		
		else if (this.getBalance(from)>amount){
			
				if (this.addBalance(from, (-1)*amount)<0)
				{
					return false;
				}
					
							
				//Only if withdrawal succeeds
				else{
					if (this.addBalance(to, amount)>0)
						return true;
					
				}
			
		} 
	
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
	  try {
		  PreparedStatement get_balance 	= myCon.prepareStatement("SELECT accounts SET solde = ? WHERE aid = ?");
			add_balance.setInt(1, number);
			ResultSet result = this.get_operations.executeQuery();
			
			result.beforeFirst();
			while (result.next()){
				if(from==null)
					from=new Date(1800, 01, 01, 0, 0, 0);
				if(to==null)
					to= new Date();
					//to=new Date(2500, 12, 30, 23, 59,59);
				
				if(result.getDate(date).before(to) && result.getDate(date).after(from))
				{
					list.add(new Operation(result.getInt(id), result.getDouble(amount), result.getDate(date)));
				}
			}
			
			result.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new DataStoreException(e);
		}
		return list;
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
