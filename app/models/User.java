package models;

import javax.persistence.*;
import java.security.NoSuchAlgorithmException;
import java.io.UnsupportedEncodingException;
@Entity
public class User 
{
     @Id
     @GeneratedValue(strategy=GenerationType.AUTO)
 public Long id;

 public String email;

public boolean isAdmin;

public byte[] shaPassword;

 public void setPassword(String password) {
   this.shaPassword = getSha512(password);
 }

 public void setEmail(String email) {
   this.email = email.toLowerCase();
 }

public void setisAdmin(boolean  type)
{
    this.isAdmin=type;
}
 public static byte[] getSha512(String value) {
   try {
     return java.security.MessageDigest.getInstance("SHA-512").digest(value.getBytes("UTF-8"));
   }
   catch (NoSuchAlgorithmException e) {
     throw new RuntimeException(e);
   }
   catch (UnsupportedEncodingException e) {
     throw new RuntimeException(e);
   }
   
 }
    
    
}