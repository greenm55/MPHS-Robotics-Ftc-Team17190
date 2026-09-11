## TeamCode Module

Welcome!

This module, TeamCode, contains the code used to control the team's FTC robot. It currently contains four primary OpModes, each designed for a different type of robot control:

- **BasicMecanum** — Manual robot-centric mecanum-drive TeleOp
- **AdvancedMecanum** — Manual field-centric mecanum-drive TeleOp using odometry
- **BasicTank** — Manual tank-drive TeleOp
- **BasicMecanumWOdom** — Autonomous mecanum-drive OpMode using odometry and target points

The module also contains a reusable **Odometry** class used to track the robot's position and heading.

All four drive programs use the same four drive motors:

- `front_left_drive`
- `front_right_drive`
- `back_left_drive`
- `back_right_drive`

# Driver controlled programs

## BasicMecanum

`BasicMecanum` is a manual robot-centric mecanum-drive TeleOp. The driver can control forward and backward movement, strafing, and rotation using the gamepad.

### OpMode Information

```text
Class: BasicMecanum
Type: TeleOp
Drive: Mecanum
Control: Robot-centric
```

### Controls

| Controller | Input         | Function            |
|------------|---------------|---------------------|
| Gamepad 1  | Left Stick Y  | Forward / backward  |
| Gamepad 1  | Left Stick X  | Strafe left / right |
| Gamepad 1  | Right Stick X | Rotate left / right |

### Wheel Power Calculation

The program calculates the power for each wheel using the driver's forward, strafe, and rotation inputs.

```text
wheelPower[0] = leftXY[0] - leftXY[1] - rightXY[1]; // Front Right
wheelPower[1] = leftXY[0] + leftXY[1] + rightXY[1]; // Front Left
wheelPower[2] = leftXY[0] + leftXY[1] - rightXY[1]; // Back Right
wheelPower[3] = leftXY[0] - leftXY[1] + rightXY[1]; // Back Left
```

The wheel powers are normalized using the largest absolute wheel power. This keeps all motor powers within the `-1.0` to `1.0` range while preserving the intended movement direction.

# Advanced driver controlled programs

## AdvancedMecanum

`AdvancedMecanum` is a manual field-centric mecanum-drive TeleOp. It uses the robot's heading from the `Odometry` class to convert the driver's controls into field-relative movement.

### OpMode Information

```text
Class: AdvancedMecanum
Type: TeleOp
Drive: Mecanum
Control: Field-centric
Localization: Odometry
```

### Controls

| Controller | Input         | Function                          |
|------------|---------------|-----------------------------------|
| Gamepad 1  | Left Stick Y  | Field-relative forward / backward |
| Gamepad 1  | Left Stick X  | Field-relative strafe             |
| Gamepad 1  | Right Stick X | Rotate left / right               |

### Odometry Update

The robot's position is updated using three encoder inputs:

```text
Odometry.evalPos(
    frontRightDrive.getCurrentPosition(),
    frontLeftDrive.getCurrentPosition(),
    backRightDrive.getCurrentPosition()
);
```

The robot's current heading is then retrieved from the odometry system:

```text
heading = Odometry.getHeading();
```

### Field-Centric Calculation

The driver's movement input is rotated using the robot's current heading before being passed into the mecanum wheel-power equations.

```java
float fieldX = (float) (
    leftXY[1] * Math.cos(heading)
    - leftXY[0] * Math.sin(heading)
);

float fieldY = (float) (
    leftXY[1] * Math.sin(heading)
    + leftXY[0] * Math.cos(heading)
);
```

This allows the driver to control the robot relative to the field instead of relative to the robot.

# Basic tank drive

## BasicTank

`BasicTank` is a manual tank-drive TeleOp. The left and right sides of the robot are controlled independently.

### OpMode Information

```text
Class: BasicTank
Type: TeleOp
Drive: Tank
Control: Robot-centric
```

### Controls

| Controller | Input         | Function           |
|------------|---------------|--------------------|
| Gamepad 1  | Left Stick Y  | Control left side  |
| Gamepad 1  | Right Stick Y | Control right side |

The joystick values are inverted so that pushing the sticks forward produces positive motor power.

# Autonomous programs

## BasicMecanumWOdom

`BasicMecanumWOdom` is an autonomous mecanum-drive OpMode that uses odometry to track the robot's position on the field.

The program can drive toward target points and use configurable tolerances to determine when the robot has reached its destination.

### OpMode Information

```text
Class: BasicMecanumWOdom
Type: Autonomous
Drive: Mecanum
Localization: Odometry
Navigation: Target points
```

### Target Points

Target points are defined using field X and Y coordinates.

The robot compares its current position with the target position and uses position tolerances to determine when the target has been reached.

Multiple target points can be chained together to create an autonomous path.

### Robot Position

The odometry system stores the robot's position in the following format:

```text
robotPosition = {X, Y, Heading}
```

| Index | Value      | Unit    |
|-------|------------|---------|
| `0`   | X position | mm      |
| `1`   | Y position | mm      |
| `2`   | Heading    | radians |

The values can be accessed using:

```text
Odometry.getX();
Odometry.getY();
Odometry.getHeading();
```

# Odometry

## Odometry

`Odometry` is a reusable helper class responsible for calculating the robot's field position and heading from three encoder inputs.

The class is separate from the OpModes so that the same localization system can be used by multiple programs.

### OpMode Information

```text
Class: Odometry
Type: Helper class
Inputs: Three encoder positions
Outputs: X position, Y position, heading
```

### Odometry Constants

The current odometry constants are:

| Constant              | Value   | Unit             |
|-----------------------|---------|------------------|
| `wheelDiameter`       | `48.0`  | mm               |
| `tickPerRev`          | `537.7` | ticks/revolution |
| `trackWidth`          | `350.0` | mm               |
| `PerpendicularOffset` | `120.0` | mm               |

These values can be adjusted to match the physical measurements of the robot.

### Position Variables

The odometry system maintains the robot's current field position and heading.

```java
public static double robotT = 0;
public static double fieldX = 0;
public static double fieldY = 0;

public static double[] robotPosition = {0, 0, 0};
```

### Encoder Inputs

The main odometry calculation is performed using three encoder positions:

```text
Odometry.evalPos(
    frontRightEncoder,
    frontLeftEncoder,
    backRightEncoder
);
```

The encoder changes between updates are converted into distances using the wheel diameter and encoder ticks per revolution.

### Movement Calculation

The difference between the left and right encoder movement is used to calculate the robot's change in heading.

The average left and right encoder movement is used to calculate forward movement.

The perpendicular encoder is used to calculate sideways movement.

```text
deltaT = (deltaRight - deltaLeft) / trackWidth;
deltaY = (deltaRight + deltaLeft) / 2;
deltaX = deltaPerpendicular - PerpendicularOffset * deltaT;
```

### Field Movement

The robot-relative movement is converted into field-relative movement using the average heading during the movement.

```text
fieldDeltaX =
    deltaX * Math.cos(averageT)
    - deltaY * Math.sin(averageT);

fieldDeltaY =
    deltaX * Math.sin(averageT)
    + deltaY * Math.cos(averageT);
```

The calculated field movement is then added to the robot's current field position.

### Position Access

The current X position can be retrieved with:

```text
Odometry.getX();
```

The current Y position can be retrieved with:

```text
Odometry.getY();
```

The current heading can be retrieved with:
```text
Odometry.getHeading();
```

The heading is returned in radians.

# Drive Motor Configuration

All drive OpModes use the following hardware names:

| Motor       | Hardware Name       | Direction |
|-------------|---------------------|-----------|
| Front Left  | `front_left_drive`  | Reverse   |
| Front Right | `front_right_drive` | Forward   |
| Back Left   | `back_left_drive`   | Reverse   |
| Back Right  | `back_right_drive`  | Forward   |

These names must match the motor configuration in the FTC Robot Controller.

# Program Structure

The current TeamCode module separates manual driving, autonomous navigation, and localization into individual classes.

```text
TeamCode
│
├── BasicMecanum.java
│   └── Robot-centric mecanum TeleOp
│
├── AdvancedMecanum.java
│   └── Field-centric mecanum TeleOp
│
├── BasicTank.java
│   └── Tank-drive TeleOp
│
├── BasicMecanumWOdom.java
│   └── Autonomous mecanum navigation
│
└── Odometry.java
    └── Robot position and heading tracking
```