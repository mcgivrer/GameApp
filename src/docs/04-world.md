# World

---

## Introduction

The `World` class is a part of the `Demo01Frame` framework, designed to define the physical and environmental parameters
within which the game or simulation entities operate. This class encapsulates the concept of the game world, including
the play area dimensions and global physical properties such as gravity.

## Purpose

The primary purpose of the `World` class is to provide a structured environment for the game entities. It defines the
boundaries of the play area and applies global physical constraints that affect all entities within this environment.

## Attributes

1. **playArea**:
    - Type: `Rectangle2D`
    - Description: Represents the dimensions of the playable area within the game. The default value is set to a
      rectangle starting at (0, 0) with a width of 640 and a height of 480.
    - Usage: This attribute defines the spatial limits where the entities can move and interact.

2. **gravity**:
    - Type: `double`
    - Description: Represents the gravitational force applied to all entities within the game world. The default value
      is set to 0.981.
    - Usage: This attribute simulates the effect of gravity, influencing the vertical motion of entities.

## Methods

The `World` class currently does not define any methods. It primarily serves as a container for the play area and
gravity attributes, which can be accessed and modified directly.

## Usage

To use the `World` class, instantiate it within the context of the `Demo01Frame` framework. The play area and gravity
can be adjusted according to the specific needs of the game or simulation. For example:

```java
Demo01Frame.World world = new Demo01Frame.World();

world.playArea =new Rectangle2D.Double(0.0,0.0,800.0,600.0);

world.gravity =1.62; // Setting gravity to simulate moon-like conditions
```

This configuration will create a game world with an 800x600 play area and a reduced gravity similar to that of the moon.

## Conclusion

The `World` class provides a simple yet essential framework for defining the game environment. By specifying the play
area and gravity, it establishes the foundational physical constraints within which all game entities operate. This
documentation aims to help developers understand and effectively utilize the `World` class to create diverse and dynamic
game environments.
