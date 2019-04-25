package moe.xox.library.controller;


import com.alibaba.fastjson.JSONObject;
import moe.xox.library.controller.vo.ReturnBean;
import moe.xox.library.dao.CollectionRepository;
import moe.xox.library.dao.UserRepository;
import moe.xox.library.dao.UserRoleRepository;
import moe.xox.library.dao.entity.Collection;
import moe.xox.library.dao.entity.User;
import moe.xox.library.dao.entity.UserRole;
import moe.xox.library.utils.ShiroUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@RestController
@RequestMapping("yonghuguanli")
public class UserController extends BaseController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserRoleRepository userRoleRepository;

    CollectionRepository collectionRepository;

    /**
     * 分页!
     * 返回User表中信息
     * method = RequestMethod.GET
     *
     * @return json
     */
    @RequestMapping(method = RequestMethod.GET)
    public ReturnBean listAllUser(Integer page, Integer limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<JSONObject> userPage = userRepository.listAllUser(pageable);
        List<JSONObject> userList = userPage.getContent();
        for (JSONObject object : userList) {
            String birrthday = object.get("birthday") == null ? "空" : (object.get("birthday").toString().substring(0, 10));
            object.put("birthday", birrthday);

        }
        return getSuccess("success", userList, userPage.getTotalElements());
    }

    /**
     * 向User表中新增一条用户信息
     * 反馈一条消息
     *
     * @return msg
     * <p>
     * <p>
     * private Long userId;
     * private String email;
     * private String nickName;
     * private String password;
     * //  private Long roleId;
     * private LocalDate birthday;
     * private String realName;
     * private Long grade;
     * private String department;
     * private String major;
     * private Long sex;
     */
    @RequestMapping(path = "addUser", method = RequestMethod.POST)
    public ReturnBean addUser(String email, String nickName, String password, String birthday, String realName, Long grade, String department, String major, Long sex, Long roleId) {

        birthday += " 00:00:00";
//        LocalDateTime.parse(birthday).toLocalDate();
        User user = new User(null, email, nickName, password, LocalDateTime.parse(birthday, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).toLocalDate(), realName, grade, department, major, sex);
        user = userRepository.save(user);
        UserRole userRole = new UserRole(user.getUserId(), roleId, true);
        userRoleRepository.save(userRole);
        return getSuccess("success");
    }

    /**
     * 从User表中删除几条用户信息
     *
     * @return
     */
    @RequestMapping(path = "deleteUser", method = RequestMethod.POST)
    public ReturnBean deleteUser(@RequestBody JSONObject object) {

        if (object == null || !object.containsKey("list"))
            return getFailure("请选择正确的信息");
        List<Integer> list = (List<Integer>) object.get("list");
        for (Integer id : list) {
            User user = new User();
            user.setUserId(id.longValue());
            userRepository.delete(user);
        }

        return getSuccess("success");
    }

    /**
     * 修改用户信息
     *
     * @param userId     用户ID
     * @param roleId     角色ID
     * @param email      邮箱
     * @param nickName   昵称
     * @param password   密码
     * @param birthday
     * @param realName
     * @param grade
     * @param department
     * @param major
     * @param sex
     * @return
     */
    @RequestMapping(path = "updateUser", method = RequestMethod.POST)
    @Transactional
    public ReturnBean updateUser(Long userId, Long roleId, String email, String nickName, String password, String birthday, String realName, Long grade, String department, String major, Long sex) {
        birthday += " 00:00:00";
        User oldUser = userRepository.findByUserId(userId);
        User user = new User(userId, email, nickName, oldUser.getPassword(), LocalDateTime.parse(birthday, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).toLocalDate(), realName, grade, department, major, sex);
//        userRepository.updateUserRole( userId,  roleId);

        if (password != null || !password.equals(""))
            user.setPassword(password);
        UserRole userRole = userRoleRepository.findUserRoleByUserId(userId);
        if (userRole == null)
            userRole = new UserRole();
        userRole.setRoleId(roleId);
        userRole.setUserId(userId);
        userRole.setStatus(true);
        userRoleRepository.save(userRole);
        userRepository.save(user);
        return getSuccess("success");
    }


    /**
     * 通过用户ID查询用户的详细信息
     */
    public ReturnBean getUserInfoByUserId(Long userId) {
        User user = userRepository.findByUserId(userId);
        return getFailure("success", user);
    }

    /**
     * 通过用户邮箱查询用户
     */
    public ReturnBean getUserInfoByEmail(String email) {
        User user = userRepository.findByEmail(email);
        return getFailure("success", user);
    }

    @RequestMapping("getCurrentUserInfo")
    public ReturnBean getCurrentUserInfo() {
        Long userId = ShiroUtils.getUserId();
        User user = userRepository.findByUserId(userId);
        return getSuccess("OK", user, 1);
    }


    /**
     * 查询用户的收藏
     */
    @RequestMapping("listUserCollection")
    public ReturnBean listUserCollection() {
//        Subject subject = SecurityUtils.getSubject();
//        User user = (User) subject.getSession().getAttribute("user");
//        List<JSONObject> list = userRepository.listUserCollection(user.getUserId());
//        System.out.println(ShiroUtils.getUserId());
        List<JSONObject> list = userRepository.listUserCollection(ShiroUtils.getUserId());
        return getSuccess("OK", list, list.size());
    }


    /**
     * 查询用户的浏览历史
     */
    @RequestMapping("listUserHistory")
    public ReturnBean listUserHistory(int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<JSONObject> list = userRoleRepository.listUserHistory(pageable);
        return getSuccess("OK",list.getContent(),list.getTotalElements());
    }

    /**
     * 收藏一本书
     */
    @RequestMapping("collectionBook")
    public ReturnBean collectionBook(Long bookMessageId) {
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getSession().getAttribute("user");
        Collection oldCollection = collectionRepository.findCollectionByUserIdAndBookMessageId(user.getUserId(), bookMessageId);
        if (oldCollection != null)
            return getSuccess("您已收藏过该书啦");
        Collection collection = new Collection(null, user.getUserId(), bookMessageId, LocalDateTime.now());
        collectionRepository.save(collection);
        return getSuccess("收藏成功");
    }

    /**
     * 删除一本书  会确实是该用户删除自己的书
     * @param collectionId 收藏ID
     * @return 成功或者失败
     */
    @RequestMapping("deleteCollectionById")
    public ReturnBean deleteCollectionById(Long collectionId){
        Collection collection = collectionRepository.findCollectionByCollectionId(collectionId);
        if(!collection.getUserId().equals(ShiroUtils.getUserId()))
            return getFailure("请确认这是您的收藏,不要尝试删除不属于您的接口");
        collectionRepository.delete(collection);
        return getSuccess();
    }



}
