**2023/1/29**

## java.sql.SQLFeatureNotSupportedException: null



**报错原因**　java中实体类用到了LocalDateTime类型，用德鲁伊数据源的时候，版本太低导致在转换时报错（原来用的是1.1.16）

**解决方案（任选其一）**

1. 将实体类里面的LocalDateTime改为Date（java.sql）类型
2. 或者将德鲁伊数据源的版本升级为1.1.22之后



**2023/1/31**

![image-20230131115759940](C:\Users\86158\AppData\Roaming\Typora\typora-user-images\image-20230131115759940.png)**

## 分析原因：添加了拦截器导致默认的register被覆盖，默认的静态资源位置也被覆盖

## 解决办法：

![image-20230131120124661](C:\Users\86158\AppData\Roaming\Typora\typora-user-images\image-20230131120124661.png)

或者

## ![image-20230131120212933](C:\Users\86158\AppData\Roaming\Typora\typora-user-images\image-20230131120212933.png) 	