# Mall-Backend-Spring-Boot-Demo

### 项目地址

后端地址：[秒杀商城后端](https://mserver.cngo.xyz/)

前端地址：[在线商城 - 首页](https://m.cngo.xyz/)

> tips：项目部署可能休眠，需要一定时间启动

### 问题记录

- 商品没有库存记录的问题，查询会失败
- 商品删除后已购买的订单的外键问题（已解决，外键删除策略设置为Set Null）
- paramcheck无法校验这种的`list<integer>`
- 支付失败仍然会扣库存和记录销量（已解决，BusinessException extends RuntimeException）
- 更新库存时候不会更新时间