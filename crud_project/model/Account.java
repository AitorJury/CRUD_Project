/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crud_project.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import javafx.beans.property.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Entity representing bank accounts for customers. It contains the following
 * fields: account id, account type, account description, initial balance, initial balance date, 
 * current balance and credit limit. It also contains relational fields for getting
 * customers owning the account and movements or transactions made on the account.  
 * @author Javier Martín Uría
 */
@XmlRootElement
public class Account implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * Identification field for the account.
     */
    private final SimpleLongProperty id;
    /**
     * Type of the account.
     */
    private final SimpleObjectProperty<AccountType> type;
    /**
     * Description of the account.
     */
    private final SimpleStringProperty description;
    /**
     * Current balance of the account.
     */
    private final SimpleDoubleProperty balance;
    /**
     * Limit for the credit line. The balance can be negative but not below this
     * limit. Do note that the limit is stored always as a positive value. 
     */
    private final SimpleDoubleProperty creditLine;
    /**
     * Begin balance of the account. Normally it is set when opening the account.
     * It is useful to reconcile balance and movements in conjuction with its corresponding
     * timestamp.
     */
    private final SimpleDoubleProperty beginBalance;
    /**
     * Begin balance timestamp.
     */
    private final SimpleObjectProperty<Date> beginBalanceTimestamp;
    /**
     * Relational field containing Customers owning the account. 
     */
    private Set<Customer> customers;
    /**
     * Relational field containing the list of movements on the account.
     */
    private Set<Movement> movements;
    
    // Constructor vacío.
    public Account() {
        this.id = new SimpleLongProperty();
        this.type = new SimpleObjectProperty<>();
        this.description = new SimpleStringProperty();
        this.balance = new SimpleDoubleProperty();
        this.creditLine = new SimpleDoubleProperty();
        this.beginBalance = new SimpleDoubleProperty();
        this.beginBalanceTimestamp = new SimpleObjectProperty<>();
    }
    
    
    
    /**
     * 
     * @return the id
     */
    @XmlElement
    public Long getId() {
        return id.get();
    }
    /**
     * 
     * @param id the id to be set
     */
    public void setId(Long id) {
        this.id.set(id);
    }

    /**
     * @return the type
     */
    @XmlElement
    public AccountType getType() {
        return type.get();
    }

    /**
     * @param type the type to set
     */
    public void setType(AccountType type) {
        this.type.set(type);
    }
    
    /**
     * @return the description
     */
    @XmlElement
    public String getDescription() {
        return description.get();
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description.set(description);
    }
    
    /**
     * @return the balance
     */
    @XmlElement
    public Double getBalance() {
        return balance.get();
    }

    /**
     * @param balance the balance to set
     */
    public void setBalance(Double balance) {
        this.balance.set(balance);
    }
    
    /**
     * Limit for the credit line. The balance can be negative but not below this
     * limit. Do note that the limit is stored always as a positive value.
     * @return the creditLine
     */
    @XmlElement
    public Double getCreditLine() {
        return creditLine.get();
    }

    /**
     * Limit for the credit line. The balance can be negative but not below this
     * limit. Do note that the limit is stored always as a positive value.
     * @param creditLine the creditLine to set
     */
    public void setCreditLine(Double creditLine) {
        this.creditLine.set(creditLine);
    }

    /**
     * Begin balance of the account. Normally it is set when opening the account.
     * It is useful to reconcile balance and movements in conjuction with its corresponding
     * timestamp.
     * @return the beginBalance
     */
    @XmlElement
    public Double getBeginBalance() {
        return beginBalance.get();
    }
    
    /**
     * Begin balance of the account. Normally it is set when opening the account.
     * It is useful to reconcile balance and movements in conjuction with its corresponding
     * timestamp.
     * @param beginBalance the beginBalance to set
     */
    public void setBeginBalance(Double beginBalance) {
        this.beginBalance.set(beginBalance);
    }
    
    /**
     * Begin balance timestamp.
     * @return the beginBalanceTimestamp
     */
    @XmlElement
    public Date getBeginBalanceTimestamp() {
        return beginBalanceTimestamp.get();
    }
    
    /**
     * Begin balance timestamp.
     * @param beginBalanceTimestamp the beginBalanceTimestamp to set
     */
    public void setBeginBalanceTimestamp(Date beginBalanceTimestamp) {
        this.beginBalanceTimestamp.set(beginBalanceTimestamp);
    }
    
    /**
     * Relational field containing Customers owning the account.
     * @return the customers
     */
    @XmlTransient
    public Set<Customer> getCustomers() {
        return customers;
    }
    
    /**
     * Relational field containing Customers owning the account.
     * @param customers the customers to set
     */
    public void setCustomers(Set<Customer> customers) {
        this.customers = customers;
    }

    /**
     * Relational field containing the list of movements on the account.
     * @return the movements
     */
    @XmlTransient
    public Set<Movement> getMovements() {
        return movements;
    }

    /**
     * Relational field containing the list of movements on the account.
     * @param movements the movements to set
     */
    public void setMovements(Set<Movement> movements) {
        this.movements = movements;
    }
    
    /**
     * Integer representation for Account instance.
     * @return 
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }
    
    /**
     * Compares two Account objects for equality. This method consider a Account 
     * equal to another Account if their id fields have the same value. 
     * @param object The other Account object to compare to.
     * @return true if ids are equals.
     */
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Account)) {
            return false;
        }
        Account other = (Account) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
    /**
     * Obtains a string representation of the Account.
     * @return The String representing the Account.
     */
    @Override
    public String toString() {
        return "serverside.entity.Account[ id=" + id + " ]";
    }
    
}
