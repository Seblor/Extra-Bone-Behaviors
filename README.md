<p align="center">
  <img src="logo.svg" alt="ExtraBoneBehaviors" width="128"/>
</p>

<h1 align="center">ExtraBoneBehaviors</h1>

<p align="center">
  A <a href="https://mythiccraft.io/index.php?resources/model-engine.389/">ModelEngine 4.x</a> plugin that adds custom bone behaviors driven by real-world data.<br/>
  Built for my own Minecraft server — free to use by anyone.
</p>

---

## Features

### Clock Hands

Bones named with any of the prefixes below are automatically rotated to reflect the **server's local time**. Rotation is applied on the Z axis (clockwise when viewed from the front).

| Bone prefix | Behavior |
|---|---|
| `hour_` | Snaps once per hour |
| `hour_s_` | Smooth — advances continuously through each minute |
| `hour_ss_` | Super-smooth — millisecond precision |
| `minute_` | Snaps once per minute |
| `minute_s_` | Smooth — advances continuously through each second |
| `minute_ss_` | Super-smooth — millisecond precision |
| `second_` | Snaps once per second |
| `second_s_` | Smooth — millisecond precision |
| `second_ss_` | Same as `second_s_` (Minecraft's tick is the finest unit) |

Any text after the prefix is ignored, so `hour_hand`, `hour_left`, `hour_1` are all valid.

## Requirements

| Dependency | Version |
|---|---|
| Java | 17+ |
| Spigot / Paper | 1.21.x |
| [ModelEngine](https://mythiccraft.io/index.php?resources/model-engine.389/) | R4.0.9+ |

## Installation

1. Drop `ExtraBoneBehaviors.jar` into your server's `plugins/` folder.
2. Ensure **ModelEngine** is also installed.
3. Restart. No configuration is required.

The plugin registers itself during ModelEngine's load phase — `/meg reload` is fully supported and will seamlessly refresh all active clock models.

## Usage in Blockbench

Name your bones using one of the prefixes in the table above. ModelEngine picks up the behavior automatically when the model is loaded or reloaded.

```
my_clock_model
├── hour_hand          ← snaps per hour
├── minute_s_hand      ← smooth minute hand (snaps every second)
└── second_ss_hand     ← millisecond-precise second hand (snaps every tick)
```

## Building from Source

```bash
git clone https://github.com/Seblor/ExtraBoneBehaviors.git
cd ExtraBoneBehaviors
mvn package
```

The compiled jar is produced at `target/ExtraBoneBehaviors.jar`.

## License

[MIT](LICENSE)
