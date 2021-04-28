package cn.javayuli.businessserver.web;

import cn.javayuli.businessserver.vo.UserRateVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 评分controller
 *
 * @author 韩桂林
 */
@RestController
public class RatingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RatingController.class);

    private static final String PRODUCT_RATING_PREFIX = "PRODUCT_RATING_PREFIX";

    /**
     * 用户对商品进行评分
     *
     * @param userRateVo 用户评分
     * @return
     */
    @PostMapping("/rate")
    public String doRate(@RequestBody UserRateVo userRateVo) {
        LOGGER.info(PRODUCT_RATING_PREFIX + ":" + userRateVo.getUser() +"|"+ userRateVo.getProduct() +"|"+ userRateVo.getScore() +"|"+ System.currentTimeMillis()/1000);
        return "SUCCESS";
    }
}
