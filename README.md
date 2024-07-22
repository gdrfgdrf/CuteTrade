CuteTrade
===
__[English](https://github.com/gdrfgdrf/CuteTrade/blob/1.20.1/README.md)__ | [简体中文](https://github.com/gdrfgdrf/CuteTrade/blob/1.20.1/README_ChineseSimplified.md)  
A trading mod for Fabric

Commands
------------------------
## Public
Root: /trade-public

| Content                          | Description                          | Permission  |
|----------------------------------|--------------------------------------|-------------|
| /trade-public                    | Root command                         | All players |
| /trade-public request \<Player\> | Send a trade request to a player     | All players |
| /trade-public accept \<Player\>  | Accept a trade request from a player | All players |
| /trade-public decline \<Player\> | Decline a player's trade request     | All players |
| /trade-public end-trade          | End current transaction              | All players |
| /trade-public history            | View the transaction history         | All players |
| /trade-public help               | Print help                           | All players |
| /trade-public tutorial           | Print tutorial                       | All players |

## Admin
Root: /trade-admin


| Content                         | Description                         | Permission         |
|---------------------------------|-------------------------------------|--------------------|
| /trade-admin                    | Root command                        | Administrator only |
| /trade-admin history \<Player\> | View a player's transaction history | Administrator only |
| /trade-admin help               | Print administrator help            | Administrator only |

Dependencies
------------------------
[Fabric Language Kotlin](https://github.com/FabricMC/fabric-language-kotlin)  
[Cute Translation API](https://github.com/gdrfgdrf/CuteTranslationAPI)

| Dependency                                                      | Use                                      |
|-----------------------------------------------------------------|------------------------------------------|
| [Protocol Buffers](https://github.com/protocolbuffers/protobuf) | Store player and transaction data        |
| [Snappy](https://github.com/google/snappy)                      | Compress for player and transaction data |

License
------------------------
We use the Apache-2.0 License