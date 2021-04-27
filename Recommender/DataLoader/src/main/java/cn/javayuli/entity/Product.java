package cn.javayuli.entity;

/**
 * 产品实体
 *
 * @author hanguilin
 */
public class Product {

    /**
     * product集合名称
     */
    public static final String COLLECTION_PRODUCT = "Products";

    /**
     * 商品id
     */
    private Integer productId;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品分类
     */
    private String categories;

    /**
     * 商品图片url
     */
    private String imageUrl;

    /**
     * 商品UGC标签
     */
    private String tags;

    public Product(Integer productId, String name, String categories, String imageUrl, String tags) {
        this.productId = productId;
        this.name = name;
        this.categories = categories;
        this.imageUrl = imageUrl;
        this.tags = tags;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
