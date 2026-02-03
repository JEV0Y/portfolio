# SocialConnect

SocialConnect is a Java-based social networking simulation project. It models core social media features including user profiles, posts (text, image, and reshared), and interactions.

## Features

- **User Management**: Create and manage user profiles.
- **Post Types**: Support for regular text posts, image posts (with captions and dimensions), and reshared posts.
- **Feed System**: Users can view posts from friends.
- **Privacy Controls**: Posts can be public or friends-only.

## Project Structure

The project source code is organized in the `src` directory under the `project` package.

- `src/project/SocialConnect.java`: Main entry point/controller class.
- `src/project/User.java`: Represents a user in the system.
- `src/project/Post.java`: Abstract base class for different post types.
- `src/project/enums/`: Enumerations for audience and sorting.
- `src/project/gui/`: Swing components (if any).
- `src/project/interfaces/`: Interfaces like `Displayable`.

## How to Compile and Run

Ensure you have Java installed (JDK 8 or higher recommended).

### Compilation

Compile the project from the `src` directory:

```bash
cd src
javac project/SocialConnect.java
```

### Running

Run the application from the `src` directory:

```bash
cd src
java project.SocialConnect
```
