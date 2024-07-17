# Material for Physic purpose

---

## Introduction

The `Material` subclass within the `Demo01Frame` framework encapsulates the properties that define the physical
characteristics of game entities. This subclass provides a detailed modeling of material attributes like density,
elasticity, and roughness, which influence how entities interact within the game environment.

## Purpose

The purpose of the `Material` subclass is to allow developers to define the behavioral characteristics of entities based
on their material properties. This aids in simulating realistic physics-based interactions, such as collisions and
movements, influenced by material characteristics.

## Attributes

1. **name**:
    - Type: `String`
    - Description: The name of the material.
    - Usage: Helps in identifying and referencing the material.

2. **density**:
    - Type: `double`
    - Description: Defines the mass per unit volume of the material.
    - Usage: Affects how gravity and other forces impact the entity.

3. **elasticity**:
    - Type: `double`
    - Description: Measures the material's ability to return to its original shape after deformation (bounciness).
    - Usage: Influences how the entity behaves upon impact or collision.

4. **roughness**:
    - Type: `double`
    - Description: Indicates the texture or frictional characteristics of the material surface.
    - Usage: Affects sliding and rolling interactions with other surfaces.

## Constructor

- **Material(String name, double density, double elasticity, double roughness)**:
    - Initializes a new instance of `Material` with the specified properties. This allows for the creation of custom
      materials with unique physical properties.

## Static Materials

- **DEFAULT**:
    - `public static final Material DEFAULT = new Material("default", 1.0, 1.0, 1.0);`
    - A predefined material with standard properties, used as a baseline for comparison or default behavior.

## Usage

To use the `Material` subclass, instantiate it with specific properties or utilize the predefined `DEFAULT` material.
For example:

```java
Entity rock = new Demo01Frame.Entity("Rock");
rock.material =new Demo01Frame.

Material("Granite",2.75,0.1,1.5);
```

This example creates a new material called "Granite" with specific properties for a rock entity, influencing how it
interacts with the game world based on its physical attributes.

The `Material` subclass is a critical component for developing physics-based functionalities within the `Demo01Frame`
environment. It allows developers to customize how entities behave under various physical conditions, providing a more
immersive and realistic gaming experience. This documentation is intended to assist developers in understanding and
utilizing the `Material` subclass effectively to enhance the dynamism and realism of their game entities.

Creating a set of materials for different objects such as glass, rock, wood, animals, and vegetables within
the `Material` subclass requires defining unique physical properties for each. Here are possible specifications for each
material type based on common physical characteristics:

## Material Definitions

### 1. **Glass**:

- **Name**: "Glass"
- **Density**: 2.6 (typical density of glass in g/cm³)
- **Elasticity**: 0.2 (glass is brittle and has low elasticity)
- **Roughness**: 0.05 (smooth surface, low friction)
   ```java
   Material glass = new Material("Glass", 2.6, 0.2, 0.05);
   ```

### 2. **Rock**:

- **Name**: "Rock"
- **Density**: 2.7 (average density of rock in g/cm³, varies widely)
- **Elasticity**: 0.1 (rocks are generally non-elastic)
- **Roughness**: 0.7 (rough texture, higher friction)
   ```java
   Material rock = new Material("Rock", 2.7, 0.1, 0.7);
   ```

### 3. **Wood**:

- **Name**: "Wood"
- **Density**: 0.6 (varies widely among types of wood)
- **Elasticity**: 0.3 (wood can be somewhat flexible)
- **Roughness**: 0.4 (variable texture, moderate friction)
   ```java
   Material wood = new Material("Wood", 0.6, 0.3, 0.4);
   ```

### 4. **Animal**:

- **Name**: "Animal"
- **Density**: 1.0 (approximation for the density of water, which is close to the density of many animal tissues)
- **Elasticity**: 0.5 (animals tend to have more elastic tissues)
- **Roughness**: 0.3 (depends on the skin or fur, generally moderate friction)
   ```java
   Material animal = new Material("Animal", 1.0, 0.5, 0.3);
   ```

### 5. **Vegetable**:

- **Name**: "Vegetable"
- **Density**: 0.95 (slightly less dense than water)
- **Elasticity**: 0.1 (vegetables are generally not very elastic unless fresh and hydrated)
- **Roughness**: 0.2 (typically smoother surfaces, lower friction)
   ```java
   Material vegetable = new Material("Vegetable", 0.95, 0.1, 0.2);
   ```

## Conclusion

Each of these material definitions is designed to reflect realistic physical properties based on common knowledge of
these objects. These properties can be adjusted based on specific requirements or more detailed scientific data to
better represent specific types of glass, rock, wood, animals, and vegetables in different scenarios within the game or
simulation environment. This approach ensures that interactions within the virtual world mimic real-world physics as
closely as possible.
