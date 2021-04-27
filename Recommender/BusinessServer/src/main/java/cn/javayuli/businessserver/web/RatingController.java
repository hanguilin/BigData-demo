package cn.javayuli.businessserver.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
     * @param user 用户
     * @param product 商品
     * @param score 分数
     * @return
     */
    @GetMapping("/rate")
    public String doRate(@RequestParam String user, @RequestParam String product, @RequestParam Double score) {
        LOGGER.info(PRODUCT_RATING_PREFIX + ":" + user +"|"+ product +"|"+ score +"|"+ System.currentTimeMillis()/1000);
        return "SUCCESS";
    }
}
