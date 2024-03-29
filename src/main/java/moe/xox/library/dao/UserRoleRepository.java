package moe.xox.library.dao;

import com.alibaba.fastjson.JSONObject;
import moe.xox.library.controller.vo.ReturnBean;
import moe.xox.library.dao.entity.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRoleRepository  extends JpaRepository<UserRole,Long> {

    UserRole findUserRoleByUserId(Long userId);


    /**
     * 此处等待修改
     * @param pageable
     * @return
     */
    @Query(nativeQuery = true,value = "select history_id as historyId,\n" +
            "       history.book_message_id as bookMessageId,\n" +
            "       name as name,\n" +
            "       kind_name as kindName,\n" +
            "       author as author,\n" +
            "       book_message.img_name as imgName,\n" +
            "       publisher as publisher,\n" +
            "       history.create_time   as historyTime,\n" +
            "       introduction as intorduciton,\n" +
            "       ISBN\n" +
            "from history left join book_message on history.book_message_id = book_message.book_message_id\n" +
            "left join book_kind on book_kind.kind_id = book_message.kind_id\n" +
            "where user_id = :userId and book_message.status = true  \n" +
            "order by history.create_time desc limit 50")
    List<JSONObject> listUserHistory(@Param("userId") Long userId);
}
