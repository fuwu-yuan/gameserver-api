# gameserver-api
My experimental way of "generating" game server for small web games.

Available routes:

### `POST` Create game room
```
/gameserver-api/v1/room
```
This route create a game room which means:
  - determine server id, ip & port
  - launch a game server
  - save those informations to database

#### Parameter

This route expects a json input:

```
{
  "name": "Join my Pong game!",
  "description": "Chill game, everyone is welcome",
  "game": "Pong",
  "game_version": "1.0",
  "n_max_players": 8
}
```

Properties `name`, `game`, `game_version` and `n_max_players` are mandatory.

The property `description` is optional. It can be null, empty or even not present in the json input.

#### Results

###### 201 Created
```
{
  "statusCode": 201,
  "statusReason": "Created",
  "data": {
    "server_id": "4",
    "name": "Join my Pong game!",
    "description": "Chill game, everyone is welcome",
    "game": "Pong",
    "game_version": "1.0",
    "ip": "123.12.3.123",
    "port": 50000,
    "n_max_players": 8
  }
}
```

### `GET` Get all servers
```
/gameserver-api/v1/room
```
This route create returns a list of all game running servers.

#### Parameter

This route expects no parameter.

#### Results

###### 200 OK
```
{
  "statusCode": 201,
  "statusReason": "Created",
  "data": [
    {
      "server_id": "4",
      "name": "Join my Pong game!",
      "game": "Pong",
      "game_version": "1.0",
      "ip": "123.12.3.123",
      "port": 50000,
      "n_max_players": 8
    },
    {
      "server_id": "5",
      "name": "Another Pong game!",
      "game": "Pong",
      "game_version": "1.0",
      "ip": "123.12.3.123",
      "port": 50001,
      "n_max_players": 4
    },
    {
      "server_id": "9",
      "name": "Only server for my noob game :(",
      "game": "My small game",
      "game_version": "0.6",
      "ip": "123.12.3.123",
      "port": 50004,
      "n_max_players": 3
    }
  ]
}
```

### `GET` Get game server by id
```
/gameserver-api/v1/room/$id
```
This route returns the game server corresponding to `$server-id`.

#### Parameter

Expects an url parameter: `$server-id`.

#### Results

###### 200 OK
```
{
  "statusCode": 200,
  "statusReason": "OK",
  "data": {
    "server_id": "4",
    "name": "Join my Pong game!",
    "description": "Chill game, everyone is welcome",
    "game": "Pong",
    "game_version": "1.0",
    "ip": "123.12.3.123",
    "port": 50000,
    "n_max_players": 8,
    "opened_on": "yyyy-mm-dd HH:MM:SS",
    "ready_for_shutdown": "0"
  }
}
```

### `GET` Get game server by game name & game version
```
/gameserver-api/v1/room/$game-name/$game-version
```
This route returns a list of game servers filtered by the game name & version.
#### Parameter

Expects two url parameters: `$game-name` & `$game-version`.

#### Results

###### 200 OK
```
{
  "statusCode": 200,
  "statusReason": "OK",
  "data": [
    {
      "server_id": "4",
      "name": "Join my Pong game!",
      "game": "Pong",
      "game_version": "1.0",
      "ip": "123.12.3.123",
      "port": 50000,
      "n_max_players": 8
    },
    {
      "server_id": "5",
      "name": "Another Pong game!",
      "game": "Pong",
      "game_version": "1.0",
      "ip": "123.12.3.123",
      "port": 50001,
      "n_max_players": 4
    }
  ]
}
```

### `DELETE` Shutdown a game server
```
/gameserver-api/v1/room/$server-id
```
This route stops a game room which means:
  - shutdown the game server
  - free the port used by the game server
  - delete its informations from the database

#### Parameter

Expects an url parameter: `$server-id`

#### Results

###### 200 OK
```
{
  "statusCode": 200,
  "statusReason": "OK"
}
```
