package com.example.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 地址实体类 - 演示嵌套对象的校验
 *
 * 嵌套校验是指当校验一个对象时，如果该对象包含其他对象（嵌套对象），
 * 也需要对这些嵌套对象进行校验。
 *
 * 如何启用嵌套校验？
 * 1. 在嵌套对象上添加 @Valid 注解
 * 2. Spring 会自动递归地校验嵌套对象的所有校验注解
 * 3. 嵌套校验的错误信息会包含完整的路径，如 address.city
 *
 * 嵌套校验的常见场景：
 * - 用户注册时校验用户信息和地址信息
 * - 订单创建时校验订单信息和商品列表
 * - 表单提交时校验主表和明细表数据
 */
public class Address {

    /**
     * 省份
     * 使用 @NotBlank 校验不能为空且不能是空白字符串
     */
    @NotBlank(message = "省份不能为空")
    private String province;

    /**
     * 城市
     * 使用 @NotBlank 校验不能为空
     */
    @NotBlank(message = "城市不能为空")
    private String city;

    /**
     * 区县
     */
    @NotBlank(message = "区县不能为空")
    private String district;

    /**
     * 详细地址
     * 使用 @Size 限制长度在 5-100 个字符之间
     */
    @NotBlank(message = "详细地址不能为空")
    @Size(min = 5, max = 100, message = "详细地址长度必须在5-100个字符之间")
    private String detailAddress;

    /**
     * 邮编
     * 使用正则表达式校验中国邮编格式（6位数字）
     */
    @Pattern(regexp = "^\\d{6}$", message = "邮编必须是6位数字")
    private String zipCode;

    /**
     * 国家
     * 设置默认值，假设大部分用户都是中国
     */
    @Size(max = 50, message = "国家名称不能超过50个字符")
    private String country = "中国";

    // ==================== Getter 和 Setter 方法 ====================

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "Address{" +
                "province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", detailAddress='" + detailAddress + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
