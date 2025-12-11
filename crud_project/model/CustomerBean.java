package crud_project.model;

import javafx.beans.property.*;
import lombok.Builder;


public class CustomerBean {
    private LongProperty id;
    private StringProperty firstName;
    private StringProperty lastName;
    private StringProperty middleInitial;
    private StringProperty street;
    private StringProperty city;
    private StringProperty state;
    private IntegerProperty zip;
    private LongProperty phone;
    private StringProperty email;
    private StringProperty password;


    public CustomerBean() {
    }

    public CustomerBean(Long id, String firstName, String lastName, String middleInitial, String street, String city, String state, int zip, long phone, String email, String password) {

        this.id = new SimpleLongProperty(id);
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.middleInitial = new SimpleStringProperty(middleInitial);
        this.street = new SimpleStringProperty(street);
        this.city = new SimpleStringProperty(city);
        this.state = new SimpleStringProperty(state);
        this.zip = new SimpleIntegerProperty(zip);
        this.phone = new SimpleLongProperty(phone);
        this.email = new SimpleStringProperty(email);
        this.password = new SimpleStringProperty(password);

    }



    public long getId() {
        return id.get();
    }

    public LongProperty idProperty() {
        return id;
    }

    public void setId(long id) {
        this.id.set(id);
    }

    public String getFirstName() {
        return firstName.get();
    }

    public StringProperty firstNameProperty() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public String getLastName() {
        return lastName.get();
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    public String getMiddleInitial() {
        return middleInitial.get();
    }

    public StringProperty middleInitialProperty() {
        return middleInitial;
    }

    public void setMiddleInitial(String middleInitial) {
        this.middleInitial.set(middleInitial);
    }

    public String getStreet() {
        return street.get();
    }

    public StringProperty streetProperty() {
        return street;
    }

    public void setStreet(String street) {
        this.street.set(street);
    }

    public String getCity() {
        return city.get();
    }

    public StringProperty cityProperty() {
        return city;
    }

    public void setCity(String city) {
        this.city.set(city);
    }

    public String getState() {
        return state.get();
    }

    public StringProperty stateProperty() {
        return state;
    }

    public void setState(String state) {
        this.state.set(state);
    }

    public int getZip() {
        return zip.get();
    }

    public IntegerProperty zipProperty() {
        return zip;
    }

    public void setZip(int zip) {
        this.zip.set(zip);
    }

    public long getPhone() {
        return phone.get();
    }

    public LongProperty phoneProperty() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone.set(phone);
    }

    public String getEmail() {
        return email.get();
    }

    public StringProperty emailProperty() {
        return email;
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public String getPassword() {
        return password.get();
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public void setPassword(String password) {
        this.password.set(password);
    }
}

