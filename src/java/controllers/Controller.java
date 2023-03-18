package controllers;

import data.DB;
import beans.User;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

@SessionScoped
@Named(value = "controller")
public class Controller implements Serializable {
    
    private Integer userId;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String contactMessage;
    
    private User korisnik = new User();
    private Boolean loggedIn = false;
    private Boolean isAdmin = false;
    private Boolean isFetched = false;
    private List<User> korisnici = new ArrayList<>();

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getContactMessage() {
        return contactMessage;
    }

    public void setContactMessage(String contactMessage) {
        this.contactMessage = contactMessage;
    }

    public Boolean getLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(Boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public User getKorisnik() {
        return korisnik;
    }

    public void setKorisnik(User korisnik) {
        this.korisnik = korisnik;
    }

    public List<User> getKorisnici() {
        return korisnici;
    }

    public void setKorisnici(List<User> korisnici) {
        this.korisnici = korisnici;
    }
    
    public void logIn() throws IOException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = DB.getInstance().getConnection();
            if(con != null) {
                ps = con.prepareStatement("SELECT * FROM ROOT.USERS WHERE email = ? AND password = ?");
                ps.setString(1, email);
                ps.setString(2, password);
                rs = ps.executeQuery();
                if(rs.next()) {
                    korisnik.setUserId(rs.getInt("user_id"));
                    korisnik.setFirstName(rs.getString("first_name"));
                    korisnik.setLastName(rs.getString("last_name"));
                    korisnik.setEmail(rs.getString("email"));
                    korisnik.setAdminStatus(rs.getString("admin_status"));
                    loggedIn = true;
                    if(korisnik.getAdminStatus().equals("1")) {
                        isAdmin = true;
                    }
                    ps.close();
                    FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
                }
            }
        }
        catch(SQLException ex) {
            loggedIn = false;
        }
    }
    
    public void logOut() throws IOException {
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession)fc.getExternalContext().getSession(false);
        session.invalidate();
        loggedIn = false;
        isAdmin = false;
        FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
    }
    
    public String register() throws IOException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = DB.getInstance().getConnection();
            if(con != null) {
                ps = con.prepareStatement("SELECT * FROM ROOT.USERS WHERE email = ? AND password = ?");
                ps.setString(1, email);
                ps.setString(2, password);
                rs = ps.executeQuery();
                if(!rs.next()) {
                    PreparedStatement regPs;
                    regPs = con.prepareStatement("INSERT INTO ROOT.USERS(first_name, last_name, email, password) VALUES(?, ?, ?, ?)");
                    regPs.setString(1, firstName);
                    regPs.setString(2, lastName);
                    regPs.setString(3, email);
                    regPs.setString(4, password);
                    regPs.executeUpdate();
                    regPs.close();
                    ps.close();
                    return "success";
                }
            }
            return "failure";
        }
        
        catch(SQLException ex) {
            return "failure";
        }
    }
    
    public String contact() throws IOException {
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            con = DB.getInstance().getConnection();
            if(con != null) {
                ps = con.prepareStatement("INSERT INTO ROOT.CONTACT(contact_email, contact_content, contact_date) VALUES(?, ?, ?)");
                ps.setString(1, email);
                ps.setString(2, contactMessage);
                ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                ps.executeUpdate();
                ps.close();
                email = "";
                contactMessage = "";
                return "success";
            }
            return "failure";
        }
        
        catch(SQLException ex) {
            return "failure";
        }
    }
    
    public void fetchUsers() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = DB.getInstance().getConnection();
            if(con != null) {
                ps = con.prepareStatement("SELECT * FROM ROOT.USERS");
                rs = ps.executeQuery();
                while(rs.next() && !isFetched) {
                    User u = new User();
                    u.setUserId(rs.getInt("user_id"));
                    u.setFirstName(rs.getString("first_name"));
                    u.setLastName(rs.getString("last_name"));
                    u.setEmail(rs.getString("email"));
                    u.setAdminStatus(rs.getString("admin_status"));
                    korisnici.add(u);
                }
                isFetched = true;
            }
        }
        
        catch(SQLException ex) {
            System.out.println(ex);
        }
    }
    
    public String goToUserControl() {
        fetchUsers();
        return "admin-users";
    }
}
