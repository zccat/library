package moe.xox.library.dao.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "advice")
public class Advice {
  @Id
  @GeneratedValue(strategy= GenerationType.IDENTITY)
  private Long adviceId;
  private Long userId;
  private String message;
  private java.sql.Timestamp createTime;

}
