package controllers;

import beans.Message;
import beans.Post;
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
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String contactMessage;
    // All info we can get from the post writing form.
    private String postTitle;
    private String postContent;
    private String postType;
    private Integer editId;
    private String editTitle;
    private String editContent;
    // All user info will be stored in this object.
    private User user = new User();
    private Post post = new Post();
    // Status checks.
    private Boolean loggedIn = false;
    private Boolean isAdmin = false;
    // Item lists.
    private List<User> users = new ArrayList<>();
    private List<Message> messages = new ArrayList<>();
    private List<Post> posts = new ArrayList<>();
    // Getters and Setters
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
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

    public String getEditTitle() {
        return editTitle;
    }

    public void setEditTitle(String editTitle) {
        this.editTitle = editTitle;
    }

    public String getEditContent() {
        return editContent;
    }

    public void setEditContent(String editContent) {
        this.editContent = editContent;
    }

    public Integer getEditId() {
        return editId;
    }

    public void setEditId(Integer editId) {
        this.editId = editId;
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
                    user.setUserId(rs.getInt("user_id"));
                    user.setFirstName(rs.getString("first_name"));
                    user.setLastName(rs.getString("last_name"));
                    user.setEmail(rs.getString("email"));
                    user.setAdminStatus(rs.getString("admin_status"));
                    loggedIn = true;
                    if(user.getAdminStatus().equals("1")) {
                        isAdmin = true;
                    }
                    ps.close();
                    email = "";
                    password = "";
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
                    email = "";
                    password = "";
                    firstName = "";
                    lastName = "";
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
    
    public void fetchUsers() throws IOException {
        users.clear();
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
                    users.add(u);
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
        
        try {
            con = DB.getInstance().getConnection();
            if(con != null) {
                ps = con.prepareStatement("DELETE FROM ROOT.USERS WHERE user_id = ?");
                ps.setInt(1, id);
                ps.executeUpdate();
                ps.close();
            }
            reloadUsers();
        }
        
        catch(SQLException ex) {
            System.out.println(ex);
        }
    }
    
    public void promoteUser(int id) throws IOException {
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            con = DB.getInstance().getConnection();
            if(con != null) {
                ps = con.prepareStatement("UPDATE ROOT.USERS SET admin_status = 1 WHERE user_id = ?");
                ps.setInt(1, id);
                ps.executeUpdate();
                ps.close();
            }
            reloadUsers();
        }
        
        catch(SQLException ex) {
            System.out.println(ex);
        }
    }
    
    public String getUserName(int id) throws IOException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = DB.getInstance().getConnection();
            if(con != null) {
                ps = con.prepareStatement("SELECT * FROM ROOT.USERS WHERE user_id = ?");
                ps.setInt(1, id);
                rs = ps.executeQuery();
                if(rs.next()) {
                    return rs.getString("first_name") + " " + rs.getString("last_name");
                }
                ps.close();
            }
            return "Error";
        }
        
        catch(SQLException ex) {
            return "Error";
        }
    }
    
    public void fetchMessages() throws IOException {
        messages.clear();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = DB.getInstance().getConnection();
            if(con != null) {
                ps = con.prepareStatement("SELECT * FROM ROOT.CONTACT ORDER BY contact_date DESC");
                rs = ps.executeQuery();
                while(rs.next()) {
                    Message m = new Message();
                    m.setContactId(rs.getInt("contact_id"));
                    m.setContactEmail(rs.getString("contact_email"));
                    m.setContactContent(rs.getString("contact_content"));
                    m.setContactDate(rs.getTimestamp("contact_date"));
                    messages.add(m);
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
        
        try {
            con = DB.getInstance().getConnection();
            if(con != null) {
                ps = con.prepareStatement("DELETE FROM ROOT.CONTACT WHERE contact_id = ?");
                ps.setInt(1, id);
                ps.executeUpdate();
                ps.close();
            }
            reloadMessages();
        }
        
        catch(SQLException ex) {
            System.out.println(ex);
        }
    }
    
    public void fetchPosts() throws IOException {
        posts.clear();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = DB.getInstance().getConnection();
            if(con != null) {
                ps = con.prepareStatement("SELECT * FROM ROOT.POSTS ORDER BY post_date DESC");
                rs = ps.executeQuery();
                while(rs.next()) {
                    Post p = new Post();
                    p.setPostId(rs.getInt("post_id"));
                    p.setPostTitle(rs.getString("post_title"));
                    p.setPostContent(rs.getString("post_content"));
                    p.setPostType(rs.getString("post_type"));
                    p.setPostDate(rs.getTimestamp("post_date"));
                    p.setAuthorId(rs.getInt("user_id"));
                    posts.add(p);
                }
                ps.close();
            }
        }
        
        catch(SQLException ex) {
            System.out.println(ex);
        }
    }
    
    public void getPost(int id) throws IOException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = DB.getInstance().getConnection();
            if(con != null) {
                ps = con.prepareStatement("SELECT * FROM ROOT.POSTS WHERE post_id = ?");
                ps.setInt(1, id);
                rs = ps.executeQuery();
                if(rs.next()) {
                    post.setPostId(id);
                    post.setPostTitle(rs.getString("post_title"));
                    post.setPostContent(rs.getString("post_content"));
                    post.setPostType(rs.getString("post_type"));
                    post.setPostDate(rs.getTimestamp("post_date"));
                    post.setAuthorId(rs.getInt("user_id"));
                }
                ps.close();
                FacesContext.getCurrentInstance().getExternalContext().redirect("post.xhtml");
            }
        }
        
        catch(SQLException ex) {
            System.out.println(ex);
        }
    }
    
    public String shortenPostContent(String content) {
        return content.substring(0, 99) + "...";
    }
    
    public String cleanUpDate(Timestamp date) {
        return date.toString().substring(0, 10);
    }
    
    public String addPost() throws IOException {
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            con = DB.getInstance().getConnection();
            if(con != null) {
                ps = con.prepareStatement("INSERT INTO ROOT.POSTS(post_title, post_content, post_type, post_date, user_id) VALUES(?, ?, ?, ?, ?)");
                ps.setString(1, postTitle);
                ps.setString(2, postContent);
                ps.setString(3, postType);
                ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                ps.setInt(5, user.getUserId());
                ps.executeUpdate();
                ps.close();
                postTitle = "";
                postContent = "";
                postType = "";
                return "success";
            }
            return "failure";
        }
        
        catch(SQLException ex) {
            return "failure";
        }
    }
    
    public void deletePost(int id) throws IOException {
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            con = DB.getInstance().getConnection();
            if(con != null) {
                ps = con.prepareStatement("DELETE FROM ROOT.POSTS WHERE post_id = ?");
                ps.setInt(1, id);
                ps.executeUpdate();
                ps.close();
            }
            reloadPosts();
        }
        
        catch(SQLException ex) {
            System.out.println(ex);
        }
    }
    
    public void getEditPost(int id) throws IOException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = DB.getInstance().getConnection();
            if(con != null) {
                ps = con.prepareStatement("SELECT * FROM ROOT.POSTS WHERE post_id = ?");
                ps.setInt(1, id);
                rs = ps.executeQuery();
                if(rs.next()) {
                    post.setPostId(id);
                    post.setPostTitle(rs.getString("post_title"));
                    post.setPostContent(rs.getString("post_content"));
                    post.setPostType(rs.getString("post_type"));
                    post.setPostDate(rs.getTimestamp("post_date"));
                    post.setAuthorId(rs.getInt("user_id"));
                    editId = post.getPostId();
                    editTitle = post.getPostTitle();
                    editContent = post.getPostContent();
                }
                ps.close();
                FacesContext.getCurrentInstance().getExternalContext().redirect("admin-edit.xhtml");
            }
        }
        
        catch(SQLException ex) {
            System.out.println(ex);
        }
    }
    
    public String editPost() throws IOException {
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            con = DB.getInstance().getConnection();
            if(con != null) {
                ps = con.prepareStatement("UPDATE ROOT.POSTS SET post_title = ?, post_content = ? WHERE post_id = ?");
                ps.setString(1, editTitle);
                ps.setString(2, editContent);
                ps.setInt(3, editId);
                ps.executeUpdate();
                ps.close();
                return "success";
            }
            return "failure";
        }
        
        catch(SQLException ex) {
            return "failure";
        }
    }
    
    public void reloadPosts() throws IOException {
        fetchPosts();
        FacesContext.getCurrentInstance().getExternalContext().redirect("posts.xhtml");
    }
    
    public void reloadUsers() throws IOException {
        fetchUsers();
        FacesContext.getCurrentInstance().getExternalContext().redirect("admin-users.xhtml");
    }
    
    public void reloadMessages() throws IOException {
        fetchMessages();
        FacesContext.getCurrentInstance().getExternalContext().redirect("admin-contact.xhtml");
    }
}