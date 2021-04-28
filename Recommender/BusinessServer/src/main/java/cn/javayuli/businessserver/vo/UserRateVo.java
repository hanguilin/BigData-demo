package cn.javayuli.businessserver.vo;

/**
 * 用户评分
 *
 * @author hanguilin
 */
public class UserRateVo {

    /**
     * 用户名
     */
    private String user;

    /**
     * 商品名
     */
    private String product;

    /**
     * 评分
     */
    private Double score;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
