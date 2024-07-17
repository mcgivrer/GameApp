# Entity

---

## Introduction

The `Entity` class is a crucial part of the `Demo01Frame` framework, representing any object that appears within the
game or simulation environment. This class encapsulates the properties and behaviors of these objects, allowing for
dynamic interaction and movement within the game world.

## Purpose

The primary purpose of the `Entity` class is to provide a blueprint for creating and managing game objects. It defines
attributes such as position, size, velocity, and visual properties, and includes methods to manipulate these attributes,
facilitating the development of interactive and animated entities.

## Attributes

1. **id**:
    - Type: `int`
    - Description: A unique identifier for each entity, incremented automatically.
    - Usage: Used to differentiate between multiple entities.

2. **name**:
    - Type: `String`
    - Description: A descriptive name for the entity.
    - Usage: Can be used for logging, debugging, or identifying entities.

3. **dx, dy**:
    - Type: `double`
    - Description: The velocity components of the entity in the x and y directions.
    - Usage: Determines the speed and direction of the entity's movement.

4. **priority**:
    - Type: `int`
    - Description: A value indicating the rendering priority of the entity.
    - Usage: Entities with higher priority are rendered on top of those with lower priority.

5. **active**:
    - Type: `boolean`
    - Description: Indicates whether the entity is active and should be processed by the game loop.
    - Usage: Inactive entities are ignored during updates and rendering.

6. **borderColor, fillColor**:
    - Type: `Color`
    - Description: The colors used to draw the entity's border and fill.
    - Usage: Defines the visual appearance of the entity.

7. **ax, ay**:
    - Type: `double`
    - Description: The acceleration components of the entity in the x and y directions.
    - Usage: Determines how the velocity of the entity changes over time.

8. **forces**:
    - Type: `List<Point2D>`
    - Description: A list of forces applied to the entity.
    - Usage: Used to calculate the resultant force affecting the entity's movement.

9. **material**:
    - Type: `Material`
    - Description: Represents the physical properties of the entity's material.
    - Usage: Affects the entity's interaction with other entities and the environment.

10. **mass**:
    - Type: `double`
    - Description: The mass of the entity.
    - Usage: Influences the entity's response to forces and acceleration.

11. **stickToCamera**:
    - Type: `boolean`
    - Description: Indicates whether the entity should move with the camera.
    - Usage: Useful for HUD elements or static overlays.

12. **behaviors**:
    - Type: `List<Behavior>`
    - Description: A list of behaviors defining the entity's actions.
    - Usage: Allows for the modular addition of actions and reactions to events.

13. **attributes**:
    - Type: `Map<String, Object>`
    - Description: A collection of custom attributes for the entity.
    - Usage: Provides flexibility to add additional properties as needed.

## Methods

1. **Constructor**:
    - `public Entity(String name)`
    - Initializes a new entity with the given name.

2. **setPosition(double x, double y)**:
    - Sets the position of the entity.

3. **setSize(double w, double h)**:
    - Sets the size of the entity.

4. **setVelocity(double dx, double dy)**:
    - Sets the velocity of the entity.

5. **setAcceleration(double ax, double ay)**:
    - Sets the acceleration of the entity.

6. **setPriority(int p)**:
    - Sets the rendering priority of the entity.

7. **setStickToCamera(boolean s)**:
    - Sets whether the entity should stick to the camera.

## Usage

To use the `Entity` class, create an instance and configure its attributes using the provided methods. For example:

```java
Entity player = new Demo01Frame.Entity("Player");
player.setPosition(100,150)
      .setSize(50,50)
      .setVelocity(1,0)
      .setAcceleration(0,0.1)
      .setPriority(1)
      .setStickToCamera(false);
```

This configuration creates a player entity at position (100, 150) with a size of 50x50, an initial velocity of 1 in the
x-direction, and an acceleration of 0.1 in the y-direction.

#### Conclusion

The `Entity` class provides a comprehensive framework for creating and managing game objects within the `Demo01Frame`
environment. By defining attributes and behaviors, it allows developers to build dynamic and interactive entities that
can move, interact, and respond to the game world. This documentation aims to help developers understand and utilize
the `Entity` class effectively to create rich and engaging game experiences.
