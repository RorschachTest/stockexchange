package entities;

public class User {
    private final String userId;
    private final String username;
    private final String emailId;
    private final String phoneNumber;
    
    public User(String userId, String username, String emailId, String phoneNumber) {
        this.userId = userId;
        this.username = username;
        this.emailId = emailId;
        this.phoneNumber = phoneNumber;
    }

    public String getUserId() {
        return userId;
    }
    public String getUsername() {
        return username;
    }
    public String getEmailId() {
        return emailId;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((userId == null) ? 0 : userId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        if (userId == null) {
            if (other.userId != null)
                return false;
        } else if (!userId.equals(other.userId))
            return false;
        return true;
    }
    
    
}