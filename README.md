# AxSync
> Minecraft Plugin to sync player data between multiple backends
---
## Features
- Sync
  - Health
  - FoodLevel
  - GameMode
  - Saturation
  - Experience
  - AirLevel
  - Inventory
  - EnderChest
---
## Installation
1. Download the latest release
2. Put the `.jar` into the `/plugins` folder of every server to be synchronized
3. Start the server
4. Stop the server
5. Edit the `config.yml`
6. Start the server
---
## Configuration
```
# config.yml
database:
  host: localhost
  port: 3306
  database: axsync
  username: root
  password: