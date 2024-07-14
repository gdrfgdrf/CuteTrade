CuteTrade
===
[English](https://github.com/gdrfgdrf/CuteTrade/blob/1.20.1/README.md) | __[简体中文](https://github.com/gdrfgdrf/CuteTrade/blob/1.20.1/README_ChineseSimplified.md)__  
一个 Fabric 的交易模组

命令
------------------------
### 公共指令
根指令为 /trade-public

| 内容                          | 描述          | 拥有执行权限的范围 |
|-----------------------------|-------------|-----------|
| /trade-public               | 根指令         | 全体玩家      |
| /trade-public request <玩家名> | 向某个玩家发起交易请求 | 全体玩家      |
| /trade-public accept <玩家名>  | 接受某个玩家的交易请求 | 全体玩家      |
| /trade-public decline <玩家名> | 拒绝某个玩家的交易请求 | 全体玩家      |
| /trade-public end-trade     | 结束当前交易      | 全体玩家      |
| /trade-public history       | 显示交易历史记录    | 全体玩家      |
| /trade-public help          | 打印帮助        | 全体玩家      |
| /trade-public tutorial      | 打印教程        | 全体玩家      |

### 管理员指令
根指令为 /trade-admin

| 内容                         | 描述            | 拥有执行权限的范围 |
|----------------------------|---------------|-----------|
| /trade-admin               | 根指令           | 仅管理员      |
| /trade-admin history <玩家名> | 查看某个玩家的交易历史记录 | 仅管理员      |
| /trade-admin help          | 打印管理员帮助       | 仅管理员      |


依赖
------------------------
运行时您需要将 [Fabric Language Kotlin](https://github.com/FabricMC/fabric-language-kotlin) 作为 mod 添加

以下为项目所使用的依赖

| 依赖                                                              | 用途           |
|-----------------------------------------------------------------|--------------|
| [Protocol Buffers](https://github.com/protocolbuffers/protobuf) | 对玩家和交易数据进行存储 |
| [Snappy](https://github.com/google/snappy)                      | 对玩家和交易数据进行压缩 |


协议
------------------------
该项目使用 Apache-2.0 License 
