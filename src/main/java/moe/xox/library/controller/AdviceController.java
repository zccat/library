package moe.xox.library.controller;


import com.alibaba.fastjson.JSONObject;
import moe.xox.library.controller.vo.ReturnBean;
import moe.xox.library.dao.AdviceRepository;
import moe.xox.library.dao.entity.Advice;
import moe.xox.library.utils.ShiroUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.print.DocFlavor;
import java.time.LocalDateTime;
import java.util.List;


@Controller
@RequestMapping("fankuiguanli")
public class AdviceController extends BaseController {
    @Autowired
    AdviceRepository adviceRepository;

    /**
     * 分页!
     * 返回Advice表中信息
     * method = RequestMethod.GET
     *
     * @return json
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ReturnBean listAllAdvice(Integer page, Integer limit,
                                    @RequestParam(value = "adviceId", required = false, defaultValue = "") String adviceId,
                                    @RequestParam(value = "userId", required = false, defaultValue = "") String userId,
                                    @RequestParam(value = "realName", required = false, defaultValue = "") String realName) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<JSONObject> advicePage = adviceRepository.findAllAdvice(adviceId,userId,realName,pageable);
        List<JSONObject> advice = advicePage.getContent();
        return getSuccess("success", advice, advicePage.getTotalElements());
    }

    /**
     * 向Advice表中新增一条信息
     * 反馈一条消息
     *
     * @return msg
     */
    @RequestMapping(path = "addAdvice", method = RequestMethod.POST)
    @ResponseBody
    public ReturnBean addAdvice(Advice advice) {
        Long userId = ShiroUtils.getUserId();
        advice.setUserId(userId);
        advice.setCreateTime(LocalDateTime.now());
        adviceRepository.save(advice);
        return getSuccess();
    }

    /**
     * 从Advice表中删除几条信息
     *
     * @return
     */
    @RequestMapping(path = "deleteAdvice", method = RequestMethod.POST)
    @ResponseBody
    public ReturnBean deleteAdvice(@RequestBody JSONObject object) {

        if (object == null || !object.containsKey("list"))
            return getFailure("请选择正确的信息");
        List<Integer> list = (List<Integer>) object.get("list");
        for (Integer integer : list) {
            Advice advice = new Advice();
            advice.setAdviceId(integer.longValue());
            adviceRepository.delete(advice);
        }
        return getSuccess();
    }

    /**
     * 修改Advice表中一条数据
     * @param advice
     * @return
     */
//    @RequestMapping(path = "updateAdvice",method = RequestMethod.POST)
//    @ResponseBody
//    public  ReturnBean updateAdvice(Advice advice){
//        adviceRepository.save(advice);
//        return getSuccess();
//    }

    /**
     * 出
     *
     * @param
     * @return
     */
    @RequestMapping(path = "handleAdvice", method = RequestMethod.POST)
    @ResponseBody
    public ReturnBean handleAdvice(Long adviceId) {
        Advice advice = adviceRepository.findAdviceByAdviceId(adviceId);
//        advice.setAdviceId();
        adviceRepository.save(advice);
        return getSuccess();
    }
}
