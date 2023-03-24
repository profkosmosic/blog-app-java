package controllers;

import beans.Message;
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
    // All info we can get from the login, register and contact forms.
    private Integer userId;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String contactMessage;
    // All info we can get from the post writing form.
    private Integer postId;
    private String postTitle;
    private String postContent;
    private String postType;
    private Timestamp postDate;
    private Integer authorId;
    // All user info will be stored in this object.
    private User korisnik = new User();
    // Status checks.
    private Boolean loggedIn = false;
    private Boolean isAdmin = false;
    // Item lists.
    private List<User> korisnici = new ArrayList<>();
    private List<Message> poruke = new ArrayList<>();
    // Getters and Setters
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

    public List<Message> getPoruke() {
        return poruke;
    }

    public void setPoruke(List<Message> poruke) {
        this.poruke = poruke;
    }

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public Timestamp getPostDate() {
        return postDate;
    }

    public void setPostDate(Timestamp postDate) {
        this.postDate = postDate;
    }

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }
    // Methods
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
        korisnici.clear();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = DB.getInstance().getConnection();
            if(con != null) {
                ps = con.prepareStatement("SELECT * FROM ROOT.USERS");
                rs = ps.executeQuery();
                while(rs.next()) {
                    User u = new User();
                    u.setUserId(rs.getInt("user_id"));
                    u.setFirstName(rs.getString("first_name"));
                    u.setLastName(rs.getString("last_name"));
                    u.setEmail(rs.getString("email"));
                    u.setAdminStatus(rs.getString("admin_status"));
                    korisnici.add(u);
                }
                ps.close();
            }
        }
        
        catch(SQLException ex) {
            System.out.println(ex);
        }
    }
    
    public void deleteUser(int id) throws IOException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = DB.getInstance().getConnection();
            if(con != null) {
                ps = con.prepareStatement("DELETE FROM ROOT.USERS WHERE user_id = ?");
                ps.setInt(1, id);
                ps.executeUpdate();
                ps.close();
                goToUserControl();
            }
        }
        
        catch(SQLException ex) {
            System.out.println(ex);
        }
    }
    
    public void promoteUser(int id) throws IOException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = DB.getInstance().getConnection();
            if(con != null) {
                ps = con.prepareStatement("UPDATE ROOT.USERS SET admin_status = 1 WHERE user_id = ?");
                ps.setInt(1, id);
                ps.executeUpdate();
                ps.close();
                goToUserControl();
            }
        }
        
        catch(SQLException ex) {
            System.out.println(ex);
        }
    }
    
    public void fetchMessages() {
        poruke.clear();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = DB.getInstance().getConnection();
            if(con != null) {
                ps = con.prepareStatement("SELECT * FROM ROOT.CONTACT");
                rs = ps.executeQuery();
                while(rs.next()) {
                    Message m = new Message();
                    m.setContactId(rs.getInt("contact_id"));
                    m.setContactEmail(rs.getString("contact_email"));
                    m.setContactContent(rs.getString("contact_content"));
                    m.setContactDate(rs.getTimestamp("contact_date"));
                    poruke.add(m);
                }
                ps.close();
            }
        }
        
        catch(SQLException ex) {
            System.out.println(ex);
        }
    }
    
    public void deleteMessage(int id) throws IOException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = DB.getInstance().getConnection();
            if(con != null) {
                ps = con.prepareStatement("DELETE FROM ROOT.CONTACT WHERE contact_id = ?");
                ps.setInt(1, id);
                ps.executeUpdate();
                ps.close();
                goToAdminContact();
            }
        }
        
        catch(SQLException ex) {
            System.out.println(ex);
        }
    }
    
    public void goToUserControl() throws IOException {
        fetchUsers();
        FacesContext.getCurrentInstance().getExternalContext().redirect("admin-users.xhtml");
    }
    
    public void goToPostWriting() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect("admin-write.xhtml");
    }
    
    public void goToAdminContact() throws IOException {
        fetchMessages();
        FacesContext.getCurrentInstance().getExternalContext().redirect("admin-contact.xhtml");
    }
}
