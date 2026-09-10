## TeamCode Module

Welcome!

This module, TeamCode, contains the code used to control the team's FTC robot. It currently contains three primary OpModes, each designed for a different type of robot control:

- **BasicMecanum** — Manual mecanum-drive TeleOp
- **BasicTank** — Manual tank-drive TeleOp
- **BasicMecanumWOdom** — Autonomous mecanum-drive OpMode using three-wheel odometry and target points

All three programs use the same four drive motors:

- `front_left_drive`
- `front_right_drive`
- `back_left_drive`
- `back_right_drive`

The left-side motors are configured as `REVERSE`, while the right-side motors are configured as `FORWARD`.

# Driver controlled programs

## BasicMecanum

`BasicMecanum` is a TeleOp OpMode designed to manually control a four-wheel mecanum-drive robot.

### OpMode Information

```text
@TeleOp(name = "Basic Mecanum TeleOp", group = "Linear OpMode")
```

The OpMode appears on the Driver Station as:

**Basic Mecanum TeleOp**

### Controls

The program uses both gamepad sticks:

- **Left Stick Y** — Forward / backward movement
- **Left Stick X** — Left / right strafing
- **Right Stick X** — Rotation

The right stick's Y value is currently read but is not used for movement.

### Mecanum Drive

The program calculates individual power values for each wheel based on:

- Forward/backward movement
- Strafing movement
- Rotation

The wheel-power calculations are:

```text
Front Right = Y - X - Rotation
Front Left  = Y + X + Rotation
Back Right  = Y + X - Rotation
Back Left   = Y - X + Rotation
```

Because these calculations can produce values greater than `1` or less than `-1`, the program finds the largest absolute wheel-power value and divides every wheel's power by that value.

This keeps all motor powers within the valid `-1` to `1` range while preserving the intended movement ratio.

## BasicTank

`BasicTank` is a TeleOp OpMode designed to manually control the robot as a traditional tank-drive system.

### OpMode Information

```text
@TeleOp(name = "Basic Tank TeleOp", group = "Linear OpMode")
```

The OpMode appears on the Driver Station as:

**Basic Tank TeleOp**

### Controls

The program uses the two gamepad sticks independently:

- **Left Stick Y** — Controls the left side of the robot
- **Left Stick X** — Used in the left-side power calculation
- **Right Stick Y** — Controls the right side of the robot
- **Right Stick X** — Used in the right-side power calculation

### Wheel Assignment

The calculated powers are assigned as:

```text
Front Right = Right Stick Y - Right Stick X
Back Right  = Right Stick Y + Right Stick X

Front Left  = Left Stick Y + Left Stick X
Back Left   = Left Stick Y - Left Stick X
```

### Important Note

The current version of `BasicTank` contains a normalization issue.

The program calculates wheel powers into `finalWheelPower`, but the normalization section divides values from the separate `wheelPower` array:

```text
finalWheelPower = new double[]{
    (wheelPower[0] / max),
    (wheelPower[1] / max),
    (wheelPower[2] / max),
    (wheelPower[3] / max)
};
```

`wheelPower` is not updated with the newly calculated values inside the main loop.

Because of this, the current program should be considered an in-development tank-drive implementation.

# Autonomous Programs

## BasicMecanumWOdom

`BasicMecanumWOdom` is an autonomous OpMode designed to move a mecanum-drive robot through a series of predefined target points.

Unlike the TeleOp programs, this program does not use the gamepad for movement.

It uses encoder measurements to estimate the robot's position on the field and automatically drives toward the specified target points.

The program combines:

- Three-wheel odometry
- Field-centric position tracking
- Heading tracking
- Target-point navigation
- Position tolerance
- Heading tolerance
- Mecanum drive calculations
- Automatic power scaling

## Odometry

The autonomous program uses three encoder measurements to estimate the robot's position:

```text
Front Right Motor → Right encoder
Front Left Motor  → Left encoder
Back Right Motor  → Perpendicular encoder
```

The program treats the back-right encoder as the perpendicular odometry wheel.

The code assumes that dedicated dead-wheel encoders are connected to these encoder ports rather than relying on the mecanum wheels for accurate odometry.

### Odometry Constants

The primary odometry constants are:

```java
public static double wheelDiameter = 48.0;       // mm
public static double tickPerRev = 537.7;
public static final double trackWidth = 350.0;   // mm
public static final double PerpendicularOffset = 120.0; // mm
```

These values describe the physical geometry of the odometry system.

They should be measured and adjusted for the actual robot.

### Position

The robot's position is stored as:

```java
public static double[] robotPosition = {0,0,0};
```

The values represent:

```text
[X, Y, Heading]
```

`X` and `Y` represent the robot's position on the field, while `Heading` represents the robot's orientation in radians.

## Target Points

Autonomous movement is controlled using the `targetPoints` array.

Each target point contains five values:

```text
{ X, Y, Heading, XY Tolerance, Heading Tolerance }
```

For example:

```text
{100, 100, Math.PI, 50, 5}
```

represents:

```text
X Position        = 100
Y Position        = 100
Heading           = π radians
XY Tolerance      = 50
Heading Tolerance = 5
```

Multiple points can be placed in the array:

```java
public static final double[][] targetPoints = {
    {100, 100, Math.PI, 50, 5},
    {-100, 100, Math.PI/2, 10, 1},
    {0,0,0,1,1}
};
```

The robot attempts to reach each point in order.

Once the robot is within both the position and heading tolerances of the current point, the program advances to the next point.

When the final point is reached, the OpMode stops.

## Position Tolerance

The program checks the distance between the robot's current position and the target position.

The XY error is calculated as:

```text
XY Error = √(Error X² + Error Y²)
```

A target is considered reached when:

```text
XY Error ≤ XY Tolerance
```

and:

```text
|Heading Error| ≤ Heading Tolerance
```

Both conditions must be satisfied.

Target points with zero or negative tolerances are rejected before the OpMode starts.

## Heading Wrapping

The `angleWrap()` function keeps heading errors within the range:

```text
-π to π
```

This prevents the robot from taking an unnecessarily long rotational path when the target heading crosses the `-π / π` boundary.

## Targeting System

The `targeter()` function determines how the robot should move toward the current target.

First, the program calculates the target error in field coordinates.

The error is then transformed into the robot's coordinate system using the robot's current heading.

This allows the robot to determine how much it needs to move:

- Forward/backward
- Left/right
- Rotationally

The resulting movement commands are then converted into four mecanum-wheel powers.

## Automatic Power Scaling

The autonomous program changes its movement power based on how far the robot is from the target.

The percentage of distance remaining is calculated using:

```text
Percent Remaining = XY Error / XY Distance
```

The program then applies a piecewise power curve:

```text
0.00 – 0.25:
Power Scale = 3x + 0.25

0.25 – 0.75:
Power Scale = 1

0.75 – 1.00:
Power Scale = -3x + 3.25
```

This allows the robot to use lower power near the target while maintaining full power through the middle portion of the movement.

## Motor Power Normalization

After the targeter calculates the desired power for each wheel, the program finds the largest absolute power.

If the maximum power is greater than zero, every wheel power is divided by the maximum.

This keeps the wheel powers within the `-1` to `1` range while maintaining the ratio between the wheels.

## Encoder Initialization

Before autonomous operation begins, all four drive motors have their encoders reset:

```text
STOP_AND_RESET_ENCODER
```

They are then placed into:

```text
RUN_WITHOUT_ENCODER
```

This allows the program to manually control motor power while still reading encoder positions for odometry.

## Telemetry

`BasicMecanumWOdom` displays the robot's estimated position through telemetry:

```text
X:
Y:
Theta:
```

This allows the programmer to monitor the robot's calculated field position and heading during autonomous operation.

## Robot Configuration

The following motor names must exist in the robot configuration:

```text
front_left_drive
front_right_drive
back_left_drive
back_right_drive
```

The names in the Robot Controller configuration must exactly match the names used in the Java code.

## OpMode Summary

| OpMode | Type | Drive System | Main Purpose |
|---|---|---|---|
| `BasicMecanum` | TeleOp | Mecanum | Manual omnidirectional driving |
| `BasicTank` | TeleOp | Tank | Manual differential-style driving |
| `BasicMecanumWOdom` | Autonomous | Mecanum + Odometry | Automatic point-to-point navigation |

## Development Notes

These programs are intended to provide a basic foundation for the robot's drive and navigation systems.

Potential future improvements include:

- PID control
- Improved heading control
- More accurate odometry calibration
- Dedicated dead-wheel hardware
- Adjustable drive constants
- Improved target-point handling
- More advanced motion profiling
- Improved autonomous path planning
- Corrected and finalized tank-drive normalization
- Separate reusable drive and odometry classes

## General Workflow

The intended development progression is:

```text
BasicTank / BasicMecanum
        ↓
Test drivetrain
        ↓
Calibrate motors and encoders
        ↓
Test odometry
        ↓
BasicMecanumWOdom
        ↓
Test target points
        ↓
Tune tolerances and power curve
        ↓
Develop more advanced autonomous navigation
```

These programs provide the basic drive, control, and navigation foundation for further development of the team's FTC robot.