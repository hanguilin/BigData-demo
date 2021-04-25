package cn.javayuli.entity;

/**
 * 评分实体
 *
 * @author hanguilin
 */
public class Rating {

    /**
     * rating集合名称
     */
    public static final String COLLECTION_RATING = "Ratings";

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 商品id
     */
    private Integer productId;

    /**
     * 评分值
     */
    private Double score;

    /**
     * 评分时间
     */
    private Integer timestamp;

    public Rating(Integer userId, Integer productId, Double score, Integer timestamp) {
        this.userId = userId;
        this.productId = productId;
        this.score = score;
        this.timestamp = timestamp;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Integer getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Integer timestamp) {
        this.timestamp = timestamp;
    }
}
