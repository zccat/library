package moe.xox.library.dao.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDate;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user")
public class User {

  @Id
  @GeneratedValue(strategy= GenerationType.IDENTITY)
  private Long userId;
  private String email;
  private String nickName;
//  @Column(name = "img_name")
  private String imgName;
  private String password;
  //  private Long roleId;

  private LocalDate birthday;
  private String realName;
  private Long grade;
  private String department;
  private String major;
  private Long sex;

}
