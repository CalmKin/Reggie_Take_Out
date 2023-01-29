**2023/1/29**

## java.sql.SQLFeatureNotSupportedException: null



**报错原因**　java中实体类用到了LocalDateTime类型，用德鲁伊数据源的时候，版本太低导致在转换时报错（原来用的是1.1.16）

**解决方案（任选其一）**

1. 将实体类里面的LocalDateTime改为Date（java.sql）类型
2. 或者将德鲁伊数据源的版本升级为1.1.22之后