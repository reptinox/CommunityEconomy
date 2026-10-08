# CommunityEconomy

A lightweight, community-driven economy plugin for Minecraft Paper servers, built with Java.

CommunityEconomy introduces **Talents**, a custom in-game currency earned by completing community projects. Instead of relying on traditional money-making systems, players work together to improve their world, complete shared goals, and earn rewards.

The plugin is designed for small, cooperative survival servers where community participation drives the economy.

## Features

### Custom Currency — Talents
- Unique in-game currency with a custom item model and texture.
- Uses Minecraft's Persistent Data Container (PDC) to identify authentic Talents.
- Supports player-to-player trading through physical items.
- Allows server operators to distribute Talents using commands.
- Automatically drops excess Talents at a player's feet when their inventory is full.

### Community Projects
- Server operators can create projects with custom names, rewards, and display items.
- Players can browse available projects through an inventory-based GUI.
- Projects can be marked as completed and assigned world coordinates.
- Talents are awarded when a completed project's location is recorded.
- Rewards can only be claimed once per project.
- Completed projects remain accessible for players to reference.

### Interactive GUI
- Custom inventory menus for navigating projects.
- Separate **Tasks** and **Completed** sections.
- Project details, reward information, and completion confirmation.
- Pagination for servers with numerous projects.
- Recorded project locations, including world and XYZ coordinates.

### Persistent Data
- Project information is stored in `projects.yml`.
- Project completion, reward status, and locations persist across server restarts.
- Project data is automatically loaded when the plugin starts and saved when changes occur.

## Commands

| Command | Description | Access      |
|---|---|-------------|
| `/projects` | Open the community projects GUI | All players |
| `/project add <id> <reward> <material> <name>` | Create a community project | Operator    |
| `/project remove <id>` | Remove an existing project | Operator    |
| `/project location <id>` | Record a completed project's location and award its Talents | All Players |
| `/talents <amount> <player>` | Give Talents to a player | Operator    |

Command tab completion is supported for relevant subcommands, project IDs, materials, and player names.

## How It Works

1. **Create a project.** An operator adds a project with a name, display item, and Talent reward.
2. **Work together.** Players view available projects using `/projects` and collaborate to complete them.
3. **Mark completion.** A completed project is confirmed through the inventory GUI.
4. **Record its location.** A player uses `/project location <id>` while standing at the finished project.
5. **Receive Talents.** The project reward is issued once, and the completed project remains available for reference.

### Example

Create a community iron farm project:

```text
/project add iron_farm 32 IRON_INGOT Iron Farm
```

Once the iron farm has been built and marked complete, stand at the location and enter:

```text
/project location iron_farm
```

The project location is recorded, and 32 Talents are awarded.

## Installation

### Requirements
- A compatible Minecraft Java Edition server running Paper
- Java version required by the target Paper server
- The CommunityEconomy plugin JAR
- The accompanying resource pack for custom Talent visuals

### Server Setup

1. Download or build the CommunityEconomy JAR.
2. Place the JAR in your server's `plugins/` directory.
3. Restart the server.
4. Verify that CommunityEconomy appears in the server's plugin list.
5. Install and enable the resource pack to display the custom Talent texture.
6. Use `/project add` to create your first community project.

The plugin automatically creates its data directory and project configuration file as needed.

## Resource Pack

CommunityEconomy uses a custom item model to give Talents their own appearance.

The resource pack contains the item model definitions and texture required to render the custom currency.

The underlying item is a Minecraft gold nugget, but Talents are distinguished using a persistent data tag rather than their display name alone.

**Note:** Players should enable the resource pack to see the intended Talent appearance. Because Talents retain gold nugget behavior, vanilla crafting interactions may still apply.

## Building from Source

The project uses **Java**, the **Paper API**, and **Gradle**.

Clone the repository:

```bash
git clone https://github.com/reptinox/CommunityEconomyPlugin.git
cd CommunityEconomyPlugin
```

Build the plugin:

```bash
./gradlew build
```

On Windows:

```powershell
.\gradlew.bat build
```

The compiled plugin JAR will be available in:

```text
build/libs/
```

## Project Structure

```text
src/main/java/com/conner/communityeconomy/
├── CommunityEconomy.java
├── Commands.java
├── Listeners.java
├── Project.java
├── ProjectManager.java
├── ProjectMenu.java
└── TalentManager.java
```

| Class | Responsibility |
|---|---|
| `CommunityEconomy` | Plugin initialization, command registration, and lifecycle |
| `Commands` | Command execution, validation, and tab completion |
| `Listeners` | Inventory interactions and event handling |
| `Project` | Project data model |
| `ProjectManager` | Project management and YAML persistence |
| `ProjectMenu` | Inventory GUI creation and navigation |
| `TalentManager` | Talent creation, identification, and distribution |

## Technologies

- **Java** — Core plugin implementation
- **Paper API** — Minecraft server integration
- **Gradle** — Build automation and dependency management
- **Adventure API** — Text components and item display formatting
- **YAML** — Persistent project storage
- **Persistent Data Container** — Custom item identification

## Design Goals

CommunityEconomy was developed with several goals in mind:

- **Lightweight:** Avoid unnecessary dependencies and complex economy infrastructure.
- **Community-focused:** Reward collaborative contributions instead of repetitive grinding.
- **Simple to use:** Provide intuitive commands and inventory-based menus.
- **Persistent:** Preserve project progress and rewards between server sessions.
- **Maintainable:** Separate responsibilities into focused Java classes.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

