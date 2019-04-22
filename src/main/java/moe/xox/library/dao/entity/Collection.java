package moe.xox.library.dao.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "collection")
public class Collection {
  @Id
  @GeneratedValue(strategy= GenerationType.IDENTITY)
  private Long collectionId;
  private Long userId;
  private Long bookMessageId;
  private java.sql.Timestamp createTime;

}
